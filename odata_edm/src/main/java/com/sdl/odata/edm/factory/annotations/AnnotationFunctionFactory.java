/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmFunction;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Parameter;
import com.sdl.odata.edm.model.ParameterImpl;
import com.sdl.odata.edm.model.FunctionImpl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Function Factory.
 */
public class AnnotationFunctionFactory {

    /**
     * Method to build Function implementation with parameters by class.
     * @param cls   function class
     * @return      built function
     */
    public Function build(Class<?> cls) {
        EdmFunction edmFunction = cls.getAnnotation(EdmFunction.class);
        EdmReturnType edmReturnType = cls.getAnnotation(EdmReturnType.class);
        if (edmReturnType == null) {
            throw new IllegalArgumentException(
                    "The class must have EdmReturnType: " + cls.getName());
        }
        Set<Parameter> parameters = new HashSet<>();
        for (Field field : cls.getDeclaredFields()) {
            EdmParameter parameterAnnotation = field.getAnnotation(EdmParameter.class);
            if (parameterAnnotation != null) {
                String parameterName = isNullOrEmpty(parameterAnnotation.name())
                        ? field.getName() : parameterAnnotation.name();
                String parameterType = isNullOrEmpty(parameterAnnotation.type())
                        ? field.getType().getSimpleName() : parameterAnnotation.type();
                parameters.add(new ParameterImpl.Builder()
                        .setMaxLength(parameterAnnotation.maxLength())
                        .setName(parameterName)
                        .setNullable(parameterAnnotation.nullable())
                        .setPrecision(parameterAnnotation.precision())
                        .setScale(parameterAnnotation.scale())
                        .setSRID(parameterAnnotation.srid())
                        .setType(parameterType)
                        .setUnicode(parameterAnnotation.unicode())
                        .setJavaField(field)
                        .build());
            }
        }

        return new FunctionImpl.Builder()
                .setBound(edmFunction.isBound())
                .setComposable(edmFunction.isComposable())
                .setEntitySetPath(edmFunction.entitySetPath())
                .setName(edmFunction.name())
                .setParameters(parameters)
                .setReturnType(edmReturnType.type())
                .setNamespace(edmFunction.namespace())
                .setJavaClass(cls)
                .build();
    }

    private static String getTypeName(EdmFunction functionAnnotation, Class<?> functionClass) {
        String name = functionAnnotation.name();
        if (isNullOrEmpty(name)) {
            // Use class name if name is not specified in EdmFunction annotation
            name = functionClass.getSimpleName();
        }
        return name;
    }

    private static String getNamespace(EdmFunction functionAnnotation, Class<?> functionClass) {
        String namespace = functionAnnotation.namespace();
        if (isNullOrEmpty(namespace)) {
            // Use package name if namespace is not specified in EdmFunction annotation
            namespace = functionClass.getPackage().getName();
        }
        return namespace;
    }

    /**
     * Returned fully qualified function name using function annotation and class.
     * @param functionAnnotation    function annotation
     * @param functionClass         function class
     * @return                      fully qualified function name
     */
    public static String getFullyQualifiedFunctionName(EdmFunction functionAnnotation, Class<?> functionClass) {
        String name = getTypeName(functionAnnotation, functionClass);
        String namespace = getNamespace(functionAnnotation, functionClass);
        return namespace + "." + name;
    }
}
