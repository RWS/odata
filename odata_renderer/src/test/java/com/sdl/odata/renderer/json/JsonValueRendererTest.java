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
package com.sdl.odata.renderer.json;

import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.renderer.RendererTest;
import com.sdl.odata.test.util.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.test.util.TestUtils.createODataRequest;
import static org.junit.Assert.assertTrue;

/**
 * Unit test to cover {@link JsonValueRendererTest}.
 */
public class JsonValueRendererTest extends RendererTest {

    private static final String ODATA_ENDPOINT_URL = "http://localhost:8080/odata_war/odata.svc/";
    private static final String TEST_BUILD_VERSION = "8.1.1";

    private JsonValueRenderer jsonValueRenderer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        jsonValueRenderer = new JsonValueRenderer();
    }

    @Test
    public void testFunctionResultStringScore() throws UnsupportedEncodingException {
        ODataUri execFunctionODataUri = new ODataUriParser(entityDataModel).parseUri(ODATA_ENDPOINT_URL +
                "ODataDemoFunctionImport");
        ODataRequestContext oDataRequestContext = TestUtils.createODataRequestContext(
                createODataRequest(ODataRequest.Method.GET, JSON), execFunctionODataUri, entityDataModel);
        int score = jsonValueRenderer.score(oDataRequestContext, QueryResult.from(TEST_BUILD_VERSION));
        assertTrue(score > 0);
    }
}
