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
package com.sdl.odata.edm.model;


import com.sdl.odata.api.edm.model.Facets;
import com.sdl.odata.api.edm.model.PrimitiveType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link TypeDefinitionImpl}.
 *
 */
public class TypeDefinitionImplTest {

    @Test
    public void testDefinitionImpl() {
        TypeDefinitionImpl definition = new TypeDefinitionImpl.Builder()
                .setIsUnicode(false)
                .setMaxLength(Facets.MAX_LENGTH_MAX)
                .setPrecision(4)
                .setScale(20)
                .setSRID(1234)
                .setUnderlyingType(PrimitiveType.INT64)
                .setName("definition")
                .setNamespace("namespace")
                .setJavaType(TypeDefinitionImpl.class)
                .build();

        assertThat(definition.getMaxLength(), is(Facets.MAX_LENGTH_MAX));
        assertThat(definition.getScale(), is(20L));
        assertThat(definition.getSRID(), is(1234L));
        assertThat(definition.getPrecision(), is(4L));
        assertThat(definition.isUnicode(), is(false));
        assertThat(definition.getName(), is("definition"));
        assertThat(definition.getNamespace(), is("namespace"));
        assertThat(definition.getUnderlyingType(), is(PrimitiveType.INT64));
    }

}
