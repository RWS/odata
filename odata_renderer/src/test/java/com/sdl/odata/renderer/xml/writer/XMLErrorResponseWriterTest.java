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
package com.sdl.odata.renderer.xml.writer;

import com.sdl.odata.api.ODataServerException;
import org.junit.Test;

import static com.sdl.odata.api.ODataErrorCode.EDM_ERROR;
import static com.sdl.odata.api.ODataErrorCode.UNMARSHALLER_ERROR;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.Assert.assertEquals;

/**
 * The XML Error Response Writer Test.
 */
public class XMLErrorResponseWriterTest {

    private static final String EXPECTED_ERROR_RESPONSE_PATH = "/xml/ErrorResponse.xml";
    private static final String EXPECTED_ERROR_RESPONSE_WITH_TARGET_PATH = "/xml/ErrorResponseWithTarget.xml";

    @Test
    public void testWriteError() throws Exception {

        XMLErrorResponseWriter writer = new XMLErrorResponseWriter();

        writer.startDocument();
        writer.writeError(new ODataServerException(EDM_ERROR, "EDM error"));
        writer.endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ERROR_RESPONSE_PATH)), prettyPrintXml(writer.getXml()));
    }

    @Test
    public void testWriteErrorWithTarget() throws Exception {

        XMLErrorResponseWriter writer = new XMLErrorResponseWriter();

        writer.startDocument();
        writer.writeError(new ODataServerException(UNMARSHALLER_ERROR, "Category expected but not found", "category"));
        writer.endDocument();

        assertEquals(prettyPrintXml(
                readContent(EXPECTED_ERROR_RESPONSE_WITH_TARGET_PATH)), prettyPrintXml(writer.getXml()));
    }
}
