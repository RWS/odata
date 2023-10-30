/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link MediaType}.
 */
public class MediaTypeTest {

    @Test
    public void testFromStringNoParams() {
        MediaType mediaType = MediaType.fromString("text/html");

        assertEquals("text", mediaType.getType());
        assertEquals("html", mediaType.getSubType());
        assertEquals(0, mediaType.getParameters().size());
    }

    @Test
    public void testFromStringOneParam() {
        MediaType mediaType = MediaType.fromString("text/html;encoding=UTF-8");

        assertEquals("text", mediaType.getType());
        assertEquals("html", mediaType.getSubType());
        assertEquals(1, mediaType.getParameters().size());
        assertEquals("UTF-8", mediaType.getParameter("encoding"));
    }

    @Test
    public void testFromStringMultipleParams() {
        // NOTE: Spaces after ; should also be allowed
        MediaType mediaType = MediaType.fromString("application/atom+xml; encoding=UTF-8; q=0.8");

        assertEquals("application", mediaType.getType());
        assertEquals("atom+xml", mediaType.getSubType());
        assertEquals(2, mediaType.getParameters().size());
        assertEquals("UTF-8", mediaType.getParameter("encoding"));
        assertEquals("0.8", mediaType.getParameter("q"));
    }

    @Test
    public void testFromStringInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                MediaType.fromString("text;q=0.8")
        );
    }

    @Test
    public void testDefaultUrlConnectionAcceptHeaders() {
        MediaType mediaType = MediaType.fromString("*/*; q=.2");
        assertEquals("*", mediaType.getType());
        assertEquals("*", mediaType.getSubType());
        assertEquals(".2", mediaType.getParameter("q"));

        mediaType = MediaType.fromString("*; q=.4");
        assertEquals("*", mediaType.getType());
        assertEquals("*", mediaType.getSubType());
        assertEquals(".4", mediaType.getParameter("q"));
    }
}
