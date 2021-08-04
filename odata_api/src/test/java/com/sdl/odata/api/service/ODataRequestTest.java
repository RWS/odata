/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

        assertThat(request.getMethod(), is(ODataRequest.Method.POST));
        assertThat(request.getUri(), is("http://localhost:8080/test"));
        assertThat(request.getHeaders().size(), is(3));
        assertThat(request.getHeader("one"), is("value1"));
        assertThat(request.getHeader("TWO"), is("value2"));
        assertThat(request.getHeader("Three"), is("value3"));
        assertThat(request.getBody(), is(new byte[]{1, 2, 3}));
    }

    @Test
    public void testBuilderTextBody() throws UnsupportedEncodingException {
        ODataRequest request = new ODataRequest.Builder()
                .setMethod(ODataRequest.Method.PUT)
                .setUri("http://localhost:8080/test")
                .setBodyText("The bike costs € 725", "UTF-8")
                .build();

        assertThat(request.getMethod(), is(ODataRequest.Method.PUT));
        assertThat(request.getUri(), is("http://localhost:8080/test"));
        assertThat(request.getBodyText("UTF-8"), is("The bike costs € 725"));
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
        assertThat(mediaTypes.size(), is(3));

        MediaType mediaType1 = mediaTypes.get(0);
        assertThat(mediaType1.getType(), is("text"));
        assertThat(mediaType1.getSubType(), is("html"));
        assertThat(mediaType1.getParameters().size(), is(0));

        MediaType mediaType2 = mediaTypes.get(1);
        assertThat(mediaType2.getType(), is("application"));
        assertThat(mediaType2.getSubType(), is("xml"));
        assertThat(mediaType2.getParameters().size(), is(1));
        assertThat(mediaType2.getParameter("q"), is("0.8"));

        MediaType mediaType3 = mediaTypes.get(2);
        assertThat(mediaType3.getType(), is("*"));
        assertThat(mediaType3.getSubType(), is("*"));
        assertThat(mediaType3.getParameters().size(), is(1));
        assertThat(mediaType3.getParameter("q"), is("0.1"));
    }

    @Test
    public void testBuilderWithAdditionalData() throws UnsupportedEncodingException {
        ODataRequest request = new ODataRequest.Builder()
                .setMethod(ODataRequest.Method.GET)
                .setUri("http://localhost:8080/test")
                .addAdditionalData(new Person("PersonName"))
                .build();

        assertThat(request.getMethod(), is(ODataRequest.Method.GET));
        assertThat(request.getUri(), is("http://localhost:8080/test"));
        Optional<Person> personData = request.getAdditionalData(Person.class);
        assertTrue(personData.isPresent());
        assertThat(personData.get().getName(), is("PersonName"));
    }

    /**
     * Sample class to test additional parameters in oDataRequest.
     */
    class Person {
        private final String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
