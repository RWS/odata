/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.edm.model.TypeNameResolver;
import com.sdl.odata.edm.model.ComplexTypeImpl;

import java.lang.reflect.Modifier;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Complex Type Factory.
 */
final class AnnotationComplexTypeFactory extends AnnotationStructuredTypeFactory<ComplexType> {

    AnnotationComplexTypeFactory(TypeNameResolver typeNameResolver) {
        super(typeNameResolver);
    }

    @Override
    public ComplexType build(Class<?> cls) {
        EdmComplex complexAnno = cls.getAnnotation(EdmComplex.class);

        // Base type
        String baseTypeName = null;
        Class<?> superClass = cls.getSuperclass();
        EdmComplex superComplexAnno = superClass.getAnnotation(EdmComplex.class);
        if (superComplexAnno != null) {
            baseTypeName = getFullyQualifiedTypeName(superComplexAnno, superClass);
        }

        return new ComplexTypeImpl.Builder()
                .setName(getTypeName(complexAnno, cls))
                .setNamespace(getNamespace(complexAnno, cls))
                .setJavaType(cls)
                .setBaseTypeName(baseTypeName)
                .setIsAbstract(Modifier.isAbstract(cls.getModifiers()))
                .addStructuralProperties(buildStructuralProperties(cls))
                .setIsOpen(complexAnno.open())
                .build();
    }

    private static String getTypeName(EdmComplex complexAnno, Class<?> complexClass) {
        String name = complexAnno.name();
        if (isNullOrEmpty(name)) {
            // Use class name if name is not specified in EdmComplex annotation
            name = complexClass.getSimpleName();
        }
        return name;
    }

    private static String getNamespace(EdmComplex complexAnno, Class<?> complexClass) {
        String namespace = complexAnno.namespace();
        if (isNullOrEmpty(namespace)) {
            // Use package name if namespace is not specified in EdmComplex annotation
            namespace = complexClass.getPackage().getName();
        }
        return namespace;
    }

    public static String getFullyQualifiedTypeName(EdmComplex complexAnno, Class<?> complexClass) {
        String name = getTypeName(complexAnno, complexClass);
        String namespace = getNamespace(complexAnno, complexClass);
        return namespace + "." + name;
    }
}
