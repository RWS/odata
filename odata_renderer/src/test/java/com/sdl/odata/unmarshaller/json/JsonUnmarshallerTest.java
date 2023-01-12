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
package com.sdl.odata.unmarshaller.json;

import com.google.common.collect.ImmutableMap;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.sdl.odata.api.service.ODataRequest.Method.DELETE;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataRequest.Method.PUT;
import static com.sdl.odata.test.util.TestUtils.createODataRequest;
import static com.sdl.odata.test.util.TestUtils.createODataRequestContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Json Unmarshaller Test.
 */
@ExtendWith(MockitoExtension.class)
public class JsonUnmarshallerTest extends UnmarshallerTest {

    private static final Map<String, String> CONTENT_TYPE = ImmutableMap.of("Content-type", "application/json");

    @InjectMocks
    private JsonUnmarshaller jsonUnmarshaller = new JsonUnmarshaller();

    @Spy
    private ODataParser uriParser = new ODataParserImpl();

    @Test
    public void testJsonNullScore() {
        assertEquals(0, jsonUnmarshaller.score(errorContext));
        assertThrows(ODataUnmarshallingException.class, () ->
                jsonUnmarshaller.unmarshall(errorContext)
        );

    }

    @Test
    public void testScoreForPOST() throws UnsupportedEncodingException {
        assertTrue(jsonUnmarshaller.score(createODataRequestContext(
                createODataRequest(POST, CONTENT_TYPE), odataUri, entityDataModel)) > 0);
    }

    @Test
    public void testScoreForGET() throws UnsupportedEncodingException {
        assertEquals(0, jsonUnmarshaller.score(createODataRequestContext(
                createODataRequest(GET, CONTENT_TYPE), odataUri, entityDataModel)));
    }

    @Test
    public void testScoreForPUT() throws UnsupportedEncodingException {
        assertTrue(jsonUnmarshaller.score(createODataRequestContext(
                createODataRequest(PUT, CONTENT_TYPE), odataUri, entityDataModel)) > 0);
    }

    @Test
    public void testScoreForDelete() throws UnsupportedEncodingException {
        assertEquals(0, jsonUnmarshaller.score(createODataRequestContext(DELETE, entityDataModel)));
    }
}
