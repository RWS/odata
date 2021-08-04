/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link PrimitiveType}.
 */
public class PrimitiveTypeTest {

    @Test
    public void testForName() {
        assertThat(PrimitiveType.forName("String"), is(PrimitiveType.STRING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNameException() {
        PrimitiveType.forName("Primitive");
    }

    @Test
    public void testGetMetaType() {
        assertThat(PrimitiveType.INT32.getMetaType(), is(MetaType.PRIMITIVE));
    }

    @Test
    public void testGetName() {
        assertThat(PrimitiveType.DECIMAL.getName(), is("Decimal"));
    }

    @Test
    public void testGetNamespace() {
        assertThat(PrimitiveType.DOUBLE.getNamespace(), is("Edm"));
    }

    @Test
    public void testGetFullyQualifiedName() {
        assertThat(PrimitiveType.BOOLEAN.getFullyQualifiedName(), is("Edm.Boolean"));
    }

    @Test
    public void testGetJavaType() {
        assertThat(PrimitiveType.INT16.getJavaType().getName(), is(short.class.getName()));
    }
}
