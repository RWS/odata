/**
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.renderer.json.writer;

import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.WriterTest;
import org.junit.Test;

import java.io.IOException;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintJson;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.Assert.assertEquals;

/**
 * Json Service Root Test.
 *
 */
public class JsonServiceDocumentWriterTest extends WriterTest {

    private JsonServiceDocumentWriter serviceWriter;

    private static final String EXPECTED_SERVICE_DOCUMENT = "/json/ServiceDocument.json";

    @Test
    public void setUp() throws Exception {
        super.setUp();
        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc", entityDataModel);
        serviceWriter = new JsonServiceDocumentWriter(odataUri, entityDataModel);
    }

    @Test
    public void testServiceRootDocument() throws ODataRenderException, IOException {
        assertEquals(prettyPrintJson(readContent(EXPECTED_SERVICE_DOCUMENT)),
                prettyPrintJson(serviceWriter.buildJson()));
    }
}
