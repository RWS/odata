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
package com.sdl.odata.renderer.xml.util;

import org.junit.Test;

import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * This is unit test for {@link XMLWriterUtil}.
 *
 */
public class XMLWriterUtilTest {

    @Test
    public void testStartDocumentWithoutPrefix() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XMLStreamWriter writer = XMLWriterUtil.startDocument(outputStream, null, "test", "namespace");
            assertNotNull(writer);
            assertThat(outputStream.toString(), is("<test xmlns=\"namespace\""));
        }
    }

    @Test
    public void testStartDocumentWithPrefix() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XMLStreamWriter writer = XMLWriterUtil.startDocument(outputStream, "prefix", "test", "namespace");
            assertNotNull(writer);
            assertThat(outputStream.toString(), is("<prefix:test xmlns:prefix=\"namespace\""));
        }
    }
}
