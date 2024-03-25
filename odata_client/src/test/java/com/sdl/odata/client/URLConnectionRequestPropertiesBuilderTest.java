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
package com.sdl.odata.client;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(1, result.size());

        assertTrue(result.containsKey("Cookie"));
        assertEquals("CN1=CV1", result.get("Cookie"));
    }

    @Test
    public void testWithMultipleCookies() {
        URLConnectionRequestPropertiesBuilder builder = new URLConnectionRequestPropertiesBuilder();
        Map<String, String> result = builder.withCookie("CN1", "CV1").withCookie("CN2", "CV2").build();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        assertTrue(result.containsKey("Cookie"));
        assertEquals("CN1=CV1; CN2=CV2", result.get("Cookie"));
    }

    @Test
    public void testWithAccessToken() {
        URLConnectionRequestPropertiesBuilder builder = new URLConnectionRequestPropertiesBuilder();
        Map<String, String> result = builder.withAccessToken("this_is_access_token").build();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        assertTrue(result.containsKey("Authorization"));
        assertEquals("Bearer this_is_access_token", result.get("Authorization"));
    }

}
