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
package com.sdl.odata.unmarshaller.json;

import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.test.model.ActionSample;
import com.sdl.odata.test.model.UnboundActionSample;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for OData Json Action Parser class.
 */
public class ODataJsonActionParserTest extends UnmarshallerTest {
    private static final String ACTION_PARAMETERS_JSON = "/json/ActionParameters.json";
    private static final String ACTION_IMPORT_PARAMETERS_JSON = "/json/ActionImportParameters.json";
    private static final String EMPTY_PARAMETERS_JSON = "/json/ActionEmpty.json";
    private static final String INCORRECT_PARAMETERS_JSON = "/xml/Product.xml";

    private final ODataParser uriParser = new ODataParserImpl();

    @Test
    public void testParseAction() throws Exception {
        String uri = "http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(ACTION_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        ActionSample action = (ActionSample) parser.getAction();
        assertEquals(101, action.getIntNumber());
        assertEquals(2L, action.getNumber());
        assertEquals("BLACKFRIDAY", action.getStringParameter());
        assertEquals("binaryLink", action.getParameters().get("type"));
        assertEquals("testUrl", action.getParameters().get("url"));

        Map<String, String> param1 = new HashMap<>();
        param1.put("type", "binaryLink");
        param1.put("url", "testUrl");

        Map<String, String> param2 = new HashMap<>();
        param2.put("type", "binaryLink1");
        param2.put("url", "testUrl1");

        assertTrue(action.getParametersList().containsAll(List.of(param1, param2)));
    }

    @Test
    public void testParseActionImport() throws Exception {
        String uri = "http://some.com/xyz.svc/ODataDemoActionImport";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(ACTION_IMPORT_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        UnboundActionSample action = (UnboundActionSample) parser.getAction();

        assertEquals(42L, action.getNumber());
        assertEquals("BLACKFRIDAY", action.getStringParameter());
    }

    @Test
    public void testEmptyBodyActionImport() throws Exception {
        String uri = "http://some.com/xyz.svc/ODataDemoActionImport";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(EMPTY_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        assertThrows(ODataUnmarshallingException.class, parser::getAction);
    }

    @Test
    public void testIncorrectActionRequestBody() throws Exception {
        String uri = "http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(INCORRECT_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        assertThrows(ODataUnmarshallingException.class, parser::getAction);
    }
}
