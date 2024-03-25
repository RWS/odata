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
package com.sdl.odata.api.service;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link ODataResponse}.
 */
public class ODataResponseTest {

    @Test
    public void testBuilderBinaryBody() {
        ODataResponse response = new ODataResponse.Builder()
                .setStatus(ODataResponse.Status.OK)
                .setHeader("one", "value1")
                .setHeaders(ImmutableMap.of("two", "value2", "three", "value3"))
                .setBody(new byte[]{1, 2, 3})
                .build();

        assertEquals(ODataResponse.Status.OK, response.getStatus());
        assertEquals(3, response.getHeaders().size());
        assertEquals("value1", response.getHeader("one"));
        assertEquals("value2", response.getHeader("TWO"));
        assertEquals("value3", response.getHeader("Three"));
        assertArrayEquals(response.getBody(), new byte[]{1, 2, 3});
    }

    @Test
    public void testBuilderTextBody() throws UnsupportedEncodingException {
        ODataResponse response = new ODataResponse.Builder()
                .setStatus(ODataResponse.Status.CREATED)
                .setBodyText("The bike costs € 725", "UTF-8")
                .build();

        assertEquals(ODataResponse.Status.CREATED, response.getStatus());
        assertEquals("The bike costs € 725", response.getBodyText("UTF-8"));
    }

    @Test
    public void testStatusForCode() {
        assertEquals(ODataResponse.Status.OK, ODataResponse.Status.forCode(200));
        assertEquals(ODataResponse.Status.CREATED, ODataResponse.Status.forCode(201));
        assertEquals(ODataResponse.Status.NOT_FOUND, ODataResponse.Status.forCode(404));
        assertEquals(ODataResponse.Status.INTERNAL_SERVER_ERROR, ODataResponse.Status.forCode(500));
    }

    @Test
    public void testStatusForCodeException() {
        assertThrows(IllegalArgumentException.class, () -> ODataResponse.Status.forCode(0));
    }
}
