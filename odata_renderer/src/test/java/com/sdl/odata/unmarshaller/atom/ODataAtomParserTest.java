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
package com.sdl.odata.unmarshaller.atom;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.unmarshaller.UnmarshallerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link ODataAtomParser}.
 */
public class ODataAtomParserTest extends UnmarshallerTest {

    private static final String CUSTOMER_WITH_LINKS_READ_OP_ENTITY_PATH = "/xml/CustomerWithLinks.xml";
    private static final String CUSTOMER_WITH_LINKS_PATH_WRITE = "/xml/CustomerWithLinksWrite.xml";

    private static final String CUSTOMER_ENTITY_PATH = "/xml/CustomerWithNoLinks.xml";
    private static final String CUSTOMER_WITH_NO_ADDRESS = "/xml/CustomerWithNoAddress.xml";
    private static final String PRODUCT_ENTITY_PATH = "/xml/Product.xml";
    private static final String PRIMITIVE_TYPES_ENTITY_PATH = "/xml/PrimitiveTypesSample.xml";
    private static final String COLLECTIONS_ENTITY_PATH = "/xml/CollectionsSample.xml";
    private static final String CUSTOMER_FEED_PATH = "/xml/Customers.xml";
    private static final String ODATA_DEMO_XML_SAMPLE = "/xml/ODataDemoFeed.xml";
    private static final String EXPECTED_EXPANDED_PROPERTIES_ENTITY_PATH = "/xml/ExpandedPropertiesSample.xml";
    private static final String EXPECTED_ABSTRACT_ENTITY_PATH = "/xml/AbstractEntitySample.xml";

    private ODataParser uriParser;

    @BeforeEach
    public void setUpParser() {
        uriParser = new ODataParserImpl();
    }

    @Test
    public void testCustomerWithNoOrdersShouldNotThrowException() throws Exception {

        preparePostRequestContext(CUSTOMER_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        singleCustomer = atomParser.getODataEntity();
        assertCustomerSample();
    }

    @Test
    public void testCustomerWithNoAddressShouldThrowException() throws Exception {

        requestBuilder.setUri(odataUri.serviceRoot()).setMethod(ODataRequest.Method.POST);
        preparePostRequestContext(CUSTOMER_WITH_NO_ADDRESS);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        assertThrows(ODataUnmarshallingException.class, () ->
                singleCustomer = atomParser.getODataEntity()
        );
    }

    @Test
    public void testCustomerWithLinksSample() throws Exception {
        preparePostRequestContext(CUSTOMER_WITH_LINKS_PATH_WRITE);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        singleCustomer = atomParser.getODataEntity();
        assertCustomerWithLinksSample();
    }

    @Test
    public void testProductSample() throws Exception {

        preparePostRequestContext(PRODUCT_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        products = atomParser.getODataEntity();
        assertProductSample();
    }

    @Test
    public void testPrimitiveTypesSample() throws Exception {

        preparePostRequestContext(PRIMITIVE_TYPES_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        primitiveTypesSamples = atomParser.getODataEntity();
        assertPrimitiveTypesSample();
    }

    @Test
    public void testCollectionsSample() throws Exception {

        preparePostRequestContext(COLLECTIONS_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        collectionsTypesSamples = atomParser.getODataEntity();
        assertCollectionsTypesSample();
    }

    @Test
    public void testCustomersSample() throws IOException, ODataException {
        preparePostRequestContext(CUSTOMER_FEED_PATH);
        assertThrows(ODataUnmarshallingException.class, () ->
                new ODataAtomParser(context, uriParser).getODataEntity()
        );
    }

    @Test
    public void testCustomersReadSample() throws Exception {

        prepareGetRequestContext(CUSTOMER_FEED_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        customersFeed = atomParser.getODataEntities();
        assertCustomersSample();
    }

    /**
     * This test validates that nested complex types with an attached collection do not interfere
     * with the parent complextype. This is simply asserted by checking that we have
     * the right amount of 'propertydefinitions' which are complex types in the parent.
     */
    @Test
    public void testNestedComplexTypes() throws Exception {
        preparePostRequestContext(ODATA_DEMO_XML_SAMPLE);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        nestedComplexTypesSamples = atomParser.getODataEntity();
        assertNestedComplexTypesSamples();
    }

    @Test
    public void testCustomerWithNavigationPropertiesRead() throws Exception {
        prepareGetRequestContext(CUSTOMER_WITH_LINKS_READ_OP_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        singleCustomer = atomParser.getODataEntity();
        assertCustomerSample();
    }

    @Test
    public void testExpandedPropertiesSample() throws Exception {
        prepareGetRequestContext(EXPECTED_EXPANDED_PROPERTIES_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        expandedPropertiesSamples = atomParser.getODataEntity();
        assertExtendedPropertiesSample();
    }

    @Test
    public void testAbstractEntitySample() throws Exception {
        prepareGetRequestContext(EXPECTED_ABSTRACT_ENTITY_PATH);
        ODataAtomParser atomParser = new ODataAtomParser(context, uriParser);

        entityTypeSample = atomParser.getODataEntity();
        assertAbstractEntityTypeSample();
    }

}
