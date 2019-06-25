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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.Facets;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.Property;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PropertyImpl}.
 *
 */
public class PropertyImplTest {

    private String testStringField;
    private List<Integer> testIntegerListField;
    private long[] testLongArrayField;

    @Test
    public void testSetTypeNameNonCollection() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testStringField");

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeName(PrimitiveType.STRING.getFullyQualifiedName())
                .setIsNullable(false)
                .setJavaField(field)
                .setDefaultValue("x")
                .setMaxLength(Facets.MAX_LENGTH_MAX)
                .setPrecision(5L)
                .setScale(10L)
                .setSRID(1234L)
                .setIsUnicode(false)
                .build();

        assertThat(property.getName(), is("propname"));
        assertThat(property.getTypeName(), is(PrimitiveType.STRING.getFullyQualifiedName()));
        assertNull("Element type name should be null for non-collection", property.getElementTypeName());
        assertFalse(property.isCollection());
        assertFalse(property.isNullable());
        assertThat(property.getJavaField().getName(), is(field.getName()));
        assertThat(property.getDefaultValue(), is("x"));
        assertThat(property.getMaxLength(), is(Facets.MAX_LENGTH_MAX));
        assertThat(property.getPrecision(), is(5L));
        assertThat(property.getScale(), is(10L));
        assertThat(property.getSRID(), is(1234L));
        assertFalse(property.isUnicode());
    }

    @Test
    public void testSetTypeNameCollection() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testIntegerListField");

        String typeName = "Collection(" + PrimitiveType.INT32.getFullyQualifiedName() + ")";

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeName(typeName)
                .setJavaField(field)
                .build();

        assertThat(property.getName(), is("propname"));
        assertThat(property.getTypeName(), is(typeName));
        assertThat(property.getElementTypeName(), is(PrimitiveType.INT32.getFullyQualifiedName()));
        assertTrue(property.isCollection());
        assertThat(property.getJavaField().getName(), is(field.getName()));
    }

    @Test
    public void testSetTypeNameFromJavaField() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testStringField");
        PropertyDescriptor propertyDescriptor =
           BeanUtils.getPropertyDescriptor(PropertyImplTest.class, "testStringField");

        TypeNameResolver typeNameResolver = mock(TypeNameResolver.class);
        when(typeNameResolver.resolveTypeName(String.class)).thenReturn(PrimitiveType.STRING.getFullyQualifiedName());

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeFromJavaFieldOrDescriptor(field, propertyDescriptor, typeNameResolver)
                .setJavaField(field)
                .build();

        // TypeNameResolver must have been called with the expected argument
        verify(typeNameResolver).resolveTypeName(String.class);
        verifyNoMoreInteractions(typeNameResolver);

        assertThat(property.getTypeName(), is(PrimitiveType.STRING.getFullyQualifiedName()));
        assertNull("Element type name should be null for non-collection", property.getElementTypeName());
        assertFalse(property.isCollection());
        assertThat(property.getJavaField().getName(), is(field.getName()));
    }

    @Test
    public void testSetTypeNameFromJavaFieldWithArray() throws NoSuchFieldException {
        Field field =
          PropertyImplTest.class.getDeclaredField("testLongArrayField");
        PropertyDescriptor propertyDescriptor =
          BeanUtils.getPropertyDescriptor(PropertyImplTest.class, "testLongArrayField");

        TypeNameResolver typeNameResolver = mock(TypeNameResolver.class);
        when(typeNameResolver.resolveTypeName(long.class)).thenReturn(PrimitiveType.INT64.getFullyQualifiedName());

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeFromJavaFieldOrDescriptor(field, propertyDescriptor, typeNameResolver)
                .setJavaField(field)
                .build();

        // TypeNameResolver must have been called with the expected argument
        verify(typeNameResolver).resolveTypeName(long.class);
        verifyNoMoreInteractions(typeNameResolver);

        assertThat(property.getTypeName(), is("Collection(" + PrimitiveType.INT64.getFullyQualifiedName() + ")"));
        assertThat(property.getElementTypeName(), is(PrimitiveType.INT64.getFullyQualifiedName()));
        assertTrue(property.isCollection());
        assertThat(property.getJavaField().getName(), is(field.getName()));
    }

    @Test
    public void testSetTypeNameFromJavaFieldWithCollection() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testIntegerListField");
        PropertyDescriptor propertyDescriptor =
            BeanUtils.getPropertyDescriptor(PropertyImplTest.class, "testIntegerListField");

        TypeNameResolver typeNameResolver = mock(TypeNameResolver.class);
        when(typeNameResolver.resolveTypeName(Integer.class)).thenReturn(PrimitiveType.INT32.getFullyQualifiedName());

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeFromJavaFieldOrDescriptor(field, propertyDescriptor, typeNameResolver)
                .setJavaField(field)
                .build();

        // TypeNameResolver must have been called with the expected argument
        verify(typeNameResolver).resolveTypeName(Integer.class);
        verifyNoMoreInteractions(typeNameResolver);

        assertThat(property.getTypeName(), is("Collection(" + PrimitiveType.INT32.getFullyQualifiedName() + ")"));
        assertThat(property.getElementTypeName(), is(PrimitiveType.INT32.getFullyQualifiedName()));
        assertTrue(property.isCollection());
        assertThat(property.getJavaField().getName(), is(field.getName()));
    }

    @Test
    public void testDefaults() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testStringField");

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeName(PrimitiveType.STRING.getFullyQualifiedName())
                .setJavaField(field)
                .build();

        assertThat(property.getName(), is("propname"));
        assertThat(property.getTypeName(), is(PrimitiveType.STRING.getFullyQualifiedName()));
        assertNull("Element type name should be null for non-collection", property.getElementTypeName());
        assertFalse(property.isCollection());
        assertTrue("isNullable should be true by default", property.isNullable());
        assertThat(property.getJavaField().getName(), is(field.getName()));
        assertNull(property.getDefaultValue());
        assertThat(property.getMaxLength(), is(Facets.MAX_LENGTH_UNSPECIFIED));
        assertThat(property.getPrecision(), is(Facets.PRECISION_UNSPECIFIED));
        assertThat(property.getScale(), is(Facets.SCALE_UNSPECIFIED));
        assertThat(property.getSRID(), is(Facets.SRID_UNSPECIFIED));
        assertTrue("isUnicode should be true by default", property.isUnicode());
    }

}
