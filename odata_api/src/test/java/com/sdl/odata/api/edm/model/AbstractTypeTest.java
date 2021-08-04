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
package com.sdl.odata.api.edm.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link AbstractType}.
 */
public class AbstractTypeTest {

    @Test
    public void testForName() {
        assertThat(AbstractType.forName("PrimitiveType"), is(AbstractType.PRIMITIVE_TYPE));
        assertThat(AbstractType.forName("ComplexType"), is(AbstractType.COMPLEX_TYPE));
        assertThat(AbstractType.forName("EntityType"), is(AbstractType.ENTITY_TYPE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNameException() {
        AbstractType.forName("String");
    }

    @Test
    public void testGetMetaType() {
        assertThat(AbstractType.PRIMITIVE_TYPE.getMetaType(), is(MetaType.ABSTRACT));
        assertThat(AbstractType.COMPLEX_TYPE.getMetaType(), is(MetaType.ABSTRACT));
        assertThat(AbstractType.ENTITY_TYPE.getMetaType(), is(MetaType.ABSTRACT));
    }

    @Test
    public void testGetName() {
        assertThat(AbstractType.PRIMITIVE_TYPE.getName(), is("PrimitiveType"));
        assertThat(AbstractType.COMPLEX_TYPE.getName(), is("ComplexType"));
        assertThat(AbstractType.ENTITY_TYPE.getName(), is("EntityType"));
    }

    @Test
    public void testGetNamespace() {
        assertThat(AbstractType.PRIMITIVE_TYPE.getNamespace(), is("Edm"));
        assertThat(AbstractType.COMPLEX_TYPE.getNamespace(), is("Edm"));
        assertThat(AbstractType.ENTITY_TYPE.getNamespace(), is("Edm"));
    }

    @Test
    public void testGetFullyQualifiedName() {
        assertThat(AbstractType.PRIMITIVE_TYPE.getFullyQualifiedName(), is("Edm.PrimitiveType"));
        assertThat(AbstractType.COMPLEX_TYPE.getFullyQualifiedName(), is("Edm.ComplexType"));
        assertThat(AbstractType.ENTITY_TYPE.getFullyQualifiedName(), is("Edm.EntityType"));
    }

    @Test
    public void testGetJavaType() {
        assertNull(AbstractType.PRIMITIVE_TYPE.getJavaType());
        assertNull(AbstractType.COMPLEX_TYPE.getJavaType());
        assertNull(AbstractType.ENTITY_TYPE.getJavaType());
    }
}
