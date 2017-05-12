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

import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.WriterTest;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link AtomWriter}.
 */
public class AtomWriterTest extends WriterTest {

    private static final String EXPECTED_CUSTOMER_ENTITY_PATH = "/xml/CustomerWithEmptyLinks.xml";
    private static final String EXPECTED_CUSTOMER_ENTITY_PATH_WRITE = "/xml/CustomerWithEmptyLinksWrite.xml";

    private static final String EXPECTED_CUSTOMER_ENTITY_WITH_LINKS_PATH = "/xml/CustomerWithLinks.xml";
    private static final String EXPECTED_CUSTOMER_ENTITY_WITH_LINKS_PATH_WRITE = "/xml/CustomerWithLinksWrite.xml";

    private static final String EXPECTED_PRODUCT_ENTITY_PATH = "/xml/Product.xml";
    private static final String EXPECTED_PRIMITIVE_TYPES_ENTITY_PATH = "/xml/PrimitiveTypesSample.xml";
    private static final String EXPECTED_COLLECTIONS_ENTITY_PATH = "/xml/CollectionsSample.xml";

    private static final String EXPECTED_CUSTOMER_FEED_PATH = "/xml/Customers.xml";
    private static final String EXPECTED_CUSTOMER_FEED_WITH_COUNT_PATH = "/xml/CustomersWithCount.xml";
    private static final String EXPECTED_CUSTOMER_FEED_PATH_WRITE = "/xml/CustomersWrite.xml";

    private static final String EXPECTED_EXPANDED_PROPERTIES_ENTITY_PATH = "/xml/ExpandedPropertiesSample.xml";
    private static final String EXPECTED_EXPANDED_PROPERTIES_NO_LINKS_ENTITY_PATH
            = "/xml/ExpandedPropertiesNoLinksSample.xml";
    private static final String EXPECTED_COMPLEX_KEY_ENTITY_PATH = "/xml/ComplexKeySample.xml";
    private static final String EXPECTED_ABSTRACT_ENTITY_PATH = "/xml/AbstractEntitySample.xml";


    @Test
    public void testCustomerSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        checkWrittenXmlStream(createCustomerSample(), CUSTOMER_URL, EXPECTED_CUSTOMER_ENTITY_PATH, false);
    }

    @Test
    public void testCustomerSampleWrite() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        checkWrittenXmlStream(createCustomerSample(), CUSTOMER_URL, EXPECTED_CUSTOMER_ENTITY_PATH_WRITE, true);
    }

    @Test
    public void testCustomerWithLinkSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        checkWrittenXmlStream(createCustomerWithLinkSample(), CUSTOMER_URL,
                EXPECTED_CUSTOMER_ENTITY_WITH_LINKS_PATH, false);
    }

    @Test
    public void testCustomerWithLinkSampleWrite() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        checkWrittenXmlStream(createCustomerWithLinkSample(), CUSTOMER_URL,
                EXPECTED_CUSTOMER_ENTITY_WITH_LINKS_PATH_WRITE, true);
    }

    @Test
    public void testProductSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Products(1)", entityDataModel);
        checkWrittenXmlStream(createProductSample(), PRODUCT_URL, EXPECTED_PRODUCT_ENTITY_PATH, false);
    }

    @Test
    public void testPrimitiveTypesSample() throws Exception {

        odataUri
                = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/PrimitiveTypesSamples(1)",
                entityDataModel);
        checkWrittenXmlStream(createPrimitiveTypesSample(), PRIMITIVE_TYPES_SAMPLE_URL,
                EXPECTED_PRIMITIVE_TYPES_ENTITY_PATH, false);
    }

    @Test
    public void testCollectionsSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/CollectionsSamples(40)",
                entityDataModel);
        checkWrittenXmlStream(createCollectionsSample(), COLLECTION_SAMPLE_URL,
                EXPECTED_COLLECTIONS_ENTITY_PATH, false);
    }

    @Test
    public void testCustomersSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers", entityDataModel);
        checkWrittenXmlStream(createCustomersSample(), CUSTOMERS_URL, EXPECTED_CUSTOMER_FEED_PATH, false);
    }

    @Test
    public void testCustomersWithCountSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers?$count=true",
                entityDataModel);

        Map<String, Object> meta = new HashMap<>();
        meta.put("count", 5);
        checkWrittenXmlStream(createCustomersSample(), meta, CUSTOMERS_URL,
                EXPECTED_CUSTOMER_FEED_WITH_COUNT_PATH, false);
    }

    @Test
    public void testCustomersSampleWrite() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers", entityDataModel);
        checkWrittenXmlStream(createCustomersSample(), CUSTOMERS_URL, EXPECTED_CUSTOMER_FEED_PATH_WRITE, true);
    }

    @Test
    public void testExpandedPropertiesSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri(
                "http://localhost:8080/odata.svc/ExpandedPropertiesSamples(1)?$expand=ExpandedEntry,ExpandedFeed",
                entityDataModel);
        checkWrittenXmlStream(createExpandedPropertiesSample(), EXPANDED_PROPERTIES_SAMPLE_URL,
                EXPECTED_EXPANDED_PROPERTIES_ENTITY_PATH, false);
    }

    @Test
    public void testExpandedPropertiesNoLinksSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri(
                "http://localhost:8080/odata.svc/ExpandedPropertiesSamples(5)?$expand=ExpandedEntry,ExpandedFeed",
                entityDataModel);
        checkWrittenXmlStream(createExpandedPropertiesNoLinksSample(), EXPANDED_PROPERTIES_SAMPLE_URL,
                EXPECTED_EXPANDED_PROPERTIES_NO_LINKS_ENTITY_PATH, false);
    }

    @Test
    public void testComplexKeySample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(1)",
                entityDataModel);
        checkWrittenXmlStream(createComplexKeySample(), COMPLEX_KEY_SAMPLE_URL,
                EXPECTED_COMPLEX_KEY_ENTITY_PATH, false);
    }

    @Test
    public void testAbstractEntitySample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/EntityTypeSamples('id.10')",
                entityDataModel);
        checkWrittenXmlStream(createEntityTypeSample(),
                ABSTRACT_ENTITY_SAMPLE_URL, EXPECTED_ABSTRACT_ENTITY_PATH, false);
    }

    /**
     * Perform the actual test by specifying the entity(es) to write and the path to the file containing the expected
     * written XML stream.
     *
     * @param data               The entity(es) to write.
     * @param contextURL         The 'Context URL' to write.
     * @param expectedEntityPath Path to the file with the expected XML stream.
     * @param isWriteOperation   Boolean indicating that we are testing a write operation.
     * @throws ODataRenderException If unable to render
     * @throws IOException
     * @throws TransformerException
     */
    private void checkWrittenXmlStream(Object data, String contextURL, String expectedEntityPath,
                                       boolean isWriteOperation)
            throws ODataRenderException, IOException, TransformerException {
        checkWrittenXmlStream(data, null, contextURL, expectedEntityPath, isWriteOperation);
    }

    /**
     * Perform the actual test by specifying the entity(es) to write and the path to the file containing the expected
     * written XML stream.
     *
     * @param data               The entity(es) to write.
     * @param meta               Additional metadata to write.
     * @param contextURL         the 'Context URL' to write.
     * @param expectedEntityPath path to the file with the expected XML stream.
     * @param isWriteOperation   boolean indicating that we are testing a write operation
     * @throws ODataRenderException if unable to render
     * @throws IOException
     * @throws TransformerException
     */
    private void checkWrittenXmlStream(Object data, Map<String, Object> meta, String contextURL,
                                       String expectedEntityPath, boolean isWriteOperation)
            throws ODataRenderException, IOException, TransformerException {

        ZonedDateTime dateTime = ZonedDateTime.of(2014, 5, 2, 0, 0, 0, 0, ZoneId.of("UTC").normalized());

        AtomWriter writer = new AtomWriter(dateTime, odataUri, entityDataModel,
                new ODataV4AtomNSConfigurationProvider(), isWriteOperation, false);

        writer.startDocument();
        if (data instanceof List) {
            writer.writeFeed((List<?>) data, contextURL, meta);
        } else {
            writer.writeEntry(data, contextURL);
        }
        writer.endDocument();

        assertEquals(prettyPrintXml(readContent(expectedEntityPath)), prettyPrintXml(writer.getXml()));
    }
}
