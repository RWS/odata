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
package com.sdl.odata.renderer.xml.writer;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.WriterTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * The XML Service Document Writer Test.
 */
public class XMLServiceDocumentWriterTest extends WriterTest {
    private static final String SERVICE_DOCUMENT = "/xml/ServiceDocument.xml";

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testBuildServiceDocument() throws Exception {
        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc", entityDataModel);
        String serviceDocument = new XMLServiceDocumentWriter(odataUri, entityDataModel).buildServiceDocument();
        assertNotNull(serviceDocument);
        assertEquals(prettyPrintXml(readContent(SERVICE_DOCUMENT)), prettyPrintXml(serviceDocument));
    }

    @Test
    public void testBuildServiceDocumentException() {
        EntityDataModel entityDataModel = mock(EntityDataModel.class);

        assertThrows(ODataRenderException.class, () ->
                new XMLServiceDocumentWriter(odataUri, entityDataModel).buildServiceDocument()
        );
    }
}
