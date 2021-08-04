/*
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

import com.sdl.odata.api.ODataServerException;
import org.junit.Test;

import static com.sdl.odata.api.ODataErrorCode.EDM_ERROR;
import static com.sdl.odata.api.ODataErrorCode.UNMARSHALLER_ERROR;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintJson;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.Assert.assertEquals;

/**
 * Json Error Response Writer Test.
 *
 */
public class JsonErrorResponseWriterTest {
    private static final String EXPECTED_ERROR_RESPONSE_PATH = "/json/ErrorResponse.json";
    private static final String EXPECTED_ERROR_RESPONSE_WITH_TARGET_PATH = "/json/ErrorResponseWithTarget.json";

    @Test
    public void testWriteError() throws Exception {

        JsonErrorResponseWriter writer = new JsonErrorResponseWriter();

        String json = writer.getJsonError(new ODataServerException(EDM_ERROR, "EDM error"));

        assertEquals(prettyPrintJson(readContent(EXPECTED_ERROR_RESPONSE_PATH)), prettyPrintJson(json));
    }

    @Test
    public void testWriteErrorWithTarget() throws Exception {

        JsonErrorResponseWriter writer = new JsonErrorResponseWriter();

        String json = writer.getJsonError(
                new ODataServerException(UNMARSHALLER_ERROR, "Category expected but not found", "category"));

        assertEquals(prettyPrintJson(readContent(EXPECTED_ERROR_RESPONSE_WITH_TARGET_PATH)), prettyPrintJson(json));
    }


}
