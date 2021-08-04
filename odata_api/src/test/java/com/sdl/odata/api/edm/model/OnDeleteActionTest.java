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
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link OnDeleteAction}.
 */
public class OnDeleteActionTest {

    @Test
    public void testForName() {
        assertThat(OnDeleteAction.forName("Cascade"), is(OnDeleteAction.CASCADE));
        assertThat(OnDeleteAction.forName("None"), is(OnDeleteAction.NONE));
        assertThat(OnDeleteAction.forName("SetNull"), is(OnDeleteAction.SET_NULL));
        assertThat(OnDeleteAction.forName("SetDefault"), is(OnDeleteAction.SET_DEFAULT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNameException() {
        OnDeleteAction.forName("CASCADE");
    }

    @Test
    public void testGetName() {
        assertThat(OnDeleteAction.CASCADE.getName(), is("Cascade"));
        assertThat(OnDeleteAction.NONE.getName(), is("None"));
        assertThat(OnDeleteAction.SET_NULL.getName(), is("SetNull"));
        assertThat(OnDeleteAction.SET_DEFAULT.getName(), is("SetDefault"));
    }
}
