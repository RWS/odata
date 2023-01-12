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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

        assertEquals(Facets.MAX_LENGTH_MAX, definition.getMaxLength());
        assertEquals(20L, definition.getScale());
        assertEquals(1234L, definition.getSRID());
        assertEquals(4L, definition.getPrecision());
        assertFalse(definition.isUnicode());
        assertEquals("definition", definition.getName());
        assertEquals("namespace", definition.getNamespace());
        assertEquals(PrimitiveType.INT64, definition.getUnderlyingType());
    }

}
