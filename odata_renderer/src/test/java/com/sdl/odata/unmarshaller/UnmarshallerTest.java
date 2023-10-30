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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.WriterUnmarshallerTest;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.BankAccount;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.CollectionsSample;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.EntityTypeSample;
import com.sdl.odata.test.model.ExpandedPropertiesSample;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.PrimitiveTypesSample;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.complex.ODataDemoEntity;
import com.sdl.odata.test.model.complex.ODataDemoProperty;
import com.sdl.odata.test.model.complex.ODataVersion;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class with functionality to be shared by Xml and Json unmarshaller implementations.
 */
@SuppressWarnings("ALL")
public class UnmarshallerTest extends WriterUnmarshallerTest {
    /**
     * Service Root.
     */
    public static final String SERVICE_ROOT = "http://localhost:8080/odata.svc";
    protected final ODataRequest.Builder requestBuilder = new ODataRequest.Builder();
    protected ODataRequest request;
    protected ODataRequestContext context;
    protected Object singleCustomer = null;
    protected Object products = null;
    protected Object primitiveTypesSamples = null;
    protected Object collectionsTypesSamples = null;
    protected List<?> customersFeed = null;
    protected Object nestedComplexTypesSamples = null;
    protected ODataRequestContext errorContext;
    protected Object expandedPropertiesSamples = null;
    protected Object entityTypeSample = null;


    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        requestBuilder.setUri(odataUri.serviceRoot()).setMethod(ODataRequest.Method.GET);
        ODataRequest.Builder errorBuilder = new ODataRequest.Builder().setBodyText("test", "UTF-8")
                .setUri(odataUri.serviceRoot())
                .setMethod(ODataRequest.Method.GET);
        errorContext = new ODataRequestContext(errorBuilder.build(), odataUri, entityDataModel);
    }

    /**
     * Prepare 'OData Request Context' for a 'Write operation'
     * by specifying the source containing the unmarshalled content.
     *
     * @param source The source containing the unmarshalled content.
     * @throws IOException
     * @throws ODataUnmarshallingException
     */
    protected void preparePostRequestContext(String source) throws IOException, ODataUnmarshallingException {
        prepareRequestContext(source, POST);
    }

    /**
     * Prepare 'OData Request Context' for a 'Read operation' by specifying
     * the source containing the unmarshalled content.
     *
     * @param source The source containing the unmarshalled content.
     * @throws IOException
     * @throws ODataUnmarshallingException
     */
    protected void prepareGetRequestContext(String source) throws IOException, ODataUnmarshallingException {
        prepareRequestContext(source, GET);
    }

    private void prepareRequestContext(String source, ODataRequest.Method method)
            throws IOException, ODataUnmarshallingException {
        request = requestBuilder.setMethod(method)
                .setBodyText(readContent(source), UTF_8.name())
                .build();
        context = new ODataRequestContext(request, odataUri, entityDataModel);
    }

    public void assertCustomerSample() throws Exception {
        assertNotNull(singleCustomer);
        assertTrue(singleCustomer instanceof Customer);

        final List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(createAddress("Diagon Alley", "Behind Leaky Couldron", "10127", "London", "UK"));

        Customer expectedCustomer = createCustomer(10L, "Harry", new ArrayList<String>(), expectedAddresses,
                ZonedDateTime.parse("2014-05-02T12:00:00.000Z"), new ArrayList<Order>());

        assertCustomer((Customer) singleCustomer, expectedCustomer);
    }

    public void assertCustomersSample() throws Exception {
        assertNotNull(customersFeed);
        assertEquals(2, customersFeed.size());

        final List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(createAddress("Diagon Alley", "Behind Leaky Couldron", "10127", "London", "UK"));
        Customer expectedCustomer = createCustomer(10L, "Harry", new ArrayList<String>(), expectedAddresses,
                ZonedDateTime.parse("2014-05-02T12:00:00.000Z"), new ArrayList<Order>());
        assertCustomer((Customer) customersFeed.get(0), expectedCustomer);

        expectedAddresses.clear();
        expectedAddresses.add(createAddress("The Burrow", "102", "11001", "Ottery St. Catchpole", "UK"));
        expectedCustomer = createCustomer(20L, "Ron", new ArrayList<String>(), expectedAddresses,
                ZonedDateTime.parse("2014-05-02T12:00:00.000Z"), new ArrayList<Order>());
        assertCustomer((Customer) customersFeed.get(1), expectedCustomer);
    }

    public void assertCustomerWithLinksSample() throws Exception {
        assertNotNull(singleCustomer);
        assertTrue(singleCustomer instanceof Customer);

        final List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(createAddress("Diagon Alley", "Behind Leaky Couldron", "10127", "London", "UK"));
        final List<Order> orders = new ArrayList<>();
        orders.add(createOrder(1));
        orders.add(createOrder(2));

        Customer expectedCustomer = createCustomer(10L, "Harry", new ArrayList<String>(), expectedAddresses,
                ZonedDateTime.parse("2014-05-02T12:00:00.000Z"), orders);
        expectedCustomer.setBankAccount(new BankAccount().setIban("iban-111"));

        assertCustomer((Customer) singleCustomer, expectedCustomer);
    }

    public void assertProductSample() {
        assertNotNull(products);
        assertTrue(products instanceof Product);

        final Product product = (Product) products;
        assertEquals(15L, product.getId());
        assertEquals("Asus Nexus 7", product.getName());
        assertEquals(Category.ELECTRONICS, product.getCategory());
    }

    public void assertPrimitiveTypesSample() {
        assertNotNull(primitiveTypesSamples);
        assertTrue(primitiveTypesSamples instanceof PrimitiveTypesSample);

        final PrimitiveTypesSample primitiveTypesSample = (PrimitiveTypesSample) primitiveTypesSamples;
        assertEquals(20L, primitiveTypesSample.getId());
        assertEquals("John", primitiveTypesSample.getName());
        assertEquals(16.7, primitiveTypesSample.getDoubleProperty());
        // Note: Add all expectations over all primitives
    }

    public void assertCollectionsTypesSample() {
        assertNotNull(collectionsTypesSamples);
        assertTrue(collectionsTypesSamples instanceof CollectionsSample);

        final CollectionsSample collectionsSample = (CollectionsSample) collectionsTypesSamples;
        assertEquals(40L, collectionsSample.getId());
        assertEquals("Mixed collections", collectionsSample.getName());
    }

    public void assertNestedComplexTypesSamples() {
        assertNotNull(nestedComplexTypesSamples);
        assertTrue(nestedComplexTypesSamples instanceof ODataDemoEntity);

        final ODataDemoEntity oDataContextVocabulary = (ODataDemoEntity) nestedComplexTypesSamples;
        List<ODataDemoProperty> propertyDefinitions = oDataContextVocabulary.getProperties();
        assertEquals(33, propertyDefinitions.size());

        Set<String> setValues = propertyDefinitions.get(0).getDefaultValue().getSetValue();
        assertEquals(2, setValues.size());
        assertTrue(setValues.contains("PNG"));
        assertTrue(setValues.contains("JPEG"));

        assertEquals(800, propertyDefinitions.get(7).getDefaultValue().getIntegerValue());

        assertTrue(propertyDefinitions.get(9).getDefaultValue().getBooleanValue());

        final ODataVersion versionValue = propertyDefinitions.get(12).getDefaultValue().getVersionValue();
        assertEquals(1, versionValue.getMajorVersion());
        assertEquals(8, versionValue.getMinorVersion());
        assertEquals(5, versionValue.getIncrementalVersion());
        assertEquals("1.8.5", versionValue.getName());
        assertEquals(4, versionValue.getMajorVersionPart().getModifierPriority());
        assertEquals(1.0, propertyDefinitions.get(25).getDefaultValue().getFloatValue());
    }

    public void assertExtendedPropertiesSample() {
        assertNotNull(expandedPropertiesSamples);
        ExpandedPropertiesSample expandedPropertiesSample = (ExpandedPropertiesSample) expandedPropertiesSamples;
        assertEquals(5, expandedPropertiesSample.getId());
        assertEquals("Expanded Properties Sample", expandedPropertiesSample.getName());

        IdNamePairSample expandedEntry = expandedPropertiesSample.getExpandedEntry();
        assertNotNull(expandedEntry);
        assertEquals(10L, expandedEntry.getId());
        assertEquals("Expanded entry", expandedEntry.getName());

        List<IdNamePairSample> expandedFeed = expandedPropertiesSample.getExpandedFeed();
        assertNotNull(expandedFeed);
        assertEquals(2, expandedFeed.size());

        assertIdNamePairSample(expandedFeed.get(0), 10, "Expanded feed entry 1");
        assertIdNamePairSample(expandedFeed.get(1), 20, "Expanded feed entry 2");
    }

    private void assertIdNamePairSample(IdNamePairSample idNamePairSample, long id, String name) {
        assertNotNull(idNamePairSample);
        assertEquals(id, idNamePairSample.getId());
        assertEquals(name, idNamePairSample.getName());
    }

    public void assertAbstractEntityTypeSample() {
        assertNotNull(entityTypeSample);
        EntityTypeSample entityTypeSampleTyped = (EntityTypeSample) entityTypeSample;
        assertEquals("id.10", entityTypeSampleTyped.getId());
        assertEquals("Some inherited value", entityTypeSampleTyped.getInheritedProperty());

        assertNull(entityTypeSampleTyped.getInheritedId());
        assertEquals("Simple value", entityTypeSampleTyped.getComplexTypeProperty().getSimpleProperty());
        assertEquals("Inherited value", entityTypeSampleTyped.getComplexTypeProperty().getInheritedProperty());
    }
}
