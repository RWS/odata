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
package com.sdl.odata.api.edm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link OnDeleteAction}.
 */
public class OnDeleteActionTest {

    @Test
    public void testForName() {
        assertEquals(OnDeleteAction.CASCADE, OnDeleteAction.forName("Cascade"));
        assertEquals(OnDeleteAction.NONE, OnDeleteAction.forName("None"));
        assertEquals(OnDeleteAction.SET_NULL, OnDeleteAction.forName("SetNull"));
        assertEquals(OnDeleteAction.SET_DEFAULT, OnDeleteAction.forName("SetDefault"));
    }

    @Test
    public void testForNameException() {
        assertThrows(IllegalArgumentException.class, () -> OnDeleteAction.forName("CASCADE"));
    }

    @Test
    public void testGetName() {
        assertEquals("Cascade", OnDeleteAction.CASCADE.getName());
        assertEquals("None", OnDeleteAction.NONE.getName());
        assertEquals("SetNull", OnDeleteAction.SET_NULL.getName());
        assertEquals("SetDefault", OnDeleteAction.SET_DEFAULT.getName());
    }
}
