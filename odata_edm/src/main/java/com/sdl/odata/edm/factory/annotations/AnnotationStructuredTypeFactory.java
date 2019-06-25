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
package com.sdl.odata.edm.factory.annotations;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sdl.odata.api.edm.annotations.EdmConstraint;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.api.edm.model.Property;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.ReferentialConstraint;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.edm.model.NavigationPropertyImpl;
import com.sdl.odata.edm.model.PropertyImpl;
import com.sdl.odata.edm.model.ReferentialConstraintImpl;
import com.sdl.odata.edm.model.TypeNameResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

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
        Map<String, Annotation> annotations = new LinkedHashMap<>();
        Map<String, PropertyDescriptor> props = new HashMap<>();
        Map<String, Field> fields = new HashMap<>();
        //PropertyDescriptors override field
        for (Field field : cls.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Map.Entry<String, Annotation> annotatedName = getAnnotatedName(field);
            if (annotatedName != null) {
                fields.put(annotatedName.getKey(), field);
                annotations.put(annotatedName.getKey(), annotatedName.getValue());
            } else {
                fields.put(field.getName(), field);
            }
        }
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(cls)) {
            Map.Entry<String, Annotation> annotatedName = getAnnotatedName(propertyDescriptor.getReadMethod() != null ?
                                                                                   propertyDescriptor.getReadMethod() :
                                                                                   propertyDescriptor.getWriteMethod());
            if (annotatedName != null) {
                props.put(annotatedName.getKey(), propertyDescriptor);
                annotations.put(annotatedName.getKey(), annotatedName.getValue());
            } else {
                props.put(propertyDescriptor.getName(), propertyDescriptor);
            }
        }
        List<StructuralProperty> properties = new ArrayList<>();
        for (Map.Entry<String, Annotation> annotationEntry : annotations.entrySet()) {
            String propertyName = annotationEntry.getKey();
            Annotation annotation = annotationEntry.getValue();
            Field field = fields.get(propertyName);
            PropertyDescriptor propertyDescriptor = props.get(propertyName);
            if (annotation instanceof EdmProperty) {
                properties.add(buildProperty((EdmProperty) annotation, field, propertyDescriptor, propertyName));
            } else {
                properties.add(buildNavigationProperty((EdmNavigationProperty) annotation,
                                                       field, propertyDescriptor, propertyName));
            }
        }

        return properties;
    }


    //Return computed name along with EdmAnnotation (Pair) or null
    private Map.Entry<String, Annotation> getAnnotatedName(AnnotatedElement member) {
        EdmProperty propertyAnno = member.getAnnotation(EdmProperty.class);
        EdmNavigationProperty navigationPropertyAnno = member.getAnnotation(EdmNavigationProperty.class);
        String memberName = member instanceof Field ? ((Field) member).getName() : ((Method) member).getName();
        if (propertyAnno != null && navigationPropertyAnno != null) {
            throw new IllegalArgumentException("Field has both an EdmProperty and an EdmNavigationProperty " +
                                               "annotation. Only one of the two is allowed: " + member);
        }
        if (propertyAnno != null) {
            if (StringUtils.isEmpty(propertyAnno.name())) {
                return new AbstractMap.SimpleImmutableEntry<>(memberName, propertyAnno);
            } else {
                return new AbstractMap.SimpleImmutableEntry<>(propertyAnno.name(), propertyAnno);
            }
        } else if (navigationPropertyAnno != null) {
            if (StringUtils.isEmpty(navigationPropertyAnno.name())) {
                return new AbstractMap.SimpleImmutableEntry<>(memberName, navigationPropertyAnno);
            } else {
                return new AbstractMap.SimpleImmutableEntry<>(navigationPropertyAnno.name(), navigationPropertyAnno);
            }
        }
        return null;
    }

    private Property buildProperty(EdmProperty propertyAnno, Field field,
                                   PropertyDescriptor propertyDescriptor, String name) {
        PropertyImpl.Builder builder = new PropertyImpl.Builder();

        // Type
        String typeName = propertyAnno.type();
        if (!isNullOrEmpty(typeName)) {
            builder.setTypeName(typeName);
        } else {
            // Derive OData type from Java field type
            builder.setTypeFromJavaFieldOrDescriptor(field, propertyDescriptor, typeNameResolver);
        }

        return builder
                .setName(name)
                .setIsNullable(propertyAnno.nullable())
                .setJavaField(field)
                .setPropertyDescriptor(propertyDescriptor)
                .setDefaultValue(isNullOrEmpty(propertyAnno.defaultValue()) ? null : propertyAnno.defaultValue())
                .setMaxLength(propertyAnno.maxLength())
                .setPrecision(propertyAnno.precision())
                .setScale(propertyAnno.scale())
                .setSRID(propertyAnno.srid())
                .setIsUnicode(propertyAnno.unicode())
                .build();
    }

    private NavigationProperty buildNavigationProperty(EdmNavigationProperty navigationPropertyAnno, Field field,
                                                       PropertyDescriptor propertyDescriptor, String name) {
        NavigationPropertyImpl.Builder builder = new NavigationPropertyImpl.Builder();

        builder.setName(name);

        // Type
        String typeName = navigationPropertyAnno.type();
        if (!isNullOrEmpty(typeName)) {
            builder.setTypeName(typeName);
        } else {
            // Derive OData type from Java field type
            builder.setTypeFromJavaFieldOrDescriptor(field, propertyDescriptor, typeNameResolver);
        }

        // Referential constraints
        List<ReferentialConstraint> referentialConstraints = new ArrayList<>();
        for (EdmConstraint constraint : navigationPropertyAnno.constraints()) {
            referentialConstraints.add(
                    new ReferentialConstraintImpl(constraint.property(), constraint.referencedProperty()));
        }

        return builder
                .setName(name)
                .setIsNullable(navigationPropertyAnno.nullable())
                .setJavaField(field)
                .setPropertyDescriptor(propertyDescriptor)
                .setPartnerName(
                        isNullOrEmpty(navigationPropertyAnno.partner()) ? null : navigationPropertyAnno.partner())
                .setContainsTarget(navigationPropertyAnno.containsTarget())
                .addReferentialConstraints(referentialConstraints)
                .addOnDeleteActions(Arrays.asList(navigationPropertyAnno.onDelete()))
                .build();
    }
}
