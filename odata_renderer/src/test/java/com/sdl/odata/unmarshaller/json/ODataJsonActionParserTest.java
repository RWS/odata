/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

/**
 * Test for OData Json Action Parser class.
 */
public class ODataJsonActionParserTest extends UnmarshallerTest {
    private static final String ACTION_PARAMETERS_JSON = "/json/ActionParameters.json";
    private static final String ACTION_IMPORT_PARAMETERS_JSON = "/json/ActionImportParameters.json";
    private static final String EMPTY_PARAMETERS_JSON = "/json/ActionEmpty.json";
    private static final String INCORRECT_PARAMETERS_JSON = "/xml/Product.xml";

    private ODataParser uriParser = new ODataParserImpl();

    @Test
    public void testParseAction() throws Exception {
        String uri = "http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(ACTION_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        ActionSample action = (ActionSample) parser.getAction();
        assertThat(action.getIntNumber(), is(101));
        assertThat(action.getNumber(), is(2L));
        assertThat(action.getStringParameter(), is("BLACKFRIDAY"));
        assertThat(action.getParameters().get("type"), is("binaryLink"));
        assertThat(action.getParameters().get("url"), is("testUrl"));

        Map<String, String> param1 = new HashMap<>();
        param1.put("type", "binaryLink");
        param1.put("url", "testUrl");

        Map<String, String> param2 = new HashMap<>();
        param2.put("type", "binaryLink1");
        param2.put("url", "testUrl1");

        assertThat(action.getParametersList(), hasItems(param1, param2));
    }

    @Test
    public void testParseActionImport() throws Exception {
        String uri = "http://some.com/xyz.svc/ODataDemoActionImport";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(ACTION_IMPORT_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        UnboundActionSample action = (UnboundActionSample) parser.getAction();
        assertThat(action.getNumber(), is(42L));
        assertThat(action.getStringParameter(), is("BLACKFRIDAY"));
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testEmptyBodyActionImport() throws Exception {
        String uri = "http://some.com/xyz.svc/ODataDemoActionImport";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(EMPTY_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        parser.getAction();
    }

    @Test(expected = ODataUnmarshallingException.class)
    public void testIncorrectActionRequestBody() throws Exception {
        String uri = "http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction";
        odataUri = uriParser.parseUri(uri, entityDataModel);
        preparePostRequestContext(INCORRECT_PARAMETERS_JSON);
        ODataJsonActionParser parser = new ODataJsonActionParser(context);
        parser.getAction();
    }
}
