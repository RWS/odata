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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmConstraint;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.Property;
import com.sdl.odata.api.edm.model.ReferentialConstraint;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.edm.model.NavigationPropertyImpl;
import com.sdl.odata.edm.model.PropertyImpl;
import com.sdl.odata.edm.model.ReferentialConstraintImpl;
import com.sdl.odata.edm.model.TypeNameResolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Structured Type Factory.
 * @param <T> structured type
 */
abstract class AnnotationStructuredTypeFactory<T extends StructuredType> {

    private final TypeNameResolver typeNameResolver;

    protected AnnotationStructuredTypeFactory(TypeNameResolver typeNameResolver) {
        this.typeNameResolver = typeNameResolver;
    }

    public abstract T build(Class<?> cls);

    protected List<StructuralProperty> buildStructuralProperties(Class<?> cls) {
        List<StructuralProperty> properties = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            EdmProperty propertyAnno = field.getAnnotation(EdmProperty.class);
            EdmNavigationProperty navigationPropertyAnno = field.getAnnotation(EdmNavigationProperty.class);

            if (propertyAnno != null) {
                if (navigationPropertyAnno != null) {
                    throw new IllegalArgumentException("Field has both an EdmProperty and an EdmNavigationProperty " +
                            "annotation. Only one of the two is allowed: " + field.toGenericString());
                }
                properties.add(buildProperty(propertyAnno, field));
            } else if (navigationPropertyAnno != null) {
                properties.add(buildNavigationProperty(navigationPropertyAnno, field));
            }
        }
        return properties;
    }

    private Property buildProperty(EdmProperty propertyAnno, Field field) {
        PropertyImpl.Builder builder = new PropertyImpl.Builder();

        // Name
        String name = propertyAnno.name();
        if (isNullOrEmpty(name)) {
            // Use field name if name is not specified in the EdmProperty annotation
            name = field.getName();
        }
        builder.setName(name);

        // Type
        String typeName = propertyAnno.type();
        if (!isNullOrEmpty(typeName)) {
            builder.setTypeName(typeName);
        } else {
            // Derive OData type from Java field type
            builder.setTypeFromJavaField(field, typeNameResolver);
        }

        return builder
                .setIsNullable(propertyAnno.nullable())
                .setJavaField(field)
                .setDefaultValue(isNullOrEmpty(propertyAnno.defaultValue()) ? null : propertyAnno.defaultValue())
                .setMaxLength(propertyAnno.maxLength())
                .setPrecision(propertyAnno.precision())
                .setScale(propertyAnno.scale())
                .setSRID(propertyAnno.srid())
                .setIsUnicode(propertyAnno.unicode())
                .build();
    }

    private NavigationProperty buildNavigationProperty(EdmNavigationProperty navigationPropertyAnno, Field field) {
        NavigationPropertyImpl.Builder builder = new NavigationPropertyImpl.Builder();

        // Name
        String name = navigationPropertyAnno.name();
        if (isNullOrEmpty(name)) {
            // Use field name if name is not specified in the EdmProperty annotation
            name = field.getName();
        }
        builder.setName(name);

        // Type
        String typeName = navigationPropertyAnno.type();
        if (!isNullOrEmpty(typeName)) {
            builder.setTypeName(typeName);
        } else {
            // Derive OData type from Java field type
            builder.setTypeFromJavaField(field, typeNameResolver);
        }

        // Referential constraints
        List<ReferentialConstraint> referentialConstraints = new ArrayList<>();
        for (EdmConstraint constraint : navigationPropertyAnno.constraints()) {
            referentialConstraints.add(
                    new ReferentialConstraintImpl(constraint.property(), constraint.referencedProperty()));
        }

        return builder
                .setIsNullable(navigationPropertyAnno.nullable())
                .setJavaField(field)
                .setPartnerName(
                        isNullOrEmpty(navigationPropertyAnno.partner()) ? null : navigationPropertyAnno.partner())
                .setContainsTarget(navigationPropertyAnno.containsTarget())
                .addReferentialConstraints(referentialConstraints)
                .addOnDeleteActions(Arrays.asList(navigationPropertyAnno.onDelete()))
                .build();
    }
}
