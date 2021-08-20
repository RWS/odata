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
package com.sdl.odata.api.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

        assertThat(response.getStatus(), is(ODataResponse.Status.OK));
        assertThat(response.getHeaders().size(), is(3));
        assertThat(response.getHeader("one"), is("value1"));
        assertThat(response.getHeader("TWO"), is("value2"));
        assertThat(response.getHeader("Three"), is("value3"));
        assertThat(response.getBody(), is(new byte[]{1, 2, 3}));
    }

    @Test
    public void testBuilderTextBody() throws UnsupportedEncodingException {
        ODataResponse response = new ODataResponse.Builder()
                .setStatus(ODataResponse.Status.CREATED)
                .setBodyText("The bike costs € 725", "UTF-8")
                .build();

        assertThat(response.getStatus(), is(ODataResponse.Status.CREATED));
        assertThat(response.getBodyText("UTF-8"), is("The bike costs € 725"));
    }

    @Test
    public void testStatusForCode() {
        assertThat(ODataResponse.Status.forCode(200), is(ODataResponse.Status.OK));
        assertThat(ODataResponse.Status.forCode(201), is(ODataResponse.Status.CREATED));
        assertThat(ODataResponse.Status.forCode(404), is(ODataResponse.Status.NOT_FOUND));
        assertThat(ODataResponse.Status.forCode(500), is(ODataResponse.Status.INTERNAL_SERVER_ERROR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStatusForCodeException() {
        ODataResponse.Status.forCode(0);
    }
}
