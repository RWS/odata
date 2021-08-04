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
package com.sdl.odata.client;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link URLConnectionRequestPropertiesBuilder}.
 */
public class URLConnectionRequestPropertiesBuilderTest {

    @Test
    public void testWithoutCookie() {
        URLConnectionRequestPropertiesBuilder builder = new URLConnectionRequestPropertiesBuilder();
        Map<String, String> result = builder.build();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testWithSingleCookie() {
        URLConnectionRequestPropertiesBuilder builder = new URLConnectionRequestPropertiesBuilder();
        Map<String, String> result = builder.withCookie("CN1", "CV1").build();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertThat(result.size(), is(1));

        assertTrue(result.containsKey("Cookie"));
        assertThat(result.get("Cookie"), is("CN1=CV1"));
    }

    @Test
    public void testWithMultipleCookies() {
        URLConnectionRequestPropertiesBuilder builder = new URLConnectionRequestPropertiesBuilder();
        Map<String, String> result = builder.withCookie("CN1", "CV1").withCookie("CN2", "CV2").build();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertThat(result.size(), is(1));

        assertTrue(result.containsKey("Cookie"));
        assertThat(result.get("Cookie"), is("CN1=CV1; CN2=CV2"));
    }

    @Test
    public void testWithAccessToken() {
        URLConnectionRequestPropertiesBuilder builder = new URLConnectionRequestPropertiesBuilder();
        Map<String, String> result = builder.withAccessToken("this_is_access_token").build();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertThat(result.size(), is(1));

        assertTrue(result.containsKey("Authorization"));
        assertThat(result.get("Authorization"), is("Bearer this_is_access_token"));
    }

}
