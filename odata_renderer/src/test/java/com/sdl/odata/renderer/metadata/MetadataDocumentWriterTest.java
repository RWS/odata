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
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.renderer.WriterTest;
import org.junit.Test;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@code MetadataDocumentWriter}.
 */
public class MetadataDocumentWriterTest extends WriterTest {

    private static final String EXPECTED_METADATA_DOC_PATH = "/xml/MetadataDocument.xml";

    @Test
    public void testWriteMetadataDocument() throws Exception {

        MetadataDocumentWriter writer = new MetadataDocumentWriter(entityDataModel);

        writer.startDocument();
        writer.writeMetadataDocument();
        writer.endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_METADATA_DOC_PATH)), prettyPrintXml(writer.getXml()));
    }
}
