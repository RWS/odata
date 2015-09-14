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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.sdl.odata.AtomConstants.ATOM_ENTRY;
import static com.sdl.odata.AtomConstants.ATOM_FEED;
import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.ODATA_DATA;
import static com.sdl.odata.AtomConstants.ODATA_DATA_NS;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.XML_VERSION;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 * The Atom Metadata Writer Test.
 */
public class AtomMetadataWriterTest extends WriterTest {

    private static final String EXPECTED_FEED_METADATA_PATH = "/xml/FeedODataMetadata.xml";
    private static final String EXPECTED_ENTRY_METADATA_PATH = "/xml/EntryODataMetadata.xml";
    private static final String EXPECTED_FEED_ID_PATH = "/xml/FeedId.xml";
    private static final String EXPECTED_FEED_LINK_PATH = "/xml/FeedLink.xml";
    private static final String EXPECTED_ENTRY_ID_PATH = "/xml/EntryId.xml";
    private static final String EXPECTED_ENTRY_TITLE_PATH = "/xml/EntryTitle.xml";
    private static final String EXPECTED_ENTRY_SUMMARY_PATH = "/xml/EntrySummary.xml";
    private static final String EXPECTED_ENTRY_UPDATE_PATH = "/xml/EntryUpdate.xml";
    private static final String EXPECTED_ENTRY_AUTHOR_PATH = "/xml/EntryAuthor.xml";
    private static final String EXPECTED_ENTRY_ENTITY_LINK_PATH = "/xml/EntryEntityLink.xml";
    private static final String EXPECTED_ENTRY_CATEGORY_PATH = "/xml/EntryCategory.xml";

    private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private XMLStreamWriter xmlWriter;
    private AtomMetadataWriter metadataWriter;
    private ByteArrayOutputStream outputStream;
    private Customer customer;

    @Before
    public void setUp() throws Exception {

        super.setUp();
        customer = createCustomerSample();
        outputStream = new ByteArrayOutputStream();
        xmlWriter = xmlOutputFactory.createXMLStreamWriter(outputStream, UTF_8.name());
    }

    @After
    public void tearDown() throws Exception {

        xmlWriter.close();
    }

    private void startDocument(boolean isFeed) throws XMLStreamException {

        xmlWriter.writeStartDocument(UTF_8.name(), XML_VERSION);
        xmlWriter.setPrefix(METADATA, ODATA_METADATA_NS);
        xmlWriter.setPrefix(ODATA_DATA, ODATA_DATA_NS);
        if (isFeed) {
            xmlWriter.writeStartElement(ATOM_FEED);
        } else {
            xmlWriter.writeStartElement(ATOM_ENTRY);
        }
    }

    private void endDocument() throws XMLStreamException {

        xmlWriter.writeEndElement();
        xmlWriter.writeEndDocument();
        xmlWriter.flush();
    }

    @Test
    public void testWriteODataMetadataFeed() throws Exception {

        startDocument(true);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeODataMetadata(CUSTOMERS_URL);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_FEED_METADATA_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteODataMetadataEntry() throws Exception {
        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeODataMetadata(CUSTOMER_URL);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_METADATA_PATH)),
                prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteFeedId() throws Exception {

        startDocument(true);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeFeedId();
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_FEED_ID_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteFeedLink() throws Exception {

        startDocument(true);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeFeedLink(null, null);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_FEED_LINK_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteFeedLinkByCallingAction() throws Exception {

        startDocument(true);
        odataUri = new ODataParserImpl()
                .parseUri("http://localhost:8080/odata.svc/Customers(10)/ODataDemo.ODataDemoAction", entityDataModel);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeFeedLink(null, null);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_FEED_LINK_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteEntryId() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(10)", entityDataModel);
        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeEntryId(customer);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_ID_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteTitle() throws Exception {

        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeTitle();
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_TITLE_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteSummary() throws Exception {

        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeSummary();
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_SUMMARY_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteUpdate() throws Exception {

        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        ZonedDateTime dateTime = ZonedDateTime.of(2014, 5, 27, 23, 0, 0, 0, ZoneId.of("UTC").normalized());
        metadataWriter.writeUpdate(dateTime);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_UPDATE_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteAuthor() throws Exception {

        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeAuthor();
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_AUTHOR_PATH)), prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteEntryEntityLink() throws Exception {

        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeEntryEntityLink(customer);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_ENTITY_LINK_PATH)),
                prettyPrintXml(outputStream.toString()));
    }

    @Test
    public void testWriteEntryCategory() throws Exception {

        startDocument(false);
        metadataWriter = new AtomMetadataWriter(xmlWriter, odataUri, entityDataModel);
        metadataWriter.writeEntryCategory(customer);
        endDocument();

        assertEquals(prettyPrintXml(readContent(EXPECTED_ENTRY_CATEGORY_PATH)),
                prettyPrintXml(outputStream.toString()));
    }
}
