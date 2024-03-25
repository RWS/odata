/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.test.model.ExpandedPropertiesSample;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OData Json Parser test.
 *
 */
public class ODataJsonParserTest extends UnmarshallerTest {

    private static final String CUSTOMER_ENTITY_PATH = "/json/Customer.json";
    private static final String CUSTOMER_WITH_NO_ADDRESS_ENTITY_PATH = "/json/CustomerWithNoAddress.json";
    private static final String CUSTOMER_WITH_LINKS_ENTITY_PATH = "/json/CustomerWithLinks.json";
    private static final String PRODUCT_ENTITY_PATH = "/json/Product.json";
    private static final String PRIMITIVE_TYPES_ENTITY_PATH = "/json/PrimitiveTypesSample.json";
    private static final String COLLECTIONS_ENTITY_PATH = "/json/CollectionsSample.json";
    private static final String CUSTOMER_FEED_PATH = "/json/Customers.json";
    private static final String ODATA_DEMO_SAMPLE = "/json/ODataDemoSample.json";
    private static final String EXPANDED_PROPERTIES_PATH = "/json/ExpandedPropertiesSample.json";
    private static final String ABSTRACT_ENTITY_PATH = "/json/AbstractEntitySample.json";

    private ODataJsonParser jsonParser;
    private ODataParser uriParser;

    @BeforeEach
    public void setUpParser() {
        uriParser = new ODataParserImpl();
    }

    @Test
    public void testShouldThrowExceptionAsOrdersIsNull() throws Exception {

        requestBuilder.setUri(odataUri.serviceRoot()).setMethod(ODataRequest.Method.POST);
        preparePostRequestContext(CUSTOMER_ENTITY_PATH);

        jsonParser = new ODataJsonParser(context, uriParser);
        assertThrows(ODataUnmarshallingException.class, () ->
                singleCustomer = jsonParser.getODataEntity()
        );

    }

    @Test
    public void testShouldNotThrowExceptionAsAddressIsNullANDMethodIsNotPost() throws Exception {

        prepareGetRequestContext(CUSTOMER_WITH_NO_ADDRESS_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        singleCustomer = jsonParser.getODataEntity();
    }

    @Test
    public void testShouldThrowExceptionAsAddressIsNull() throws Exception {

        requestBuilder.setUri(odataUri.serviceRoot()).setMethod(ODataRequest.Method.POST);
        preparePostRequestContext(CUSTOMER_WITH_NO_ADDRESS_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        assertThrows(ODataUnmarshallingException.class, () ->
                singleCustomer = jsonParser.getODataEntity()
        );
    }

    @Test
    public void testCustomerWithLinksSample() throws Exception {

        preparePostRequestContext(CUSTOMER_WITH_LINKS_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        singleCustomer = jsonParser.getODataEntity();
        assertCustomerWithLinksSample();
    }

    @Test
    public void testProductSample() throws Exception {

        createODataUri(SERVICE_ROOT, "Products");
        preparePostRequestContext(PRODUCT_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        products = jsonParser.getODataEntity();
        assertProductSample();
    }

    @Test
    public void testPrimitiveTypesSample() throws Exception {

        createODataUri(SERVICE_ROOT, "PrimitiveTypesSamples");
        preparePostRequestContext(PRIMITIVE_TYPES_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        primitiveTypesSamples = jsonParser.getODataEntity();
        assertPrimitiveTypesSample();
    }

    @Test
    public void testCollectionsSample() throws Exception {

        createODataUri(SERVICE_ROOT, "CollectionsSamples");
        preparePostRequestContext(COLLECTIONS_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        collectionsTypesSamples = jsonParser.getODataEntity();
        assertCollectionsTypesSample();
    }

    @Test
    public void testCustomersSample() throws ODataException, IOException {
        preparePostRequestContext(CUSTOMER_FEED_PATH);
        assertThrows(ODataUnmarshallingException.class, () ->
                new ODataJsonParser(context, uriParser).getODataEntity()
        );
    }

    // Note: Parsing a feed using the Json parser is not supported yet
    @Test
    @Disabled
    public void testCustomersReadSample() throws Exception {

        prepareGetRequestContext(CUSTOMER_FEED_PATH);
        customersFeed = jsonParser.getODataEntities();
        assertCustomersSample();
    }

    @Test
    /**
     * This test validates that nested complex types with an attached collection
     * do not interfere with the parent complextype. This is simply
     * asserted by checking that we have the right amount of
     * 'propertydefinitions' which are complex types in the parent.
     */
    public void testNestedComplexTypes() throws Exception {

        createODataUri(SERVICE_ROOT, "ODataDemoEntities");
        preparePostRequestContext(ODATA_DEMO_SAMPLE);
        jsonParser = new ODataJsonParser(context, uriParser);

        nestedComplexTypesSamples = jsonParser.getODataEntity();
        assertNestedComplexTypesSamples();
    }

    // this test has been ignored since we remove JsonPossibleTypeMatcher
    // because of critical CM issue - it shouldn't be ignored after future changes
    @Disabled
    @Test
    public void testInlineFeedAndEntries() throws Exception {
        prepareGetRequestContext(EXPANDED_PROPERTIES_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        Object givenEntity = jsonParser.getODataEntity();
        assertTrue(givenEntity instanceof ExpandedPropertiesSample);

        ExpandedPropertiesSample targetSample = (ExpandedPropertiesSample) givenEntity;

        assertEquals(5L, targetSample.getId());
        assertEquals("Expanded Properties Sample", targetSample.getName());
        assertNotNull(targetSample.getExpandedEntry());
        assertNotNull(targetSample.getExpandedFeed());
        assertEquals(2, targetSample.getExpandedFeed().size());

        IdNamePairSample expandedEntry = targetSample.getExpandedEntry();
        assertEquals(10L, expandedEntry.getId());
        assertEquals("Expanded entry", expandedEntry.getName());

        List<IdNamePairSample> feeds = targetSample.getExpandedFeed();
        assertNotNull(feeds);
        IdNamePairSample entryFeed1 = feeds.get(0);
        IdNamePairSample entryFeed2 = feeds.get(1);

        assertNotSame(entryFeed1, entryFeed2);
        assertEquals(10L, entryFeed1.getId());
        assertEquals("Expanded feed entry 1", entryFeed1.getName());

        assertEquals(20L, entryFeed2.getId());
        assertEquals("Expanded feed entry 2", entryFeed2.getName());
    }

    @Test
    public void testAbstractEntitySample() throws Exception {

        createODataUri(SERVICE_ROOT, "EntityTypeSamples");
        preparePostRequestContext(ABSTRACT_ENTITY_PATH);
        jsonParser = new ODataJsonParser(context, uriParser);

        entityTypeSample = jsonParser.getODataEntity();
        assertAbstractEntityTypeSample();
    }
}
