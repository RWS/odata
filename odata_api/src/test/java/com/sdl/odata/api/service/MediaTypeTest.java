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
package com.sdl.odata.api.service;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link MediaType}.
 */
public class MediaTypeTest {

    @Test
    public void testFromStringNoParams() {
        MediaType mediaType = MediaType.fromString("text/html");

        assertThat(mediaType.getType(), is("text"));
        assertThat(mediaType.getSubType(), is("html"));
        assertThat(mediaType.getParameters().size(), is(0));
    }

    @Test
    public void testFromStringOneParam() {
        MediaType mediaType = MediaType.fromString("text/html;encoding=UTF-8");

        assertThat(mediaType.getType(), is("text"));
        assertThat(mediaType.getSubType(), is("html"));
        assertThat(mediaType.getParameters().size(), is(1));
        assertThat(mediaType.getParameter("encoding"), is("UTF-8"));
    }

    @Test
    public void testFromStringMultipleParams() {
        // NOTE: Spaces after ; should also be allowed
        MediaType mediaType = MediaType.fromString("application/atom+xml; encoding=UTF-8; q=0.8");

        assertThat(mediaType.getType(), is("application"));
        assertThat(mediaType.getSubType(), is("atom+xml"));
        assertThat(mediaType.getParameters().size(), is(2));
        assertThat(mediaType.getParameter("encoding"), is("UTF-8"));
        assertThat(mediaType.getParameter("q"), is("0.8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalid() {
        MediaType.fromString("text;q=0.8");
    }

    @Test
    public void testDefaultUrlConnectionAcceptHeaders() {
        MediaType mediaType = MediaType.fromString("*/*; q=.2");
        assertThat(mediaType.getType(), is("*"));
        assertThat(mediaType.getSubType(), is("*"));
        assertThat(mediaType.getParameter("q"), is(".2"));

        mediaType = MediaType.fromString("*; q=.4");
        assertThat(mediaType.getType(), is("*"));
        assertThat(mediaType.getSubType(), is("*"));
        assertThat(mediaType.getParameter("q"), is(".4"));
    }
}
