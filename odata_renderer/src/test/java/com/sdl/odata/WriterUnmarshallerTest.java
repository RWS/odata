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
package com.sdl.odata;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.BankAccount;
import com.sdl.odata.test.model.ComplexTypeSample;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.EntityTypeSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.ComplexTypeSampleList;
import com.sdl.odata.test.util.TestUtils;
import org.junit.Before;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Base test class with functionality to be shared by writer and unmarshaller (Xml and Json) implementations.
 */
public abstract class WriterUnmarshallerTest {

    protected EntityDataModel entityDataModel;
    protected ODataUri odataUri;

    @Before
    public void setUp() throws Exception {

        this.entityDataModel = buildEntityDataModel();

        // Note: By the default create a simple OData URI about the Customers entity set, please note that this method
        // can be called from extensions of this base test class to change the OData URI to use.
        createODataUri("http://localhost:8080/odata.svc", "Customers");
    }

    public EntityDataModel buildEntityDataModel() throws ODataEdmException {
        final AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        return factory.addClasses(getEdmEntityClasses()).buildEntityDataModel();
    }

    /**
     * Create a test OData URI specifying only the service root.
     *
     * @param serviceRoot The service root.
     */
    protected void createODataUri(String serviceRoot) {
        odataUri = TestUtils.createODataUri(serviceRoot);
    }

    public void createODataUri(String serviceRoot, String entitySetName) {
        odataUri = TestUtils.createODataUri(serviceRoot, entitySetName);
    }

    /**
     * Create a customer with the given details.
     *
     * @param id           The customer id.
     * @param name         The customer name.
     * @param phoneNumbers The list of phone numbers.
     * @param addresses    The list of addresses.
     * @param dateTime     The date time.
     * @param orders       @param orders The list of orders.
     * @return The created customer.
     */
    protected Customer createCustomer(long id, String name, List<String> phoneNumbers, List<Address> addresses,
                                      ZonedDateTime dateTime, List<Order> orders) {

        final Customer customer = new Customer();

        customer.setId(id);
        customer.setName(name);
        customer.setPhoneNumbers(phoneNumbers);
        customer.setAddress(addresses);
        customer.setDateTime(dateTime);
        customer.setOrders(orders);

        return customer;
    }

    /**
     * Create a customer with the given details by specifying only a single address.
     *
     * @param id          The customer id.
     * @param name        The customer name.
     * @param dateTime    The customer date and time.
     * @param street      The street of the customer single address.
     * @param houseNumber The house number of the customer single address.
     * @param postalCode  The postal code of the customer single address.
     * @param city        The city of the customer single address.
     * @param country     The country of the customer single address.
     * @return The created customer.
     */
    protected Customer createCustomer(int id, String name, ZonedDateTime dateTime, String street, String houseNumber,
                                      String postalCode, String city, String country) {

        final List<Address> addressList = new ArrayList<>();
        final Customer customer = new Customer();
        final Address address = new Address();
        addressList.add(address);

        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setPostalCode(postalCode);
        address.setCity(city);
        address.setCountry(country);
        customer.setId(id);
        customer.setName(name);
        customer.setAddress(addressList);
        customer.setDateTime(dateTime);

        return customer;
    }

    /**
     * Create an address with the given details.
     *
     * @param street      The street name.
     * @param houseNumber The house number.
     * @param postalCode  The postal code.
     * @param city        The city.
     * @param country     The country.
     * @return The created address.
     */
    protected Address createAddress(String street, String houseNumber, String postalCode, String city, String country) {

        final Address address = new Address();
        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setPostalCode(postalCode);
        address.setCity(city);
        address.setCountry(country);

        return address;
    }

    /**
     * Create an order with the given details.
     *
     * @param id The id.
     * @return The created order.
     */
    protected Order createOrder(long id) {

        return new Order().setId(id);
    }

    /**
     * Create an instance of {@link EntityTypeSample} with the given details.
     *
     * @param id                       The given 'Id'.
     * @param inheritedProperty        The given 'InheritedProperty'.
     * @param simpleProperty           The given 'SimpleProperty'.
     * @param complexInheritedProperty The given 'InheritedProperty' for the embedded instance of
     *                                 {@link ComplexTypeSample}.
     * @return The created instance.
     */
    protected EntityTypeSample createEntityType(String id, String inheritedProperty, String simpleProperty,
                                                String complexInheritedProperty, List<String> listProperty) {

        ComplexTypeSample complexTypeSample = new ComplexTypeSample();
        complexTypeSample.setSimpleProperty(simpleProperty).setInheritedProperty(complexInheritedProperty);

        EntityTypeSample entityTypeSample = new EntityTypeSample();
        entityTypeSample.setId(id).setInheritedProperty(inheritedProperty);
        entityTypeSample.setComplexTypeProperty(complexTypeSample);

        ComplexTypeSampleList complexTypeSampleList = new ComplexTypeSampleList();
        complexTypeSampleList.setListProperty(listProperty);
        entityTypeSample.setComplexTypeListProperty(complexTypeSampleList);

        return entityTypeSample;
    }

    /**
     * Assert a customer.
     *
     * @param actual   The actual customer.
     * @param expected The expected customer.
     */
    protected void assertCustomer(Customer actual, Customer expected) {

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getName(), is(expected.getName()));

        List<String> actualPhoneNumbers = actual.getPhoneNumbers();
        List<String> expectedPhoneNumbers = expected.getPhoneNumbers();
        assertThat(actualPhoneNumbers.size(), is(expectedPhoneNumbers.size()));
        int phoneNumberIndex = 0;
        for (String phoneNumber : actualPhoneNumbers) {
            assertThat(phoneNumber, is(expectedPhoneNumbers.get(phoneNumberIndex++)));
        }

        List<Address> actualAddresses = actual.getAddress();
        List<Address> expectedAddresses = expected.getAddress();
        assertThat(actualAddresses.size(), is(expectedAddresses.size()));

        int addressIndex = 0;
        for (Address address : actualAddresses) {
            assertAddress(address, expectedAddresses.get(addressIndex++));
        }

        assertThat(actual.getDateTime(), is(expected.getDateTime()));

        List<Order> actualOrders = actual.getOrders();
        List<Order> expectedOrders = expected.getOrders();
        assertThat(actualOrders.size(), is(expectedOrders.size()));

        int orderIndex = 0;
        for (Order order : actualOrders) {
            assertOrder(order, expectedOrders.get(orderIndex++));
        }

        assertBankAccount(actual.getBankAccount(), expected.getBankAccount());
    }

    /**
     * Assert an address.
     *
     * @param actual   The actual address.
     * @param expected The expected address.
     */
    protected void assertAddress(Address actual, Address expected) {

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.getStreet(), is(expected.getStreet()));
        assertThat(actual.getHouseNumber(), is(expected.getHouseNumber()));
        assertThat(actual.getPostalCode(), is(expected.getPostalCode()));
        assertThat(actual.getCity(), is(expected.getCity()));
        assertThat(actual.getCountry(), is(expected.getCountry()));
    }

    /**
     * Assert an order.
     *
     * @param actual   The actual order.
     * @param expected The expected order.
     */
    protected void assertOrder(Order actual, Order expected) {

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.getId(), is(expected.getId()));
    }

    /**
     * Assert a bank account.
     *
     * @param actual   The actual bank account.
     * @param expected The expected bank account.
     */
    protected void assertBankAccount(BankAccount actual, BankAccount expected) {

        if (expected == null) {
            assertThat(actual, is(nullValue()));
        } else {
            assertThat(actual, is(not(nullValue())));
            assertThat(actual.getIban(), is(expected.getIban()));
        }
    }

}
