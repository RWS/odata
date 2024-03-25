/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.edm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link AbstractType}.
 */
public class AbstractTypeTest {

    @Test
    public void testForName() {
        assertEquals(AbstractType.PRIMITIVE_TYPE, AbstractType.forName("PrimitiveType"));
        assertEquals(AbstractType.COMPLEX_TYPE, AbstractType.forName("ComplexType"));
        assertEquals(AbstractType.ENTITY_TYPE, AbstractType.forName("EntityType"));
    }

    @Test
    public void testForNameException() {
        assertThrows(IllegalArgumentException.class, () -> AbstractType.forName("String"));
    }

    @Test
    public void testGetMetaType() {
        assertEquals(MetaType.ABSTRACT, AbstractType.PRIMITIVE_TYPE.getMetaType());
        assertEquals(MetaType.ABSTRACT, AbstractType.COMPLEX_TYPE.getMetaType());
        assertEquals(MetaType.ABSTRACT, AbstractType.ENTITY_TYPE.getMetaType());
    }

    @Test
    public void testGetName() {
        assertEquals("PrimitiveType", AbstractType.PRIMITIVE_TYPE.getName());
        assertEquals("ComplexType", AbstractType.COMPLEX_TYPE.getName());
        assertEquals("EntityType", AbstractType.ENTITY_TYPE.getName());
    }

    @Test
    public void testGetNamespace() {
        assertEquals("Edm", AbstractType.PRIMITIVE_TYPE.getNamespace());
        assertEquals("Edm", AbstractType.COMPLEX_TYPE.getNamespace());
        assertEquals("Edm", AbstractType.ENTITY_TYPE.getNamespace());
    }

    @Test
    public void testGetFullyQualifiedName() {
        assertEquals("Edm.PrimitiveType", AbstractType.PRIMITIVE_TYPE.getFullyQualifiedName());
        assertEquals("Edm.ComplexType", AbstractType.COMPLEX_TYPE.getFullyQualifiedName());
        assertEquals("Edm.EntityType", AbstractType.ENTITY_TYPE.getFullyQualifiedName());
    }

    @Test
    public void testGetJavaType() {
        assertNull(AbstractType.PRIMITIVE_TYPE.getJavaType());
        assertNull(AbstractType.COMPLEX_TYPE.getJavaType());
        assertNull(AbstractType.ENTITY_TYPE.getJavaType());
    }
}
