/*
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
package com.sdl.odata.renderer.xml.util;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
            assertEquals("<test xmlns=\"namespace\"", outputStream.toString());
        }
    }

    @Test
    public void testStartDocumentWithPrefix() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XMLStreamWriter writer = XMLWriterUtil.startDocument(outputStream, "prefix", "test", "namespace");
            assertNotNull(writer);
            assertEquals("<prefix:test xmlns:prefix=\"namespace\"", outputStream.toString());
        }
    }
}
