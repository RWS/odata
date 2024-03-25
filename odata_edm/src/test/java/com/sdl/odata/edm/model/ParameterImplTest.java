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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.Parameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test for ParameterImpl class.
 */
public class ParameterImplTest {

    @Test
    public void testParameterImpl() {
        ParameterImpl.Builder builder = new ParameterImpl.Builder();
        builder.setName("someParameter")
                .setType("someType")
                .setNullable(false)
                .setUnicode(false)
                .setPrecision(202L)
                .setMaxLength(203L)
                .setScale(204L)
                .setSRID(205L);

        Parameter parameter = builder.build();
        assertEquals("someParameter", parameter.getName());
        assertEquals("someType", parameter.getType());
        assertEquals(202L, parameter.getPrecision());
        assertEquals(203L, parameter.getMaxLength());
        assertEquals(204L, parameter.getScale());
        assertEquals(205L, parameter.getSRID());
        assertFalse(parameter.isNullable());
        assertFalse(parameter.isUnicode());
    }
}
