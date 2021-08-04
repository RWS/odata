/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.processor.write.util;

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
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.ODataTargetTypeException;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import scala.Option;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.odata.api.ODataErrorCode.PROCESSOR_ERROR;
import static com.sdl.odata.api.parser.ODataUriUtil.asJavaMap;
import static com.sdl.odata.api.parser.ODataUriUtil.getEntityKeyMap;
import static com.sdl.odata.api.service.HeaderNames.LOCATION;
import static com.sdl.odata.util.edm.EntityDataModelUtil.formatEntityKey;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getEntitySetByEntity;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getPropertyValue;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * Utilization class for providing useful methods for Write Method Handlers.
 */
public final class WriteMethodUtil {

    private WriteMethodUtil() {
    }

    /**
     * The key/value pair specifying that the return-minimal is preferred by the client.
     */
    public static final String RETURN_MINIMAL = "return=minimal";

    public static TargetType getTargetType(ODataRequest request, EntityDataModel entityDataModel, ODataUri oDataUri)
            throws ODataTargetTypeException {
        Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(oDataUri, entityDataModel);
        if (targetTypeOption.isEmpty()) {
            throw new ODataTargetTypeException("The target type of this URI cannot be determined: "
                    + request.getUri());
        }
        return targetTypeOption.get();
    }

    public static DataSource getDataSource(ODataRequestContext requestContext, String entityType,
                                           DataSourceFactory dataSourceFactory) throws ODataDataSourceException {
        return dataSourceFactory.getDataSource(requestContext, entityType);
    }

    /**
     * Check whether the return-minimal is preferred by the HTTP client by inspecting the 'HTTP-Prefer' header.
     *
     * @return {@code true} if the return-minimal is preferred.
     */
    public static boolean isMinimalReturnPreferred(ODataRequest request) {
        return request.getPrefer().contains(RETURN_MINIMAL);
    }

    /**
     * Get the response headers when the response is to include an entity in its body.
     *
     * @param entity The entity that will be included in the body of the response.
     * @return The response headers.
     * @throws com.sdl.odata.api.edm.ODataEdmException In case it is not possible to build the headers.
     */
    public static Map<String, String> getResponseHeaders(Object entity, ODataUri oDataUri,
                                                         EntityDataModel entityDataModel) throws ODataEdmException {

        final Map<String, String> headers = new HashMap<>();
        headers.put(LOCATION, String.format("%s/%s(%s)", oDataUri.serviceRoot(),
                getEntitySetByEntity(entityDataModel, entity).getName(),
                formatEntityKey(entityDataModel, entity)));
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
    public static void validateKeys(Object entity, EntityType type, ODataUri oDataUri,
                                EntityDataModel entityDataModel) throws ODataClientException, ODataProcessorException {

        final Map<String, Object> oDataUriKeyValues = asJavaMap(getEntityKeyMap(oDataUri, entityDataModel));
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

    private static Map<String, Object> getKeyValues(Object entity, EntityType entityType)
            throws ODataProcessorException {

        Map<String, Object> keyValues = new HashMap<>();
        for (PropertyRef propertyRef : entityType.getKey().getPropertyRefs()) {
            try {
                Field keyField = entity.getClass().getDeclaredField(propertyRef.getPath());
                keyField.setAccessible(true);
                keyValues.put(propertyRef.getPath(), keyField.get(entity));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ODataProcessorException(PROCESSOR_ERROR,
                        "Not possible to extract the key/values from the entity", e);
            }
        }
        return keyValues;
    }

    private static Object normalize(Object value) {

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
    public static void validateTargetType(Object entity, ODataRequest request, EntityDataModel entityDataModel,
                                          ODataUri oDataUri) throws ODataProcessorException, ODataTargetTypeException {
        if (!entityDataModel.getType(
                entity.getClass()).getFullyQualifiedName().equals(getTargetType(request, entityDataModel,
                oDataUri).typeName())) {
            throw new ODataProcessorException(PROCESSOR_ERROR,
                    "Entity to persist does not match specified Resource name");
        }
    }

    /**
     * Checks if all non-nullable properties of an entity are non-empty.
     *
     * @param entity The entity to check.
     * @throws com.sdl.odata.api.ODataBadRequestException
     * If any of the non-nullable properties of the entity are empty.
     */
    public static void validateProperties(final Object entity, final EntityDataModel edm)
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
}
