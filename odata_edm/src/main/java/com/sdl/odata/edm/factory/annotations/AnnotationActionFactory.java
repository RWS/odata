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

import com.sdl.odata.api.edm.annotations.EdmAction;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.Parameter;
import com.sdl.odata.edm.model.ActionImpl;
import com.sdl.odata.edm.model.ParameterImpl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Action Factory.
 */
public class AnnotationActionFactory {

    /**
     * Builds an action instance from given class.
     *
     * @param cls action class.
     * @return instance of action.
     */
    public Action build(Class<?> cls) {
        EdmAction edmAction = cls.getAnnotation(EdmAction.class);
        EdmReturnType edmReturnType = cls.getAnnotation(EdmReturnType.class);
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

        return new ActionImpl.Builder()
                .setName(getTypeName(edmAction, cls))
                .setNamespace(getNamespace(edmAction, cls))
                .setBound(edmAction.isBound())
                .setEntitySetPath(edmAction.entitySetPath())
                .setParameters(parameters)
                .setReturnType(edmReturnType.type())
                .setJavaClass(cls)
                .build();
    }

    private static String getTypeName(EdmAction actionAnnotation, Class<?> actionClass) {
        String name = actionAnnotation.name();
        if (isNullOrEmpty(name)) {
            // Use class name if name is not specified in EdmAction annotation
            name = actionClass.getSimpleName();
        }
        return name;
    }

    private static String getNamespace(EdmAction actionAnnotation, Class<?> actionClass) {
        String namespace = actionAnnotation.namespace();
        if (isNullOrEmpty(namespace)) {
            // Use package name if namespace is not specified in EdmAction annotation
            namespace = actionClass.getPackage().getName();
        }
        return namespace;
    }
}
