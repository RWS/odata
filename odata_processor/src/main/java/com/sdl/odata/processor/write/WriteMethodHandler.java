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
package com.sdl.odata.processor.write;

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataClientException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.PropertyRef;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.ODataProcessorException;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.ODataTargetTypeException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.processor.ProcessorConfiguration;
import com.sdl.odata.processor.write.util.WriteMethodUtil;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import scala.Option;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.odata.api.ODataErrorCode.PROCESSOR_ERROR;
import static com.sdl.odata.api.parser.ODataUriUtil.asJavaMap;
import static com.sdl.odata.api.parser.ODataUriUtil.getEntityKeyMap;
import static com.sdl.odata.api.service.HeaderNames.*;
import static com.sdl.odata.util.edm.EntityDataModelUtil.formatEntityKey;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getEntitySetByEntity;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getPropertyValue;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * This is abstract method handler for write operation which is PUT, POST, PATCH and DELETE.
 *
 */
public abstract class WriteMethodHandler {

    private final ODataRequest request;
    private final EntityDataModel entityDataModel;
    private final ODataUri oDataUri;
    private final DataSourceFactory dataSourceFactory;
    private final ODataRequestContext requestContext;
    private final ProcessorConfiguration configuration;

    public WriteMethodHandler(ODataRequestContext requestContext, DataSourceFactory dataSourceFactory, ProcessorConfiguration configuration) {
        this.oDataUri = checkNotNull(requestContext.getUri());
        this.request = checkNotNull(requestContext.getRequest());
        this.entityDataModel = checkNotNull(requestContext.getEntityDataModel());
        this.dataSourceFactory = checkNotNull(dataSourceFactory);
        this.requestContext = requestContext;
        this.configuration = checkNotNull(configuration);
    }

    public abstract ProcessorResult handleWrite(Object entity) throws ODataException;

    protected TargetType getTargetType() throws ODataTargetTypeException {
        Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(getoDataUri(), getEntityDataModel());
        if (targetTypeOption.isEmpty()) {
            throw new ODataTargetTypeException("The target type of this URI cannot be determined: "
                    + getRequest().getUri());
        }
        return targetTypeOption.get();
    }

    protected DataSource getDataSource(String entityType) throws ODataDataSourceException {
        return dataSourceFactory.getDataSource(requestContext, entityType);
    }

    /**
     * Check whether the return-minimal is preferred by the HTTP client by inspecting the 'HTTP-Prefer' header.
     *
     * @return {@code true} if the return-minimal is preferred.
     */
    protected boolean isMinimalReturnPreferred() {
        return getRequest().getPrefer().contains(WriteMethodUtil.RETURN_MINIMAL);
    }

    /**
     * Get the response headers when the response is to include an entity in its body.
     *
     * @param entity The entity that will be included in the body of the response.
     * @return The response headers.
     * @throws ODataEdmException In case it is not possible to build the headers.
     */
    protected Map<String, String> getResponseHeaders(Object entity) throws ODataEdmException {

        final Map<String, String> headers = new HashMap<>();
        headers.put(LOCATION, String.format("%s/%s(%s)", getoDataUri().serviceRoot(),
                getEntitySetByEntity(getEntityDataModel(), entity).getName(),
                formatEntityKey(getEntityDataModel(), entity)));
        if(isMinimalReturnPreferred())
        {
            headers.put(ODATA_ENTITY_ID, EntityDataModelUtil.getKeyPropertyValues(((EntityType) getEntityDataModel().getType(entity.getClass())), entity).values().iterator().next().toString());
            headers.put(PREFERENCE_APPLIED, WriteMethodUtil.RETURN_MINIMAL);
        }
        return headers;
    }

    /**
     * Performs the validation of keys.
     * The key(s) in the 'OData URI' should match the existing key(s) in the passed entity.
     *
     * @param entity The passed entity.
     * @param type   The entity type of the passed entity.
     * @throws com.sdl.odata.api.ODataClientException
     * @throws com.sdl.odata.api.processor.ODataProcessorException
     */
    protected void validateKeys(Object entity, EntityType type) throws ODataClientException, ODataProcessorException {

        final Map<String, Object> oDataUriKeyValues = asJavaMap(getEntityKeyMap(getoDataUri(), getEntityDataModel()));
        if(!Boolean.TRUE.equals(configuration.getUpdateRequireId())) {
            for(Map.Entry<String, Object> keyEntry: oDataUriKeyValues.entrySet())
            {
                EntityDataModelUtil.setPropertyValue(type.getStructuralProperty(keyEntry.getKey()), entity, keyEntry.getValue());
            }
            return;
        }


        final Map<String, Object> keyValues = getKeyValues(entity, type);

        if (oDataUriKeyValues.size() != keyValues.size()) {
            throw new ODataClientException(PROCESSOR_ERROR, "Number of keys don't match");
        }

        for (Map.Entry<String, Object> oDataUriEntry : oDataUriKeyValues.entrySet()) {
            String oDataUriKey = oDataUriEntry.getKey();
            Object value = keyValues.get(oDataUriKey);
            if (value == null || !normalize(value).equals(normalize(oDataUriEntry.getValue()))) {
                throw new ODataClientException(PROCESSOR_ERROR, "Key/Values in OData URI and the entity don't match");
            }
        }
    }

    private Map<String, Object> getKeyValues(Object entity, EntityType entityType) throws ODataProcessorException {

        Map<String, Object> keyValues = new HashMap<>();
        for (PropertyRef propertyRef : entityType.getKey().getPropertyRefs()) {
            try {
                String keyFieldName = entityType.getStructuralProperty(propertyRef.getPath()).getJavaField().getName();
                Field keyField = entity.getClass().getDeclaredField(keyFieldName);
                keyField.setAccessible(true);
                keyValues.put(keyFieldName, keyField.get(entity));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ODataProcessorException(PROCESSOR_ERROR,
                        "Not possible to extract the key/values from the entity", e);
            }
        }
        return keyValues;
    }

    private Object normalize(Object value) {

        if (value instanceof Long) {
            return new BigDecimal((Long) value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        } else if (value instanceof Short) {
            return new BigDecimal((Short) value);
        } else if (value instanceof Byte) {
            return new BigDecimal((Byte) value);
        } else if (value instanceof scala.math.BigDecimal) {
            // Convert it to a Java BigDecimal
            return ((scala.math.BigDecimal) value).bigDecimal();
        }

        return value;
    }

    /**
     * Validates the target type.
     *
     * @param entity an entity
     * @throws com.sdl.odata.api.processor.ODataProcessorException
     * @throws com.sdl.odata.api.processor.datasource.ODataTargetTypeException
     */
    protected void validateTargetType(Object entity) throws ODataProcessorException, ODataTargetTypeException {
        if (!getEntityDataModel().getType(
                entity.getClass()).getFullyQualifiedName().equals(getTargetType().typeName())) {
            throw new ODataProcessorException(PROCESSOR_ERROR,
                    "Entity to persist does not match specified Resource name");
        }
    }

    /**
     * Checks if all non-nullable properties of an entity are non-empty.
     *
     * @param entity The entity to check.
     * @throws ODataBadRequestException If any of the non-nullable properties of the entity are empty.
     */
    protected void validateProperties(final Object entity, final EntityDataModel edm)
            throws ODataException {
        final Type type = edm.getType(entity.getClass());
        // No validation needed if it is not a structured type
        if (!(type instanceof StructuredType)) {
            return;
        }

        visitProperties(edm, (StructuredType) type, property -> {
            Object value = getPropertyValue(property, entity);
            if (value == null) {
                if (!property.isNullable()) {
                    throw new ODataBadRequestException("The property '" + property.getName() +
                            "' is required to be non-empty in an entity of type: " + type.getFullyQualifiedName());
                }
            } else if (!(property instanceof NavigationProperty)) {
                // Validate contained properties, but not if the property is a navigation property.
                // Navigation properties in a request are only links, they don't contain the entity that the
                // navigation property points to itself.
                validateProperties(value, edm);
            }
        });
    }

    public ODataRequest getRequest() {
        return request;
    }

    public EntityDataModel getEntityDataModel() {
        return entityDataModel;
    }

    public ODataRequestContext getODataRequestContext() {
        return this.requestContext;
    }

    public ODataUri getoDataUri() {
        return oDataUri;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }
        return reference;
    }
}
