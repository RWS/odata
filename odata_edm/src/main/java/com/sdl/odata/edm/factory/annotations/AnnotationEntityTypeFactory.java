/**
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmPropertyRef;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.PropertyRef;
import com.sdl.odata.edm.model.EntityTypeImpl;
import com.sdl.odata.edm.model.KeyImpl;
import com.sdl.odata.edm.model.PropertyRefImpl;
import com.sdl.odata.edm.model.TypeNameResolver;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Entity Type Factory.
 */
final class AnnotationEntityTypeFactory extends AnnotationStructuredTypeFactory<EntityType> {

    AnnotationEntityTypeFactory(TypeNameResolver typeNameResolver) {
        super(typeNameResolver);
    }

    @Override
    public EntityType build(Class<?> cls) {
        EdmEntity entityAnno = cls.getAnnotation(EdmEntity.class);

        // Base type
        String baseTypeName = null;
        Class<?> superClass = cls.getSuperclass();
        EdmEntity superEntityAnno = superClass.getAnnotation(EdmEntity.class);
        if (superEntityAnno != null) {
            baseTypeName = getFullyQualifiedTypeName(superEntityAnno, superClass);
        }

        // Key
        List<PropertyRef> propertyRefBuilder = new ArrayList<>();
        String[] keyPropertyNames = entityAnno.key();
        if (keyPropertyNames.length > 0) {
            for (String keyPropertyName : keyPropertyNames) {
                propertyRefBuilder.add(new PropertyRefImpl(keyPropertyName));
            }
        } else {
            for (EdmPropertyRef propertyRefAnno : entityAnno.keyRef()) {
                propertyRefBuilder.add(new PropertyRefImpl(propertyRefAnno.path(),
                        isNullOrEmpty(propertyRefAnno.alias()) ? null : propertyRefAnno.alias()));
            }
        }

        KeyImpl key = new KeyImpl(Collections.unmodifiableList(propertyRefBuilder));
        if (key.getPropertyRefs().size() < 1) {
            throw new IllegalArgumentException("Key is not specified on entity: " + cls.getName());
        }

        // NOTE: hasStream is not (yet) supported.

        return new EntityTypeImpl.Builder()
                .setName(getTypeName(entityAnno, cls))
                .setNamespace(getNamespace(entityAnno, cls))
                .setJavaType(cls)
                .setBaseTypeName(baseTypeName)
                .setIsAbstract(Modifier.isAbstract(cls.getModifiers()))
                .addStructuralProperties(buildStructuralProperties(cls))
                .setIsOpen(entityAnno.open())
                .setKey(key)
                .build();
    }

    private static String getTypeName(EdmEntity entityAnno, Class<?> entityClass) {
        String name = entityAnno.name();
        if (isNullOrEmpty(name)) {
            // Use class name if name is not specified in EdmEntity annotation
            name = entityClass.getSimpleName();
        }
        return name;
    }

    private static String getNamespace(EdmEntity entityAnno, Class<?> entityClass) {
        String namespace = entityAnno.namespace();
        if (isNullOrEmpty(namespace)) {
            // Use package name if namespace is not specified in EdmEntity annotation
            namespace = entityClass.getPackage().getName();
        }
        return namespace;
    }

    public static String getFullyQualifiedTypeName(EdmEntity entityAnno, Class<?> entityClass) {
        String name = getTypeName(entityAnno, entityClass);
        String namespace = getNamespace(entityAnno, entityClass);
        return namespace + "." + name;
    }
}
