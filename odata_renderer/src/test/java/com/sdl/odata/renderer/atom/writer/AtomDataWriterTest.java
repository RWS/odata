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
package com.sdl.odata.renderer.atom.writer;

import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.WriterTest;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.SingletonSample;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;

import static com.sdl.odata.AtomConstants.ATOM_ENTRY;
import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.ODATA_DATA;
import static com.sdl.odata.AtomConstants.ODATA_DATA_NS;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.XML_VERSION;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getAndCheckEntityType;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 * The Atom Data Writer Test.
 */
public class AtomDataWriterTest extends WriterTest {

    private static final String EXPECTED_CUSTOMER_DATA_PATH = "/xml/CustomerData.xml";

    private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private XMLStreamWriter xmlWriter;
    private ByteArrayOutputStream outputStream;
    private Customer customer;
    private AtomNSConfigurationProvider nsConfigurationProvider;

    @Before
    public void setUp() throws Exception {

        super.setUp();
        customer = createCustomerSample();
        outputStream = new ByteArrayOutputStream();
        xmlWriter = xmlOutputFactory.createXMLStreamWriter(outputStream, UTF_8.name());
        nsConfigurationProvider = new ODataV4AtomNSConfigurationProvider();
    }

    @After
    public void tearDown() throws Exception {

        xmlWriter.close();
    }

    private void startDocument() throws XMLStreamException {

        xmlWriter.writeStartDocument(UTF_8.name(), XML_VERSION);
        xmlWriter.setPrefix(METADATA, ODATA_METADATA_NS);
        xmlWriter.setPrefix(ODATA_DATA, ODATA_DATA_NS);
        xmlWriter.writeStartElement(ATOM_ENTRY);
    }

    private void endDocument() throws XMLStreamException {

        xmlWriter.writeEndElement();
        xmlWriter.writeEndDocument();
        xmlWriter.flush();
    }

    @Test
    public void testWriteODataContent() throws Exception {

        startDocument();
        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        AtomMetadataWriter metadataWriter = new AtomMetadataWriter(
                xmlWriter, odataUri, entityDataModel, nsConfigurationProvider);
        AtomDataWriter dataWriter = new AtomDataWriter(xmlWriter, entityDataModel, nsConfigurationProvider);
        metadataWriter.writeODataMetadata(CUSTOMER_URL);
        dataWriter.writeData(customer, getAndCheckEntityType(entityDataModel, customer.getClass()));
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_CUSTOMER_DATA_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteODataSingletonContent() throws Exception {

        SingletonSample singletonSample = createSingletonSample();

        startDocument();
        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/SingletonSample", entityDataModel);
        AtomMetadataWriter metadataWriter = new AtomMetadataWriter(
                xmlWriter, odataUri, entityDataModel, nsConfigurationProvider);
        AtomDataWriter dataWriter;
        dataWriter = new AtomDataWriter(xmlWriter, entityDataModel, nsConfigurationProvider);
        metadataWriter.writeODataMetadata(SINGLETON_SAMPLE_CONTEXT_URL);
        dataWriter.writeData(singletonSample, getAndCheckEntityType(entityDataModel, singletonSample.getClass()));
        endDocument();

        assertEquals(prettyPrintXml(readContent("/xml/singleton/SingletonSample.xml")),
                prettyPrintXml(outputStream.toString()));
    }
}
