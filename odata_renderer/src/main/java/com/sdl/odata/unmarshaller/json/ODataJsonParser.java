/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.unmarshaller.json;

import com.sdl.odata.api.parser.ODataPatchInfo;
import com.sdl.odata.unmarshaller.AbstractParser;
import com.sdl.odata.unmarshaller.json.core.JsonNullableValidator;
import com.sdl.odata.unmarshaller.json.core.JsonParserUtils;
import com.sdl.odata.unmarshaller.json.core.JsonProcessor;
import com.sdl.odata.JsonConstants;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataNotImplementedException;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.unmarshaller.json.core.JsonPropertyExpander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The OData Json Parser.
 */
public class ODataJsonParser extends AbstractParser {
    private static final Logger LOG = LoggerFactory.getLogger(ODataJsonParser.class);

    private Map<String, Object> fields;
    private Map<String, String> odataValues;
    private Map<String, Object> links;

    public ODataJsonParser(ODataRequestContext request, ODataParser uriParser) {
        super(request, uriParser);
    }

    /**
     * Initialize processor ready for for unmarshalling entity.
     *
     * @param processor the jsonProcessor
     * @throws ODataUnmarshallingException If unable to initialize processor
     */
    private void initializeProcessor(JsonProcessor processor) throws ODataUnmarshallingException {
        LOG.info("Trying to initialize processor: {}", processor.getClass().getSimpleName());
        processor.initialize();

        fields = processor.getValues();
        odataValues = processor.getODataValues();
        links = processor.getLinks();

        context.setProperty(new ODataPatchInfo(fields, odataValues, links));
    }

    @Override
    protected Object processEntity(String bodyText) throws ODataException {
        initializeProcessor(new JsonProcessor(bodyText));

        JsonPropertyExpander expander = new JsonPropertyExpander(getEntityDataModel());

        String entityName = getEntityName();
        Object entity = expander.loadEntity(entityName);


        StructuredType entityType = JsonParserUtils.getStructuredType(entityName, getEntityDataModel());

        if (getRequest().getMethod() == ODataRequest.Method.POST) {
            JsonNullableValidator validator = new JsonNullableValidator(fields, links);
            validator.ensureCollection(entityType);
            validator.ensureNavigationProperties(entityType);
        }

        expander.setEntityProperties(entity, entityType, fields, null);
        setEntityNavigationProperties(entity, JsonParserUtils.getStructuredType(entityName, getEntityDataModel()));

        return entity;
    }

    /**
     * @param bodyText The given json payload.
     * @return processed entities
     * @throws ODataException not implemented, will always be thrown
     */
    @Override
    protected List<?> processEntities(String bodyText) throws ODataException {
        throw new ODataNotImplementedException("Unmarshalling a feed using JSON is not supported.");
    }


    /**
     * Gets the entity type name.
     *
     * @return the entity type
     * @throws ODataUnmarshallingException
     */
    private String getEntityName() throws ODataUnmarshallingException {
        String odataType = odataValues.get(JsonConstants.TYPE);
        if (isNullOrEmpty(odataType)) {
            TargetType targetType = getTargetType();
            if (targetType == null) {
                throw new ODataUnmarshallingException("Could not find entity name");
            }
            return targetType.typeName();
        } else {
            if (odataType.startsWith("#")) {
                odataType = odataType.substring(1);
            }
            return odataType;
        }
    }


    /**
     * Sets the given entity with navigation links.
     *
     * @param entity     entity
     * @param entityType the entity type
     * @throws ODataException If unable to set navigation properties
     */
    protected void setEntityNavigationProperties(Object entity, StructuredType entityType) throws ODataException {
        for (Map.Entry<String, Object> entry : links.entrySet()) {
            String propertyName = entry.getKey();
            Object entryLinks = entry.getValue();
            LOG.debug("Found link for navigation property: {}", propertyName);

            StructuralProperty property = entityType.getStructuralProperty(propertyName);
            if (!(property instanceof NavigationProperty)) {
                throw new ODataUnmarshallingException("The request contains a navigation link '" + propertyName +
                        "' but the entity type '" + entityType + "' does not contain a navigation property " +
                        "with this name.");
            }

            // Note: The links are processed a bit differently depending on whether we are parsing in the context
            // of a 'write operation' or 'read operation'. Only in the case of a 'write operation' it is necessary
            // to resolve the referenced entity.
            if (isWriteOperation()) {
                // Get the referenced entity(es), but only with the key fields filled in
                if (entryLinks instanceof List) {
                    List<String> linksList = (List<String>) entryLinks;
                    for (String link : linksList) {
                        Object referencedEntity = getReferencedEntity(link, propertyName);
                        LOG.debug("Referenced entity: {}", referencedEntity);
                        saveReferencedEntity(entity, propertyName, property, referencedEntity);
                    }
                } else {
                    Object referencedEntity = getReferencedEntity((String) entryLinks, propertyName);
                    LOG.debug("Referenced entity: {}", referencedEntity);
                    saveReferencedEntity(entity, propertyName, property, referencedEntity);
                }
            }
        }
    }
}
