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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.odata.api.edm.model.*;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.unmarshaller.AbstractParser;
import com.sdl.odata.unmarshaller.json.core.JsonNullableValidator;
import com.sdl.odata.unmarshaller.json.core.JsonParserUtils;
import com.sdl.odata.unmarshaller.json.core.JsonProcessor;
import com.sdl.odata.JsonConstants;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataNotImplementedException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.unmarshaller.json.core.JsonPropertyExpander;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The OData Json Parser.
 */
public class ODataJsonParser extends AbstractParser {
    private static final Logger LOG = LoggerFactory.getLogger(ODataJsonParser.class);

    private Map<String, Object> jsonObject;

    public ODataJsonParser(ODataRequestContext request, ODataParser uriParser) {
        super(request, uriParser);
    }

    @Override
    protected Object processEntity(String bodyText) throws ODataException {

        try {
            jsonObject = new ObjectMapper().readValue(bodyText, Map.class);
        }
        catch (IOException e)
        {
            LOG.error("Error while deserialising JSON payload", e);
            throw new ODataUnmarshallingException("Error while deserialising JSON payload", e);
        }
        Map<String, Object> fields = objectProperties(jsonObject);

        JsonPropertyExpander expander = new JsonPropertyExpander(getEntityDataModel());

        String entityName = getEntityName(jsonObject);
        Object entity = expander.loadEntity(entityName);


        StructuredType entityType = JsonParserUtils.getStructuredType(entityName, getEntityDataModel());

        if (getRequest().getMethod() == ODataRequest.Method.POST) {
            JsonNullableValidator validator = new JsonNullableValidator(jsonObject);
            validator.ensureCollection(entityType);
            validator.ensureNavigationProperties(entityType);
        }

        expander.setEntityProperties(entity, entityType, jsonObject, null);
        setEntityNavigationProperties(entity, jsonObject, JsonParserUtils.getStructuredType(entityName, getEntityDataModel()));

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
    private String getEntityName(Map<String, Object> jsonObject) throws ODataUnmarshallingException {
        Map<String, Object> odataValues = odataProperties(jsonObject);
        Object odataTypeO = odataValues.get(JsonConstants.TYPE);
        String odataType = odataTypeO  == null ? null : (String) odataTypeO;
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
            return odataType.toString();
        }
    }


    /**
     * Sets the given entity with navigation links.
     *
     * @param entity     entity
     * @param entityType the entity type
     * @throws ODataException If unable to set navigation properties
     */
    protected void setEntityNavigationProperties(Object entity, Map<String, Object> jsonObject, StructuredType entityType) throws ODataException {
        Map<String, Object> links = links(jsonObject);
        for (Map.Entry<String, Object> linkEntry : links.entrySet()) {
            String propertyName = linkEntry.getKey();
            Object entryLinks = linkEntry.getValue();
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
                if (entryLinks instanceof Iterable) {
                    for (String link : (Iterable<String>)entryLinks) {
                        Object referencedEntity = getReferencedEntity(link, propertyName);
                        LOG.debug("Referenced entity item: {}", referencedEntity);
                        saveReferencedEntity(entity, propertyName, property, referencedEntity);
                    }
                } else {
                    Object referencedEntity = getReferencedEntity((String) entryLinks, propertyName);
                    LOG.debug("Referenced entity: {}", referencedEntity);
                    saveReferencedEntity(entity, propertyName, property, referencedEntity);
                }
            }
        }
        Map<String, Object> fields = objectProperties(jsonObject);
        for (Map.Entry<String, Object> fieldEntry : fields.entrySet()) {
            String propertyName = fieldEntry.getKey();
            StructuralProperty property = entityType.getStructuralProperty(propertyName);
            if (property instanceof NavigationProperty) {
                if(property.isCollection())
                {
                    Collection subEntities = (Collection) EntityDataModelUtil.getPropertyValue(property, entity);
                    List<Map<String, Object>> subJsonObjects = (List<Map<String, Object>>)jsonObject.get(propertyName);
                    EntityType subEntityType = (EntityType) getEntityDataModel().getType(property.getElementTypeName());

                    int i =0;
                    for(Object subEntity: subEntities)
                    {
                        Map<String, Object> subJsonObject = subJsonObjects.get(i);
                        setEntityNavigationProperties(subEntity, subJsonObject, subEntityType);
                        i++;
                    }
                }
                else {
                    Object subEntity = EntityDataModelUtil.getPropertyValue(property, entity);
                    Map<String, Object> subJsonObject = (Map<String, Object>)jsonObject.get(propertyName);
                    EntityType subEntityType = (EntityType) getEntityDataModel().getType(property.getTypeName());
                    setEntityNavigationProperties(subEntity, subJsonObject, subEntityType);
                }
            }
        }
    }

    public static <K, V> Map<K, V> subMap(Map<K, V> jsonObject, Function<K, Boolean> keyPredicate)
    {
        if(jsonObject == null)
            return null;

        Map<K, V> subMap = new HashMap<>();
        for(Map.Entry<K, V> entry: jsonObject.entrySet())
        {
            if(keyPredicate.apply(entry.getKey()))
            {
                subMap.put(entry.getKey(), entry.getValue());
            }
        }
        return subMap;
    }

    public static Map<String, Object> objectProperties(Map<String, Object> jsonObject)
    {
        return subMap(jsonObject, (k -> k == null || (!k.startsWith(JsonProcessor.ODATA) && !k.endsWith(JsonProcessor.ODATA_BIND))));
    }

    public static Map<String, Object> odataProperties(Map<String, Object> jsonObject)
    {
        return subMap(jsonObject, (k -> k != null && k.startsWith(JsonProcessor.ODATA)));
    }

    public static Map<String, Object> links(Map<String, Object> jsonObject)
    {
        Map<String, Object> subMap = subMap(jsonObject, (k -> k != null && k.endsWith(JsonProcessor.ODATA_BIND)));
        for(String key: new ArrayList<>(subMap.keySet()))
        {
            Object value = subMap.get(key);
            if(value instanceof String)
            {
                value = processLink((String) value);
                String valueS = (String)value;
                if(valueS.contains("/"))
                    value = valueS.substring(valueS.lastIndexOf("/") + 1);
            }
            else if(value instanceof Collection)
            {
                Collection values = (Collection)value;
                for(Object val : new ArrayList<>(values))
                {
                    if(val instanceof String)
                    {
                        values.remove(val);
                        values.add(processLink((String)val));
                    }

                }
            }
            subMap.remove(key);
            subMap.put(key.substring(0, key.indexOf(JsonProcessor.ODATA_BIND)), value);
        }
        return subMap;
    }

    public static String processLink(String link)
    {
        if(link != null && link.contains("/"))
            return link.substring(link.lastIndexOf("/") + 1);
        return link;
    }
}
