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

import com.sdl.odata.test.model.FunctionSample;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link FunctionImpl}.
 */
public class FunctionImplTest {

    @Test
    public void testFunctionImpl() {
        FunctionImpl function = new FunctionImpl.Builder()
                .setBound(true)
                .setComposable(true)
                .setEntitySetPath("MyEntitySetPath")
                .setJavaClass(FunctionSample.class)
                .setName("MyFunctionName")
                .setNamespace("MyFunctionNamespace")
                .setParameters(new HashSet<>())
                .setReturnType("MyFunctionReturnType")
                .build();

        assertTrue(function.isBound());
        assertTrue(function.isComposable());
        assertEquals("MyEntitySetPath", function.getEntitySetPath());
        assertEquals(function.getJavaClass(), FunctionSample.class);
        assertEquals("MyFunctionName", function.getName());
        assertEquals("MyFunctionNamespace", function.getNamespace());
        assertNotNull(function.getParameters());
        assertTrue(function.getParameters().isEmpty());
        assertEquals("MyFunctionReturnType", function.getReturnType());
    }
}
