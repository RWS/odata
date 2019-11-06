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
package com.sdl.odata.renderer.xml.writer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.renderer.WriterTest;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.ComplexTypeSample;
import com.sdl.odata.test.model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.VALUE;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * This is unit test for {@link XMLPropertyWriter}.
 */
public class XMLPropertyWriterTest extends WriterTest {

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final String UNICODE_STRING = "Japanese: 日本語 Cyrillic: Кириллица,Кирилиця,Кірыліца,Ћирилица";
    private XMLPropertyWriter propertyWriter;
    private static final String EXPECTED_ABSTRACT_COMPLEX_TYPE_LIST_PATH = "/xml/AbstractComplexTypeListSample.xml";
    private static final String EXPECTED_ABSTRACT_COMPLEX_TYPE_PATH = "/xml/AbstractComplexTypeSample.xml";
    private static final String EXPECTED_ABSTRACT_COMPLEX_TYPE_UTF_PATH = "/xml/AbstractComplexTypeUnicodeSample.xml";

    @Before
    public void setUp() throws Exception {
        super.setUp();
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
        propertyWriter.getPropertyAsString(newArrayList(new Customer()));
    }

    @Test
    public void testEmptyCollection() throws ODataException {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");
        String result = propertyWriter.getPropertyAsString(newArrayList());

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);
        assertAttributes(nodeList, 3, "metadata:context",
                "http://localhost:8080/odata.svc/$metadata#Customers(1)/Phone");
    }

    @Test
    public void testGetXMLForNonNullPrimitiveProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/id");

        // business method which needs unit test
        String xml = propertyWriter.getPropertyAsString(1L);

        // Checking expected values
        NodeList nodeList = assertNodeList(xml, 1);
        assertThat(nodeList.item(0).getTextContent(), is("1"));
        assertAttributes(nodeList, 4, "metadata:type", "Int64");
    }

    @Test
    public void testGetXMLForNonNullPrimitiveUnicodeProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/name");

        // business method which needs unit test
        String xml = propertyWriter.getPropertyAsString(UNICODE_STRING);

        // Checking expected values
        NodeList nodeList = assertNodeList(xml, 1);
        assertThat(nodeList.item(0).getTextContent(), is(UNICODE_STRING));
    }

    @Test
    public void testGetXMLForNonNullPrimitivePropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");

        // business method which needs unit test
        String xml = propertyWriter.getPropertyAsString(Arrays.asList("test1", "test2", "test3"));

        // Checking expected values
        NodeList nodeList = assertNodeList(xml, 1);

        Node node = assertAndGetElement(nodeList, 3, 0);
        assertThat(node.getTextContent(), is("test1"));

        node = assertAndGetElement(nodeList, 3, 1);
        assertThat(node.getTextContent(), is("test2"));

        node = assertAndGetElement(nodeList, 3, 2);
        assertThat(node.getTextContent(), is("test3"));

        assertAttributes(nodeList, 3, "metadata:context",
                "http://localhost:8080/odata.svc/$metadata#Customers(1)/Phone");
        metaDataTypeShouldNotPresent(nodeList, 3);
    }

    @Test
    public void testGetXMLForNullProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        String xml = propertyWriter.getPropertyAsString(null);

        // Checking expected values
        NodeList nodeList = assertNodeList(xml, 1);
        assertAttributes(nodeList, 3, "metadata:null", "true");
        assertAttributes(nodeList, 3, "metadata:context",
                "http://localhost:8080/odata.svc/$metadata#Customers(1)/address");
    }

    @Test
    public void testGetXMLForComplexPropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");

        // business method which needs unit test
        String xml = propertyWriter.getPropertyAsString(createAddressList());

        // Checking expected values
        NodeList nodeList = assertNodeList(xml, 1);
        assertAttributes(nodeList, 4, "metadata:type", "#ODataDemo.Address");
        Node firstElement = assertAndGetElement(nodeList, 2, 0);

        assertThat(firstElement.getChildNodes().getLength(), is(5));
        Node streetNode = firstElement.getChildNodes().item(0);
        assertThat(streetNode.getNodeName(), is("Street"));
        assertThat(streetNode.getTextContent(), is("first street"));

        Node secondElement = assertAndGetElement(nodeList, 2, 1);
        assertThat(secondElement.getChildNodes().getLength(), is(5));
        streetNode = secondElement.getChildNodes().item(0);
        assertThat(streetNode.getNodeName(), is("Street"));
        assertThat(streetNode.getTextContent(), is("second street"));
    }

    @Test
    public void testGetXMLForAbstractComplexPropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperties");
        String xml = propertyWriter.getPropertyAsString(createComplexTypeListSample());
        assertEquals(prettyPrintXml(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_LIST_PATH)), prettyPrintXml(xml));
    }

    @Test
    public void testJSONForComplexPropertyWithUnicodeCharacters() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperty");
        String xml = propertyWriter.getPropertyAsString(createComplexType("Prop 1", UNICODE_STRING));
        assertEquals(prettyPrintXml(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_UTF_PATH)), prettyPrintXml(xml));
    }

    @Test
    public void testGetXMLForAbstractComplexProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperty");
        String xml = propertyWriter.getPropertyAsString(createComplexType("Prop 1", "Inherited 1"));
        assertEquals(prettyPrintXml(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_PATH)), prettyPrintXml(xml));
    }

    @Test
    public void testChunkedXMLForNonNullPrimitiveProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/id");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Long id = 1L;
        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(id, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(id, startResult);
        propertyWriter.getPropertyEndDocument(id, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(id);

        assertEquals(result, streamResult);

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);
        assertThat(nodeList.item(0).getTextContent(), is("1"));
        assertAttributes(nodeList, 4, "metadata:type", "Int64");
    }

    @Test
    public void testChunkedEmptyCollection() throws ODataException, UnsupportedEncodingException {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ArrayList<Object> data = newArrayList();
        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(data, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(data, startResult);
        propertyWriter.getPropertyEndDocument(data, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(data);

        assertEquals(result, streamResult);

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);
        assertAttributes(nodeList, 3, "metadata:context",
                "http://localhost:8080/odata.svc/$metadata#Customers(1)/Phone");
    }

    @Test
    public void testChunkedXMLForNonNullPrimitiveUnicodeProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/name");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(UNICODE_STRING, os);
        Type type = propertyWriter.getTypeFromODataUri();
        propertyWriter.validateRequestChunk(type, UNICODE_STRING);
        startResult.setTypeValidated(true);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(UNICODE_STRING, startResult);
        propertyWriter.getPropertyEndDocument(UNICODE_STRING, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(UNICODE_STRING);

        assertEquals(result, streamResult);

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);
        assertThat(nodeList.item(0).getTextContent(), is(UNICODE_STRING));
    }

    @Test
    public void testChunkedXMLForNonNullPrimitivePropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/Phone");
        List<String> testList = Arrays.asList("test1", "test2", "test3");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(testList, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(testList, startResult);
        propertyWriter.getPropertyEndDocument(testList, bodyResult);

        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(testList);

        assertEquals(result, streamResult);

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);

        Node node = assertAndGetElement(nodeList, 3, 0);
        assertThat(node.getTextContent(), is("test1"));

        node = assertAndGetElement(nodeList, 3, 1);
        assertThat(node.getTextContent(), is("test2"));

        node = assertAndGetElement(nodeList, 3, 2);
        assertThat(node.getTextContent(), is("test3"));

        assertAttributes(nodeList, 3, "metadata:context",
                "http://localhost:8080/odata.svc/$metadata#Customers(1)/Phone");
        metaDataTypeShouldNotPresent(nodeList, 3);
    }

    @Test
    public void testChunkedXMLForNullProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(null, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(null, startResult);
        propertyWriter.getPropertyEndDocument(null, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(null);

        assertEquals(result, streamResult);

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);
        assertAttributes(nodeList, 3, "metadata:null", "true");
        assertAttributes(nodeList, 3, "metadata:context",
                "http://localhost:8080/odata.svc/$metadata#Customers(1)/address");
    }

    @Test
    public void testChunkedXMLForComplexPropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/Customers(1)/address");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        List<Address> addresses = createAddressList();

        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(addresses, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(addresses, startResult);
        propertyWriter.getPropertyEndDocument(addresses, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(addresses);

        assertEquals(result, streamResult);

        // Checking expected values
        NodeList nodeList = assertNodeList(result, 1);
        assertAttributes(nodeList, 4, "metadata:type", "#ODataDemo.Address");
        Node firstElement = assertAndGetElement(nodeList, 2, 0);

        assertThat(firstElement.getChildNodes().getLength(), is(5));
        Node streetNode = firstElement.getChildNodes().item(0);
        assertThat(streetNode.getNodeName(), is("Street"));
        assertThat(streetNode.getTextContent(), is("first street"));

        Node secondElement = assertAndGetElement(nodeList, 2, 1);
        assertThat(secondElement.getChildNodes().getLength(), is(5));
        streetNode = secondElement.getChildNodes().item(0);
        assertThat(streetNode.getNodeName(), is("Street"));
        assertThat(streetNode.getTextContent(), is("second street"));
    }

    @Test
    public void testChunkedXMLForAbstractComplexPropertyList() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperties");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        List<ComplexTypeSample> complexTypeListSample = createComplexTypeListSample();
        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(complexTypeListSample, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(complexTypeListSample,
                startResult);
        propertyWriter.getPropertyEndDocument(complexTypeListSample, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(complexTypeListSample);

        assertEquals(result, streamResult);

        assertEquals(prettyPrintXml(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_LIST_PATH)), prettyPrintXml(result));
    }

    @Test
    public void testChunkedXMLForComplexPropertyWithUnicodeCharacters() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperty");
        ComplexTypeSample complexType = createComplexType("Prop 1", UNICODE_STRING);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(complexType, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(complexType, startResult);
        propertyWriter.getPropertyEndDocument(complexType, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(complexType);

        assertEquals(result, streamResult);

        assertEquals(prettyPrintXml(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_UTF_PATH)), prettyPrintXml(result));
    }

    @Test
    public void testChunkedXMLForAbstractComplexProperty() throws Exception {
        prepareForTest("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')/ComplexTypeProperty");
        ComplexTypeSample complexType = createComplexType("Prop 1", "Inherited 1");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ChunkedActionRenderResult startResult = propertyWriter.getPropertyStartDocument(complexType, os);
        ChunkedActionRenderResult bodyResult = propertyWriter.getPropertyBodyDocument(complexType, startResult);
        propertyWriter.getPropertyEndDocument(complexType, bodyResult);
        String streamResult = os.toString(UTF_8.toString());

        String result = propertyWriter.getPropertyAsString(complexType);

        assertEquals(result, streamResult);

        assertEquals(prettyPrintXml(readContent(EXPECTED_ABSTRACT_COMPLEX_TYPE_PATH)), prettyPrintXml(result));
    }

    private void prepareForTest(String url) throws ODataRenderException {
        //Preparation
        odataUri = new ODataUriParser(entityDataModel).parseUri(url);
        propertyWriter = new XMLPropertyWriter(odataUri, entityDataModel);
    }

    private Node assertAndGetElement(NodeList nodeList, int expectedElements, int elementPosition) {
        Node valueNode = nodeList.item(0);
        assertThat(valueNode.getChildNodes().getLength(), is(expectedElements));
        assertTrue(valueNode.getChildNodes().getLength() > elementPosition);
        Node element = valueNode.getChildNodes().item(elementPosition);
        assertThat(element, is(not(nullValue())));
        return element;
    }

    private NamedNodeMap assertNodeMap(NodeList nodeList, int expected) {
        NamedNodeMap nodeMap = nodeList.item(0).getAttributes();
        assertThat(nodeMap, is(not(nullValue())));
        assertThat(nodeMap.getLength(), is(expected));
        return nodeMap;
    }

    private void assertAttributes(NodeList nodeList, int expectedAttributes,
                                  String attributeName, String expectedValue) {
        NamedNodeMap nodeMap = assertNodeMap(nodeList, expectedAttributes);
        Node typeAttribute = nodeMap.getNamedItem(attributeName);
        assertThat(typeAttribute, is(not(nullValue())));
        assertThat(typeAttribute.getNodeValue(), is(expectedValue));
    }

    private void metaDataTypeShouldNotPresent(NodeList nodeList, int expectedAttributes) {
        NamedNodeMap nodeMap = assertNodeMap(nodeList, expectedAttributes);
        Node typeAttribute = nodeMap.getNamedItem("metadata:type");
        assertThat(typeAttribute, is(nullValue()));
    }

    private Document parseXML(String xml) {
        try {
            DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
            return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private NodeList assertNodeList(String result, int expectedNodes) {
        Document parsedDocument = parseXML(result);
        assertThat(parsedDocument, is(not(nullValue())));

        NodeList nodeList = parsedDocument.getElementsByTagNameNS(ODATA_METADATA_NS, VALUE);
        assertThat(nodeList, is(not(nullValue())));
        assertThat(nodeList.getLength(), is(expectedNodes));
        return nodeList;
    }

    private List<Address> createAddressList() {
        return newArrayList(
                new Address().setCity("first city").setCountry("first country")
                        .setHouseNumber("first hn").setPostalCode("first postal code").setStreet("first street"),
                new Address().setCity("second city").setCountry("second country")
                        .setHouseNumber("second hn").setPostalCode("second postal code").setStreet("second street")
        );
    }
}
