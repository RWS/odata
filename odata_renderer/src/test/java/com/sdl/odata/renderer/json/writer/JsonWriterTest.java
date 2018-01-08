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

import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.renderer.WriterTest;
import com.sdl.odata.test.model.ExpandedPropertiesSample;
import com.sdl.odata.test.model.SingletonSample;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintJson;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static org.junit.Assert.assertEquals;

/**
 * Common test case for testing Atom XML and JSON marshalling process
 * Unit test for {@link JsonWriter}.
 *
 */
public class JsonWriterTest extends WriterTest {

    /* Expected results in JSON */
    private static final String EXPECTED_CUSTOMER_ENTITY_PATH = "/json/Customer.json";
    private static final String EXPECTED_PRODUCT_ENTITY_PATH = "/json/Product.json";
    private static final String EXPECTED_PRODUCT_WITH_METADATA_ENTITY_PATH = "/json/ProductWithMetadata.json";
    private static final String EXPECTED_PRODUCT_NO_METADATA_ENTITY_PATH = "/json/ProductNoMetadata.json";
    private static final String EXPECTED_PRIMITIVE_TYPES_ENTITY_PATH = "/json/PrimitiveTypesSample.json";
    private static final String EXPECTED_COLLECTIONS_ENTITY_PATH = "/json/CollectionsSample.json";
    private static final String EXPECTED_CUSTOMER_FEED_PATH = "/json/Customers.json";
    private static final String EXPECTED_CUSTOMER_FEED_WITH_COUNT_PATH = "/json/CustomersWithCount.json";
    private static final String EXPECTED_EXPANDED_PROPERTIES_ENTITY_PATH = "/json/ExpandedPropertiesSample.json";
    private static final String EXPECTED_EXPANDED_ALL_PROPERTIES_ENTITY_PATH = "/json/ExpandedPropertiesExpandAll.json";
    private static final String EXPANDED_PROPERTIES_NO_LINKS_ENTITY_PATH = "/json/ExpandedPropertiesNoLinksSample.json";
    private static final String EXPECTED_COMPLEX_KEY_ENTITY_PATH = "/json/ComplexKeySample.json";
    private static final String EXPECTED_EMPTY_LIST_PATH = "/json/EmptyList.json";
    private static final String EXPECTED_ABSTRACT_ENTITY_PATH = "/json/AbstractEntitySample.json";
    private static final String EXPECTED_SINGLETON_SAMPLE_ENTITY_PATH = "/json/SingletonSample.json";

    @Test
    public void testCustomerSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers(1)", entityDataModel);
        checkWrittenJsonStream(createCustomerSample(), CUSTOMER_URL, EXPECTED_CUSTOMER_ENTITY_PATH);
    }

    @Test
    public void testSingletonSample() throws Exception {
        SingletonSample singletonSample = createSingletonSample();

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/SingletonSample", entityDataModel);
        checkWrittenJsonStream(singletonSample, SINGLETON_SAMPLE_CONTEXT_URL, EXPECTED_SINGLETON_SAMPLE_ENTITY_PATH);
    }

    @Test
    public void testProductSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Products(1)", entityDataModel);
        checkWrittenJsonStream(createProductSample(), PRODUCT_URL, EXPECTED_PRODUCT_ENTITY_PATH);
    }

    @Test
    public void testProductWithMetadataSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Products(1)", entityDataModel);
        checkWrittenJsonStream(createProductSample(), null, PRODUCT_URL, EXPECTED_PRODUCT_WITH_METADATA_ENTITY_PATH,
            MediaType.METADATA_FULL);
    }

    @Test
    public void testProductNoMetadataSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Products(1)", entityDataModel);
        checkWrittenJsonStream(createProductSample(), null, PRODUCT_URL, EXPECTED_PRODUCT_NO_METADATA_ENTITY_PATH,
            MediaType.METADATA_NONE);
    }

    @Test
    public void testPrimitiveTypesSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/PrimitiveTypesSamples(1)",
                entityDataModel);
        checkWrittenJsonStream(createPrimitiveTypesSample(),
                PRIMITIVE_TYPES_SAMPLE_URL, EXPECTED_PRIMITIVE_TYPES_ENTITY_PATH);
    }

    @Test
    public void testCollectionsSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/CollectionsSamples(1)",
                entityDataModel);
        checkWrittenJsonStream(createCollectionsSample(),
                COLLECTION_SAMPLE_URL, EXPECTED_COLLECTIONS_ENTITY_PATH);
    }

    @Test
    public void testCustomersSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers", entityDataModel);
        checkWrittenJsonStream(createCustomersSample(), CUSTOMERS_URL, EXPECTED_CUSTOMER_FEED_PATH);
    }

    @Test
    public void testCustomersWithCountSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri(
                "http://localhost:8080/odata.svc/Customers?$count=true", entityDataModel);
        Map<String, Object> meta = new HashMap<>();
        meta.put("count", 5);
        checkWrittenJsonStream(createCustomersSample(), meta, CUSTOMERS_URL, EXPECTED_CUSTOMER_FEED_WITH_COUNT_PATH);
    }

    @Test
    public void testExpandedPropertiesSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri(
                "http://localhost:8080/odata.svc/ExpandedPropertiesSamples(1)?$expand=ExpandedEntry,ExpandedFeed",
                entityDataModel);
        checkWrittenJsonStream(createExpandedPropertiesSample(),
                EXPANDED_PROPERTIES_SAMPLE_URL, EXPECTED_EXPANDED_PROPERTIES_ENTITY_PATH);
    }

    @Test
    public void testExpandedPropertiesAll() throws Exception {

        odataUri = new ODataParserImpl().parseUri(
            "http://localhost:8080/odata.svc/ExpandedPropertiesSamples(1)?$expand=*",
            entityDataModel);
        final ExpandedPropertiesSample expandedPropertiesSample = createExpandedPropertiesSample();

        final ExpandedPropertiesSample level2 = new ExpandedPropertiesSample();
        level2.setId(1);
        level2.setName("Inner expanded property");

        expandedPropertiesSample.getExpandedEntry().setInnerExpandedProperty(level2);
        checkWrittenJsonStream(expandedPropertiesSample,
            EXPANDED_PROPERTIES_SAMPLE_URL, EXPECTED_EXPANDED_ALL_PROPERTIES_ENTITY_PATH);
    }

    @Test
    public void testExpandedPropertiesNoLinksSample() throws Exception {

        odataUri = new ODataParserImpl()
                .parseUri("http://localhost:8080/odata.svc/ExpandedPropertiesSamples(1)" +
                        "?$expand=ExpandedEntry,ExpandedFeed", entityDataModel);
        checkWrittenJsonStream(createExpandedPropertiesNoLinksSample(),
                EXPANDED_PROPERTIES_SAMPLE_URL, EXPANDED_PROPERTIES_NO_LINKS_ENTITY_PATH);
    }

    @Test
    public void testComplexKeySample() throws Exception {

        odataUri = new ODataParserImpl()
                .parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(1)", entityDataModel);
        checkWrittenJsonStream(createComplexKeySample(),
                COMPLEX_KEY_SAMPLE_URL, EXPECTED_COMPLEX_KEY_ENTITY_PATH);
    }

    @Test
    public void testEmptyListSample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Customers", entityDataModel);
        checkWrittenJsonStream(Collections.EMPTY_LIST, CUSTOMERS_URL, EXPECTED_EMPTY_LIST_PATH);
    }

    @Test
    public void testAbstractEntitySample() throws Exception {

        odataUri = new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/EntityTypeSamples", entityDataModel);
        checkWrittenJsonStream(createEntityTypeSample(),
                ABSTRACT_ENTITY_SAMPLE_URL, EXPECTED_ABSTRACT_ENTITY_PATH);
    }

    /**
     * Checks output json with expected result.
     *
     * @param data               Object(s) to write.
     * @param contextURL         The 'Context URL' to write.
     * @param expectedEntityPath Path to the file with the expected XML stream.
     * @throws IOException
     * @throws ODataRenderException if unable to render
     */
    private void checkWrittenJsonStream(Object data, String contextURL, String expectedEntityPath)
            throws IOException, ODataRenderException {

        checkWrittenJsonStream(data, null, contextURL, expectedEntityPath);
    }

    /**
     * Checks output json with expected result.
     *
     * @param data               Object(s) to write.
     * @param meta               Additional metadata to write.
     * @param contextURL         The 'Context URL' to write.
     * @param expectedEntityPath Path to the file with the expected XML stream.
     * @throws IOException
     * @throws ODataRenderException if unable to render
     */
    private void checkWrittenJsonStream(Object data, Map<String, Object> meta, String contextURL,
                                        String expectedEntityPath)
            throws IOException, ODataRenderException {

        checkWrittenJsonStream(data, meta, contextURL, expectedEntityPath, null);
    }

    /**
     * Checks output json with expected result.
     *
     * @param data               Object(s) to write.
     * @param meta               Additional metadata to write.
     * @param contextURL         The 'Context URL' to write.
     * @param expectedEntityPath Path to the file with the expected XML stream.
     * @param metadataRequest    odata.medatada request parameter.
     * @throws IOException
     * @throws ODataRenderException if unable to render
     */
    private void checkWrittenJsonStream(Object data, Map<String, Object> meta, String contextURL,
        String expectedEntityPath, String metadataRequest)
        throws IOException, ODataRenderException {

        JsonWriter writer = new JsonWriter(odataUri, entityDataModel, metadataRequest);

        String jsonStream;
        if (data instanceof List) {
            jsonStream = writer.writeFeed((List<?>) data, contextURL, meta);
        } else {
            jsonStream = writer.writeEntry(data, contextURL);
        }

        assertEquals(prettyPrintJson(readContent(expectedEntityPath)), prettyPrintJson(jsonStream));
    }
}
