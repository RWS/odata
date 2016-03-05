/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Lists;
import com.sdl.odata.JsonConstants;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.renderer.WriterTest;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.Customer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintJson;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * OData Json Property Test.
 *
 */
public class JsonPropertyWriterTest extends WriterTest {

    private JsonPropertyWriter propertyWriter;
    private static final String EXPECTED_ABSTRACT_COMPLEX_TYPE_LIST_PATH = "/json/AbstractComplexTypeListSample.json";
    private static final String EXPECTED_ABSTRACT_COMPLEX_TYPE_PATH = "/json/AbstractComplexTypeSample.json";
    private static final String EXPECTED_ABSTRACT_COMPLEX_TYPE_UTF_PATH = "/json/AbstractComplexTypeUnicodeSample.json";
    private static final String UNICODE_STRING = "Japanese: 日本語 Cyrillic: Кириллица,Кирилиця,Кірыліца,Ћирилица";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        propertyWriter = new JsonPropertyWriter(odataUri, entityDataModel);
    }

    @Test(expected = ODataRenderException.class)
    public void testTypesMismatch() throws ODataException {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");
        propertyWriter.getPropertyAsString(1L);
    }

    @Test(expected = ODataRenderException.class)
    public void testTypesMismatchCollection() throws ODataException {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");

        // Types are not same because expected is collection of strings not string
        propertyWriter.getPropertyAsString("test");
    }

    @Test(expected = ODataRenderException.class)
    public void testTypesMismatchComplexType() throws ODataException {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        propertyWriter.getPropertyAsString(Lists.newArrayList(new Customer()));
    }

    @Test
    public void testEmptyCollection() throws ODataException, IOException {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");
        String result = propertyWriter.getPropertyAsString(Lists.newArrayList());
        assertResult(result, "/odata.svc/$metadata#Customers(1)/Phone", true, true, 0);
    }

    @Test
    public void testJSONPrimitiveProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/id");
        String result = propertyWriter.getPropertyAsString(1L);

        // assertions
        Map<String, Object> resultMap = assertResult(result, "$metadata#Customers(1)/id", false, false, 1);
        assertThat(resultMap.get(JsonConstants.VALUE), is("1"));
    }

    @Test
    public void testGetXMLForNonNullPrimitiveUnicodeProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/name");

        // business method which needs unit test
        String result = propertyWriter.getPropertyAsString(UNICODE_STRING);

        Map<String, Object> resultMap = assertResult(result, "$metadata#Customers(1)/name", false, false, 1);
        assertThat(resultMap.get(JsonConstants.VALUE), is(UNICODE_STRING));
    }

    @Test
    public void testJSONPrimitivePropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");
        String result = propertyWriter.getPropertyAsString(Arrays.asList("test1", "test2"));

        // assertions
        Map<String, Object> resultMap = assertResult(result, "/odata.svc/$metadata#Customers(1)/Phone", true, false, 2);
        assertThat(((List) resultMap.get(JsonConstants.VALUE)).get(0), is("test1"));
        assertThat(((List) resultMap.get(JsonConstants.VALUE)).get(1), is("test2"));
    }

    @Test(expected = ODataRenderException.class)
    public void testJSONNullProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        propertyWriter.getPropertyAsString(null);
    }

    @Test
    public void testEmptyComplexType() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        String result = propertyWriter.getPropertyAsString(Lists.newArrayList());
        assertResult(result, "/odata.svc/$metadata#Customers(1)/address", true, true, 0);
    }

    @Test
    public void testJSONForComplexPropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        String result = propertyWriter.getPropertyAsString(createAddressList());
        Map<String, Object> resultMap = assertResult(result,
                "/odata.svc/$metadata#Customers(1)/address", true, false, 2);
        assertAddressList(resultMap);
    }

    @Test
    public void testJSONForAbstractComplexPropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperties");
        String json = propertyWriter.getPropertyAsString(createComplexTypeListSample());
        System.out.println(json);
        assertEquals(prettyPrintJson(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_LIST_PATH)), prettyPrintJson(json));
    }

    @Test
    public void testJSONForAbstractComplexProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperty");
        String json = propertyWriter.getPropertyAsString(createComplexType("Prop 1", "Inherited 1"));
        System.out.println(json);
        assertEquals(prettyPrintJson(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_PATH)), prettyPrintJson(json));
    }

    @Test
    public void testJSONForComplexPropertyWithUnicodeCharacters() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperty");
        String json = propertyWriter.getPropertyAsString(createComplexType("Prop 1", UNICODE_STRING));
        assertEquals(prettyPrintJson(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_UTF_PATH)), prettyPrintJson(json));
    }

    private void assertAddressList(Map<String, Object> results) {
        Object value = results.get(JsonConstants.VALUE);
        assertThat(value, instanceOf(List.class));
        List values = (List) value;
        int counter = 1;
        for (Object obj : values) {
            assertThat(obj, instanceOf(Map.class));

            @SuppressWarnings("unchecked")
            Map<String, String> address = (Map<String, String>) obj;

            assertThat(address.get("city"), is("city" + counter));
            assertThat(address.get("Street"), is("street" + counter));
            assertThat(address.get("houseNumber"), is("hn" + counter));
            assertThat(address.get("postalCode"), is("postal code" + counter));
            assertThat(address.get("country"), is("country" + counter));
            counter += 1;
        }
    }

    private Map<String, Object> assertResult(String result, String context, boolean isCollectionExpected,
                                             boolean isEmpty, int size) throws IOException {
        Map<String, Object> resultMap = getMapFromJson(result);
        assertTrue(((String) (resultMap.get(JsonConstants.CONTEXT))).endsWith(context));
        Object value = resultMap.get(JsonConstants.VALUE);
        if (isCollectionExpected) {
            assertThat(value, instanceOf(List.class));
            List values = (List) value;
            assertThat(values.isEmpty(), is(isEmpty));
            if (!isEmpty) {
                assertThat(values.size(), is(size));
            }
        }
        return resultMap;
    }

    private List<Address> createAddressList() {
        return Lists.newArrayList(
                new Address().setCity("city1").setCountry("country1")
                        .setHouseNumber("hn1").setPostalCode("postal code1").setStreet("street1"),
                new Address().setCity("city2").setCountry("country2")
                        .setHouseNumber("hn2").setPostalCode("postal code2").setStreet("street2")
        );
    }

    private Map<String, Object> getMapFromJson(String json) throws IOException {
        Map<String, Object> map = new HashMap<>();
        JsonParser jsonParser = new JsonFactory().createParser(json);
        jsonParser.nextToken();
        while (jsonParser.nextToken() != null) {
            String key = jsonParser.getText();
            jsonParser.nextToken();
            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                map.put(key, getJsonArray(jsonParser));
            } else {
                map.put(key, jsonParser.getText());
            }
        }
        return map;
    }

    private List<Object> getJsonArray(JsonParser jsonParser) throws IOException {
        List<Object> objects = new ArrayList<>();
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                Map<String, String> jsonObject = getJsonObject(jsonParser);
                objects.add(jsonObject);
            } else {
                objects.add(jsonParser.getText());
            }
        }
        return objects;
    }

    private Map<String, String> getJsonObject(JsonParser jsonParser) throws IOException {
        Map<String, String> map = new HashMap<>();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getText();
            jsonParser.nextToken();
            map.put(key, jsonParser.getText());
        }
        return map;
    }

    private void prepareForTest(String url) throws ODataRenderException {
        //Preparation
        odataUri = new ODataUriParser(entityDataModel).parseUri(url);
        propertyWriter = new JsonPropertyWriter(odataUri, entityDataModel);
    }
}
