/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.util.PrimitiveUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link TypeNameResolver} that resolves Java types to the
 * corresponding OData primitive types defined in {@link com.sdl.odata.api.edm.model.PrimitiveType}.
 */
public final class PrimitiveTypeNameResolver implements TypeNameResolver {

    // Maps Java types as defined in PrimitiveType to PrimitiveType values
    private static final Map<Class<?>, PrimitiveType> JAVA_TO_PRIMITIVE_TYPE;

    static {
        Map<Class<?>, PrimitiveType> javaToPrimitiveTypeBuilder = new HashMap<>();
        for (PrimitiveType primitiveType : PrimitiveType.values()) {
            Class<?> javaType = primitiveType.getJavaType();
            if (javaType != null) {
                javaToPrimitiveTypeBuilder.put(javaType, primitiveType);
            }
        }
        JAVA_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(javaToPrimitiveTypeBuilder);
    }

    @Override
    public String resolveTypeName(Class<?> javaType) {
        // NOTE: PrimitiveUtil.unwrap() is used here so that Java wrapper types such as java.lang.Integer
        // will map to the OData primitive type that corresponds to the wrapper's Java primitive type.
        PrimitiveType primitiveType = JAVA_TO_PRIMITIVE_TYPE.get(PrimitiveUtil.unwrap(javaType));
        return primitiveType != null ? primitiveType.getFullyQualifiedName() : null;
    }
}
