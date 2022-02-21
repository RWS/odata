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
package com.sdl.odata.util.edm;

import org.junit.Test;

import static com.sdl.odata.util.edm.EntityDataModelUtil.pluralize;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The Entity Data model Util Test.
 */
public class EntityDataModelUtilTest {

    @Test
    public void testPluralize() throws Exception {
        assertThat(pluralize("Bus"), is("Buses"));
        assertThat(pluralize("Hash"), is("Hashes"));
        assertThat(pluralize("Potato"), is("Potatoes"));
        assertThat(pluralize("Capability"), is("Capabilities"));
        assertThat(pluralize("Day"), is("Days"));
        assertThat(pluralize("WebApplication"), is("WebApplications"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPluralizeNull() throws Exception {
        pluralize(null);
    }
}
