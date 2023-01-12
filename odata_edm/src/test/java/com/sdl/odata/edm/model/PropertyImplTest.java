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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.Facets;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.Property;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        assertEquals("propname", property.getName());
        assertEquals(PrimitiveType.STRING.getFullyQualifiedName(), property.getTypeName());
        assertNull(property.getElementTypeName(), "Element type name should be null for non-collection");
        assertFalse(property.isCollection());
        assertFalse(property.isNullable());
        assertEquals(field.getName(), property.getJavaField().getName());
        assertEquals("x", property.getDefaultValue());
        assertEquals(Facets.MAX_LENGTH_MAX, property.getMaxLength());
        assertEquals(5L, property.getPrecision());
        assertEquals(10L, property.getScale());
        assertEquals(1234L, property.getSRID());
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

        assertEquals("propname", property.getName());
        assertEquals(typeName, property.getTypeName());
        assertEquals(PrimitiveType.INT32.getFullyQualifiedName(), property.getElementTypeName());
        assertTrue(property.isCollection());
        assertEquals(field.getName(), property.getJavaField().getName());
    }

    @Test
    public void testSetTypeNameFromJavaField() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testStringField");

        TypeNameResolver typeNameResolver = mock(TypeNameResolver.class);
        when(typeNameResolver.resolveTypeName(String.class)).thenReturn(PrimitiveType.STRING.getFullyQualifiedName());

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeFromJavaField(field, typeNameResolver)
                .setJavaField(field)
                .build();

        // TypeNameResolver must have been called with the expected argument
        verify(typeNameResolver).resolveTypeName(String.class);
        verifyNoMoreInteractions(typeNameResolver);

        assertEquals(PrimitiveType.STRING.getFullyQualifiedName(), property.getTypeName());
        assertNull(property.getElementTypeName(), "Element type name should be null for non-collection");
        assertFalse(property.isCollection());
        assertEquals(field.getName(), property.getJavaField().getName());
    }

    @Test
    public void testSetTypeNameFromJavaFieldWithArray() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testLongArrayField");

        TypeNameResolver typeNameResolver = mock(TypeNameResolver.class);
        when(typeNameResolver.resolveTypeName(long.class)).thenReturn(PrimitiveType.INT64.getFullyQualifiedName());

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeFromJavaField(field, typeNameResolver)
                .setJavaField(field)
                .build();

        // TypeNameResolver must have been called with the expected argument
        verify(typeNameResolver).resolveTypeName(long.class);
        verifyNoMoreInteractions(typeNameResolver);

        assertEquals("Collection(" + PrimitiveType.INT64.getFullyQualifiedName() + ")", property.getTypeName());
        assertEquals(PrimitiveType.INT64.getFullyQualifiedName(), property.getElementTypeName());
        assertTrue(property.isCollection());
        assertEquals(field.getName(), property.getJavaField().getName());
    }

    @Test
    public void testSetTypeNameFromJavaFieldWithCollection() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testIntegerListField");

        TypeNameResolver typeNameResolver = mock(TypeNameResolver.class);
        when(typeNameResolver.resolveTypeName(Integer.class)).thenReturn(PrimitiveType.INT32.getFullyQualifiedName());

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeFromJavaField(field, typeNameResolver)
                .setJavaField(field)
                .build();

        // TypeNameResolver must have been called with the expected argument
        verify(typeNameResolver).resolveTypeName(Integer.class);
        verifyNoMoreInteractions(typeNameResolver);

        assertEquals("Collection(" + PrimitiveType.INT32.getFullyQualifiedName() + ")",
                property.getTypeName());
        assertEquals(PrimitiveType.INT32.getFullyQualifiedName(), property.getElementTypeName());
        assertTrue(property.isCollection());
        assertEquals(field.getName(), property.getJavaField().getName());
    }

    @Test
    public void testDefaults() throws NoSuchFieldException {
        Field field = PropertyImplTest.class.getDeclaredField("testStringField");

        Property property = new PropertyImpl.Builder()
                .setName("propname")
                .setTypeName(PrimitiveType.STRING.getFullyQualifiedName())
                .setJavaField(field)
                .build();

        assertEquals("propname", property.getName());
        assertEquals(PrimitiveType.STRING.getFullyQualifiedName(), property.getTypeName());
        assertNull(property.getElementTypeName(), "Element type name should be null for non-collection");
        assertFalse(property.isCollection());
        assertTrue(property.isNullable(), "isNullable should be true by default");
        assertEquals(field.getName(), property.getJavaField().getName());
        assertNull(property.getDefaultValue());
        assertEquals(Facets.MAX_LENGTH_UNSPECIFIED, property.getMaxLength());
        assertEquals(Facets.PRECISION_UNSPECIFIED, property.getPrecision());
        assertEquals(Facets.SCALE_UNSPECIFIED, property.getScale());
        assertEquals(Facets.SRID_UNSPECIFIED, property.getSRID());
        assertTrue(property.isUnicode(), "isUnicode should be true by default");
    }
}
