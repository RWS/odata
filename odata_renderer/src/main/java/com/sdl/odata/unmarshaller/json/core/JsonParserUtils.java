/*
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.unmarshaller.json.core;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.util.PrimitiveUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Json Parser Utils.
 */
public final class JsonParserUtils {

    private JsonParserUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(JsonParserUtils.class);

    public static StructuredType getStructuredType(String entityName, EntityDataModel edm) {
        Type type = edm.getType(entityName);
        if (isStructuredType(type)) {
            return (StructuredType) type;
        } else {
            return null;
        }
    }

    /**
     * Gets the properties of a structural type, including the properties of its base types (recursively).
     *
     * @param structuredType structuredType
     * @param entityDataModel The entity data model
     * @return listOfProperties
     * @throws ODataException If unable to get all properties from edm
     */
    public static List<StructuralProperty> getAllProperties(StructuredType structuredType,
                                                            EntityDataModel entityDataModel) throws ODataException {
        List<StructuralProperty> properties = new ArrayList<>();

        String baseTypeName = structuredType.getBaseTypeName();
        if (!isNullOrEmpty(baseTypeName)) {
            StructuredType baseType = (StructuredType) entityDataModel.getType(baseTypeName);
            if (baseType == null) {
                throw new ODataUnmarshallingException("OData type not found: " + baseTypeName);
            }

            // Get the properties of the base type
            properties.addAll(getAllProperties(baseType, entityDataModel));
        }

        properties.addAll(structuredType.getStructuralProperties());
        LOG.debug("Total number of properties returning are {} for given structured type {}",
                properties.size(), structuredType.getName());
        return properties;
    }

    public static Object getAppropriateFieldValue(Class<?> type, String fieldValue) throws ODataUnmarshallingException {
        Class<?> wrappedType = PrimitiveUtil.wrap(type);
        if (String.class.isAssignableFrom(wrappedType)) {
            return fieldValue;
        }
        if (wrappedType == byte[].class) {
            return Base64.getDecoder().decode(fieldValue);
        }
        if (UUID.class.isAssignableFrom(wrappedType)) {
            return UUID.fromString(fieldValue);
        }
        if (hasMethod(wrappedType, "parse", String.class)) {
            // Handle Java.Time types which have parse method
            try {
                return wrappedType.getMethod("parse", String.class).invoke(null, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new ODataUnmarshallingException("Could not invoke 'parse' method on " +
                        wrappedType.getCanonicalName() + " for " + fieldValue, e);
            }
        } else if (hasMethod(wrappedType, "valueOf", String.class)) {
            try {
                return wrappedType.getMethod("valueOf", String.class).invoke(null, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new ODataUnmarshallingException("Could not invoke 'valueOf' method on " +
                        wrappedType.getCanonicalName() + " for " + fieldValue, e);
            }
        } else if (wrappedType == ZonedDateTime.class) {
            return ZonedDateTime.parse(fieldValue);
        }
        return null;
    }

    public static void setFieldValue(Field field, Object entity, Object value) throws ODataUnmarshallingException {
        try {
            field.setAccessible(true);
            field.set(entity, value);
            LOG.trace("'{}' is set with '{}'", field.getName(), value);
        } catch (ReflectiveOperationException e) {
            throw new ODataUnmarshallingException("Cannot set field '" + field.getName() +
                    "' = '" + value + "'", e);
        }
    }

    protected static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method != null;
        } catch (NoSuchMethodException e) {
            LOG.warn("Method '{}' with parameter types {} is not found in {}",
                    methodName, parameterTypes, clazz.getCanonicalName());
        }
        return false;
    }

    private static boolean isStructuredType(Type type) {
        return type instanceof StructuredType;
    }
}
