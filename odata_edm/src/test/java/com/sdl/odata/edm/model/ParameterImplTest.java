/*
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
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
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

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
        assertThat(parameter.getName(), is("someParameter"));
        assertThat(parameter.getType(), is("someType"));
        assertThat(parameter.getPrecision(), is(202L));
        assertThat(parameter.getMaxLength(), is(203L));
        assertThat(parameter.getScale(), is(204L));
        assertThat(parameter.getSRID(), is(205L));
        assertFalse(parameter.isNullable());
        assertFalse(parameter.isUnicode());
    }
}
