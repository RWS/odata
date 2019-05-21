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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePath;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.model.ReferencableEntity;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.immutable.List$;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.api.parser.ODataUriUtil.extractEntityWithKeys;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * OData Common Parser is an abstract util class that contains the same operation methods.
 *
 */
public abstract class AbstractParser {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractParser.class);

    private final EntityDataModel entityDataModel;
    private final ODataRequest request;
    private final ODataUri oDataUri;
    private final ODataParser uriParser;

    public AbstractParser(ODataRequestContext context, ODataParser oDataParser) {
        this.entityDataModel = checkNotNull(context.getEntityDataModel());
        this.request = checkNotNull(context.getRequest());
        this.oDataUri = checkNotNull(context.getUri());
        this.uriParser = checkNotNull(oDataParser);
    }

    /**
     * Parse the payload to obtain a single entity (entry).
     *
     * @return The single entity (entry) contained by the payload.
     * @throws ODataException In case of a parsing or validation error
     */
    public Object getODataEntity() throws ODataException {
        final String bodyText = getBodyText();
        LOG.trace("Text of the body is {}", bodyText);
        if (!isNullOrEmpty(bodyText)) {
            return processEntity(bodyText);
        } else {
            throw new ODataUnmarshallingException("Payload is empty. Expected an entry.");
        }
    }

    /**
     * Parse the payload to obtain a list of entities (feed).
     *
     * @return The list of entities (feed) contained by the payload.
     * @throws ODataException In case of a parsing or validation error
     */
    public List<?> getODataEntities() throws ODataException {
        final String bodyText = getBodyText();
        LOG.trace("Text of the body is {}", bodyText);
        if (!isNullOrEmpty(bodyText)) {
            return processEntities(bodyText);
        } else {
            throw new ODataUnmarshallingException("Payload is empty. Expected a feed.");
        }
    }

    /**
     * Process entity by given text.
     *
     * @param bodyText represents single entity and this will never be null here.
     * @return Object that represents entity by unmarshalling.
     * @throws ODataException in case of invalid body text.
     */
    protected abstract Object processEntity(String bodyText) throws ODataException;

    /**
     * Process the entities (feed) contained by the given XML payload.
     *
     * @param bodyText The given payload.
     * @return The process entities.
     * @throws ODataException If unable to process entities
     */
    protected abstract List<?> processEntities(String bodyText) throws ODataException;

    protected String getBodyText() {
        try {
            return request.getBodyText(UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException(e);
        }
    }

    protected Type getType(String entityName) {
        return entityDataModel.getType(entityName);
    }

    protected ODataRequest getRequest() {
        return request;
    }

    protected ODataUri getODataUri() {
        return oDataUri;
    }

    protected TargetType getTargetType() {
        Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(oDataUri, entityDataModel);
        if (targetTypeOption.isDefined()) {
            return targetTypeOption.get();
        }
        return null;
    }

    /**
     * Check if the parser is being used in the context of a 'write operation'.
     *
     * @return {@code true} if it is used in the context of a 'write operation', {@code false} otherwise.
     */
    protected boolean isWriteOperation() {
        return !GET.equals(request.getMethod());
    }

    /**
     * Get the referenced entity for a given 'resource path' (entity id) and a 'property name'.
     *
     * @param entityIdResourcePath The given 'resource path' (entity id).
     * @param propertyName         The given 'property name'.
     * @return The referenced entity.
     * @throws ODataException if unable to determine referenced entity
     */
    protected Object getReferencedEntity(String entityIdResourcePath, String propertyName) throws ODataException {

        LOG.debug("getReferencedEntity: {}", entityIdResourcePath);

        ResourcePath resourcePath = uriParser.parseResourcePath(entityIdResourcePath, entityDataModel);
        ODataUri referencedEntityUri = new ODataUri("", new ResourcePathUri(resourcePath,
                List$.MODULE$.<QueryOption>empty()));

        Option<Object> opt = extractEntityWithKeys(referencedEntityUri, entityDataModel);
        if (!opt.isDefined()) {
            throw new ODataUnmarshallingException("Cannot determine referenced entity for navigation link " +
                    "for property: " + propertyName + ", href=\"" + entityIdResourcePath + "\"");
        }
        Object result = opt.get();
        if(result instanceof ReferencableEntity)
        {
            ReferencableEntity referencedEntity = ((ReferencableEntity)result);
            referencedEntity.setReferenceString(entityIdResourcePath);
        }

        return result;
    }

    /**
     * Save the given referenced entity as an expanded linked entity in the given main entity.
     *
     * @param entity           The given main entity.
     * @param propertyName     The name of the navigation property in the main entity.
     * @param property         The navigation property.
     * @param referencedEntity The referenced entity to save as an expanded linked entity in the given main entity.
     * @throws ODataUnmarshallingException if unable to get or set navigation property
     */
    protected void saveReferencedEntity(Object entity, String propertyName, StructuralProperty property,
                                        Object referencedEntity) throws ODataUnmarshallingException {
        // Save the referenced entity in the entity we are unmarshalling
        Class<?> type =  EntityDataModelUtil.getPropertyJavaType(property);

        try {
            if (Collection.class.isAssignableFrom(type)) {
                saveReferencedEntityCollectionField(entity, referencedEntity, property);
            } else {
                EntityDataModelUtil.setPropertyValue(property, entity, referencedEntity);
            }
        } catch (IllegalAccessException e) {
            throw new ODataUnmarshallingException("Error while getting or setting navigation property field " +
                    propertyName, e);
        }

        // NOTE: Check if the element has a <metadata:inline> element (expanded navigation property)
        // for this, see 11.4.2.2 Create Related Entities When Creating an Entity
    }

    private void saveReferencedEntityCollectionField(Object entity, Object referencedEntity,
                                              StructuralProperty property) throws IllegalAccessException {
        Collection container = (Collection) EntityDataModelUtil.getPropertyValue(property, entity);
        if(container == null)
        {
            container = EntityDataModelUtil.createPropertyCollection(property);
            EntityDataModelUtil.setPropertyValue(property, entity, container);
        }
        container.add(referencedEntity);
    }

    public EntityDataModel getEntityDataModel() {
        return entityDataModel;
    }
}
