/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.api.parser.util;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.util.PrimitiveUtil;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Utility class for Parameter parsing.
 */
public final class ParameterTypeUtil {
    private static final PrimitiveTypeNameResolver PRIMITIVE_TYPE_NAME_RESOLVER = new PrimitiveTypeNameResolver();

    private ParameterTypeUtil() {
    }

    public static void setParameter(Object object, Field field, Object bodyParameter)
            throws ODataUnmarshallingException {
        Object fieldValue = null;
        if (bodyParameter != null) {
            EdmParameter annotation = field.getAnnotation(EdmParameter.class);
            if (annotation == null) {
                throw new ODataUnmarshallingException("Field should have EdmParameter annotation");
            }

            PrimitiveType type = resolveType(annotation.type(), field);

            try {
                if (field.getType().isAssignableFrom(bodyParameter.getClass())) {
                    fieldValue = bodyParameter;
                } else if (type != null) {
                    fieldValue = ParserUtil.parsePrimitiveValue(bodyParameter.toString(), type);
                }
            } catch (ODataException e) {
                throw new ODataUnmarshallingException("Can't parse primitive value");
            }
        }
        field.setAccessible(true);

        try {
            field.set(object, fieldValue);
        } catch (IllegalAccessException e) {
            throw new ODataUnmarshallingException("Error during setting a parameter to action object field");
        }

    }

    private static PrimitiveType resolveType(String type, Field field) {
        if (isNullOrEmpty(type)) {
            return PRIMITIVE_TYPE_NAME_RESOLVER.resolveTypeName(field.getType());
        }

        return PrimitiveType.forName(type);
    }

    /**
     * Primitive Type resolver.
     */
    private static class PrimitiveTypeNameResolver {
        private Map<Class<?>, PrimitiveType> javaToPrimitiveType;

        PrimitiveTypeNameResolver() {
            Map<Class<?>, PrimitiveType> javaToPrimitiveTypeBuilder = new HashMap<>();
            for (PrimitiveType primitiveType : PrimitiveType.values()) {
                Class<?> javaType = primitiveType.getJavaType();
                if (javaType != null) {
                    javaToPrimitiveTypeBuilder.put(javaType, primitiveType);
                }
            }
            javaToPrimitiveType = Collections.unmodifiableMap(javaToPrimitiveTypeBuilder);
        }

        public PrimitiveType resolveTypeName(Class<?> javaType) {
            return javaToPrimitiveType.get(PrimitiveUtil.unwrap(javaType));
        }

    }
}
