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
package com.sdl.odata.api.service;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ODataRequest}.
 */
public class ODataRequestTest {

    @Test
    public void testBuilderBinaryBody() {
        ODataRequest request = new ODataRequest.Builder()
                .setMethod(ODataRequest.Method.POST)
                .setUri("http://localhost:8080/test")
                .setHeader("one", "value1")
                .setHeaders(ImmutableMap.of("two", "value2", "three", "value3"))
                .setBody(new byte[]{1, 2, 3})
                .build();

        assertEquals(ODataRequest.Method.POST, request.getMethod());
        assertEquals("http://localhost:8080/test", request.getUri());
        assertEquals(3, request.getHeaders().size());
        assertEquals("value1", request.getHeader("one"));
        assertEquals("value2", request.getHeader("TWO"));
        assertEquals("value2", request.getHeader("TWO"));
        assertEquals("value3", request.getHeader("Three"));
        assertArrayEquals(new byte[]{1, 2, 3}, request.getBody());
    }

    @Test
    public void testBuilderTextBody() throws UnsupportedEncodingException {
        ODataRequest request = new ODataRequest.Builder()
                .setMethod(ODataRequest.Method.PUT)
                .setUri("http://localhost:8080/test")
                .setBodyText("The bike costs € 725", "UTF-8")
                .build();

        assertEquals(ODataRequest.Method.PUT, request.getMethod());
        assertEquals("http://localhost:8080/test", request.getUri());
        assertEquals("The bike costs € 725", request.getBodyText("UTF-8"));
    }

    @Test
    public void testGetAccept() {
        ODataRequest request = new ODataRequest.Builder()
                .setMethod(ODataRequest.Method.GET)
                .setUri("http://localhost:8080/test")
                .setAccept(
                        MediaType.fromString("text/html"),
                        MediaType.fromString("application/xml; q=0.8"),
                        MediaType.fromString("*/*; q=0.1"))
                .build();

        List<MediaType> mediaTypes = request.getAccept();
        assertEquals(3, mediaTypes.size());

        MediaType mediaType1 = mediaTypes.get(0);
        assertEquals("text", mediaType1.getType());
        assertEquals("html", mediaType1.getSubType());
        assertEquals(0, mediaType1.getParameters().size());

        MediaType mediaType2 = mediaTypes.get(1);
        assertEquals("application", mediaType2.getType());
        assertEquals("xml", mediaType2.getSubType());
        assertEquals(1, mediaType2.getParameters().size());
        assertEquals("0.8", mediaType2.getParameter("q"));

        MediaType mediaType3 = mediaTypes.get(2);
        assertEquals("*", mediaType3.getType());
        assertEquals("*", mediaType3.getSubType());
        assertEquals(1, mediaType3.getParameters().size());
        assertEquals("0.1", mediaType3.getParameter("q"));
    }

    @Test
    public void testBuilderWithAdditionalData() {
        ODataRequest request = new ODataRequest.Builder()
                .setMethod(ODataRequest.Method.GET)
                .setUri("http://localhost:8080/test")
                .addAdditionalData(new Person("PersonName"))
                .build();

        assertEquals(ODataRequest.Method.GET, request.getMethod());
        assertEquals("http://localhost:8080/test", request.getUri());
        Optional<Person> personData = request.getAdditionalData(Person.class);
        assertTrue(personData.isPresent());
        assertEquals("PersonName", personData.get().getName());
    }

    /**
     * Sample class to test additional parameters in oDataRequest.
     */
    static class Person {
        private final String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
