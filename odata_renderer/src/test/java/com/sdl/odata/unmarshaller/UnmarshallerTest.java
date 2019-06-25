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
import org.junit.Before;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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


    @Before
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

        assertThat(singleCustomer, is(not(nullValue())));
        assertThat(singleCustomer, instanceOf(Customer.class));

        final List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(createAddress("Diagon Alley", "Behind Leaky Couldron", "10127", "London", "UK"));

        Customer expectedCustomer = createCustomer(10L, "Harry", new ArrayList<String>(), expectedAddresses,
           ZonedDateTime.parse("2014-05-02T12:00:00.000Z"), new ArrayList<Order>());

        assertCustomer((Customer) singleCustomer, expectedCustomer);
    }

    public void assertCustomersSample() throws Exception {

        assertThat(customersFeed, is(notNullValue()));
        assertThat(customersFeed.size(), is(2));

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

        assertThat(singleCustomer, is(not(nullValue())));
        assertThat(singleCustomer, instanceOf(Customer.class));

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

        assertThat(products, is(not(nullValue())));
        assertThat(products, instanceOf((Class) Product.class));

        final Product product = (Product) products;
        assertThat(product.getId(), is(15L));
        assertThat(product.getName(), is("Asus Nexus 7"));
        assertThat(product.getCategory(), is(Category.ELECTRONICS));
    }

    public void assertPrimitiveTypesSample() {

        assertThat(primitiveTypesSamples, is(not(nullValue())));
        assertThat(primitiveTypesSamples, instanceOf((Class) PrimitiveTypesSample.class));

        final PrimitiveTypesSample primitiveTypesSample = (PrimitiveTypesSample) primitiveTypesSamples;
        assertThat(primitiveTypesSample.getId(), is(20L));
        assertThat(primitiveTypesSample.getName(), is("John"));
        assertThat(primitiveTypesSample.getDoubleProperty(), is(16.7));
        // Note: Add all expectations over all primitives
    }

    public void assertCollectionsTypesSample() {

        assertThat(collectionsTypesSamples, is(not(nullValue())));
        assertThat(collectionsTypesSamples, instanceOf((Class) CollectionsSample.class));

        final CollectionsSample collectionsSample = (CollectionsSample) collectionsTypesSamples;
        assertThat(collectionsSample.getId(), is(40L));
        assertThat(collectionsSample.getName(), is("Mixed collections"));
    }

    public void assertNestedComplexTypesSamples() {
        assertThat(nestedComplexTypesSamples, is(not(nullValue())));
        assertThat(nestedComplexTypesSamples, instanceOf((Class) ODataDemoEntity.class));

        final ODataDemoEntity oDataContextVocabulary = (ODataDemoEntity) nestedComplexTypesSamples;
        List<ODataDemoProperty> propertyDefinitions = oDataContextVocabulary.getProperties();
        assertThat(propertyDefinitions.size(), is(33));

        Set<String> setValues = propertyDefinitions.get(0).getDefaultValue().getSetValue();
        assertThat(setValues.size(), is(2));
        assertTrue(setValues.contains("PNG"));
        assertTrue(setValues.contains("JPEG"));

        assertThat(propertyDefinitions.get(7).getDefaultValue().getIntegerValue(), is(800));

        assertTrue(propertyDefinitions.get(9).getDefaultValue().getBooleanValue());

        final ODataVersion versionValue = propertyDefinitions.get(12).getDefaultValue().getVersionValue();
        assertThat(versionValue.getMajorVersion(), is(1));
        assertThat(versionValue.getMinorVersion(), is(8));
        assertThat(versionValue.getIncrementalVersion(), is(5));
        assertThat(versionValue.getName(), is("1.8.5"));
        assertThat(versionValue.getMajorVersionPart().getModifierPriority(), is(4));

        assertThat(propertyDefinitions.get(25).getDefaultValue().getFloatValue(), is(1.0));
    }

    public void assertExtendedPropertiesSample() {
        assertThat(expandedPropertiesSamples, is(not(nullValue())));
        ExpandedPropertiesSample expandedPropertiesSample = (ExpandedPropertiesSample) expandedPropertiesSamples;
        assertThat(expandedPropertiesSample.getId(), is(5L));
        assertThat(expandedPropertiesSample.getName(), is("Expanded Properties Sample"));

        IdNamePairSample expandedEntry = expandedPropertiesSample.getExpandedEntry();
        assertThat(expandedEntry, is(notNullValue()));
        assertThat(expandedEntry.getId(), is(10L));
        assertThat(expandedEntry.getName(), is("Expanded entry"));

        List<IdNamePairSample> expandedFeed = expandedPropertiesSample.getExpandedFeed();
        assertThat(expandedFeed, is(notNullValue()));
        assertThat(expandedFeed.size(), is(2));
        assertIdNamePairSample(expandedFeed.get(0), 10, "Expanded feed entry 1");
        assertIdNamePairSample(expandedFeed.get(1), 20, "Expanded feed entry 2");
    }

    private void assertIdNamePairSample(IdNamePairSample idNamePairSample, long id, String name) {
        assertThat(idNamePairSample, is(notNullValue()));
        assertThat(idNamePairSample.getId(), is(id));
        assertThat(idNamePairSample.getName(), is(name));
    }

    public void assertAbstractEntityTypeSample() {
        assertThat(entityTypeSample, is(notNullValue()));
        EntityTypeSample entityTypeSampleTyped = (EntityTypeSample) entityTypeSample;
        assertThat(entityTypeSampleTyped.getId(), is("id.10"));
        assertThat(entityTypeSampleTyped.getInheritedProperty(), is("Some inherited value"));
        assertThat(entityTypeSampleTyped.getInheritedId(), is(nullValue()));
        assertThat(entityTypeSampleTyped.getComplexTypeProperty().getSimpleProperty(), is("Simple value"));
        assertThat(entityTypeSampleTyped.getComplexTypeProperty().getInheritedProperty(), is("Inherited value"));
    }
}
