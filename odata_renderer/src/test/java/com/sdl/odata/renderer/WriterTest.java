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
package com.sdl.odata.renderer;

import com.sdl.odata.WriterUnmarshallerTest;
import com.sdl.odata.test.model.BankAccount;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.CollectionsSample;
import com.sdl.odata.test.model.ComplexKeySample;
import com.sdl.odata.test.model.ComplexTypeSample;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.EntityTypeSample;
import com.sdl.odata.test.model.ExpandedPropertiesSample;
import com.sdl.odata.test.model.IdNamePairComplex;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.PrimitiveTypesSample;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.SingletonSample;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.sdl.odata.test.model.EnumSample.VALUE1;
import static com.sdl.odata.test.model.EnumSample.VALUE2;
import static java.util.Arrays.asList;


/**
 * Base test class with functionality to be shared by Xml and Json writer implementations.
 */
public abstract class WriterTest extends WriterUnmarshallerTest {

    private static final String METADATA_URL = "http://localhost:8080/odata.svc/$metadata";
    private static final UUID SINGLETON_SAMPLE_TEST_UUID = UUID.fromString("3dd4fa6e-2899-4429-b818-d34fe8df5dd0");
    protected static final String CUSTOMERS_URL = METADATA_URL + "#Customers";
    protected static final String CUSTOMER_URL = METADATA_URL + "#Customers/$entity";
    protected static final String PRODUCT_URL = METADATA_URL + "#Products/$entity";
    protected static final String PRIMITIVE_TYPES_SAMPLE_URL = METADATA_URL + "#PrimitiveTypesSamples/$entity";
    protected static final String COLLECTION_SAMPLE_URL = METADATA_URL + "#CollectionsSamples/$entity";
    protected static final String EXPANDED_PROPERTIES_SAMPLE_URL = METADATA_URL + "#ExpandedPropertiesSamples/$entity";
    protected static final String COMPLEX_KEY_SAMPLE_URL = METADATA_URL + "#ComplexKeySamples/$entity";
    protected static final String ABSTRACT_ENTITY_SAMPLE_URL = METADATA_URL + "#EntityTypeSamples/$entity";
    protected static final String SINGLETON_SAMPLE_CONTEXT_URL = METADATA_URL + "#SingletonSample/$entity";

    /**
     * Create a predefined {@link Customer} sample entity instance.
     *
     * @return The predefined {@link Customer} sample entity instance.
     * @throws Exception
     */
    protected Customer createCustomerSample() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2014, 5, 2, 12, 0, 0, 0, ZoneOffset.UTC);

        return createCustomer(10, "Harry", zonedDateTime,
                "Diagon Alley", "Behind Leaky Couldron", "10127", "London", "UK");
    }

    /**
     * Create a predefined {@link Customer} sample entity instance.
     *
     * @return The predefined {@link Customer} sample entity instance.
     * @throws Exception
     */
    protected Customer createCustomerWithLinkSample() throws Exception {

        final List<Order> orders = new ArrayList<>();
        orders.add(createOrder(1));
        orders.add(createOrder(2));
        return createCustomerSample().setBankAccount(new BankAccount().setIban("iban-111")).setOrders(orders);
    }

    /**
     * Create a predefined {@link Product} sample entity instance.
     *
     * @return The predefined {@link Product} sample entity instance.
     * @throws Exception
     */
    protected Product createProductSample() throws Exception {

        final Product product = new Product();

        product.setId(15);
        product.setName("Asus Nexus 7");
        product.setCategory(Category.ELECTRONICS);

        return product;
    }

    /**
     * Create a predefined {@link PrimitiveTypesSample} sample entity.
     *
     * @return The predefined {@link PrimitiveTypesSample} sample entity.
     * @throws Exception
     */
    protected PrimitiveTypesSample createPrimitiveTypesSample() throws Exception {

        final PrimitiveTypesSample primitiveTypes = new PrimitiveTypesSample();

        OffsetDateTime firstDateTime = OffsetDateTime.of(2014, 5, 7, 10, 0, 0, 0, ZoneOffset.UTC);

        OffsetDateTime secondDateTime = OffsetDateTime.of(2014, 5, 7, 11, 0, 0, 0, ZoneOffset.UTC);

        primitiveTypes.setId(20);
        primitiveTypes.setName("John");
        primitiveTypes.setBooleanProperty(true);
        primitiveTypes.setByteProperty((byte) 0x00);
        primitiveTypes.setDateProperty(LocalDate.of(2014, 5, 7));
        primitiveTypes.setDateTimeOffsetProperty(firstDateTime);
        primitiveTypes.setDurationProperty(Period.between(
                firstDateTime.toLocalDate(),
                secondDateTime.toLocalDate()));
        primitiveTypes.setTimeOfDayProperty(LocalTime.of(12, 0));
        primitiveTypes.setDecimalValueProperty(21);
        primitiveTypes.setDoubleProperty(16.7);
        primitiveTypes.setSingleProperty(12.3f);
        primitiveTypes.setGuidProperty(UUID.fromString("23492a5b-c4f1-4a50-b7a5-d8ebd6067902"));
        primitiveTypes.setInt16Property((short) 2);
        primitiveTypes.setInt32Property(5);
        primitiveTypes.setSbyteProperty((byte) 13);

        return primitiveTypes;
    }

    /**
     * Create a predefined {@link CollectionsSample} sample entity.
     *
     * @return The predefined {@link CollectionsSample} sample entity.
     * @throws Exception
     */
    protected CollectionsSample createCollectionsSample() throws Exception {

        final CollectionsSample collections = new CollectionsSample();
        final IdNamePairComplex idNamePair100 = new IdNamePairComplex();
        final IdNamePairComplex idNamePair120 = new IdNamePairComplex();
        idNamePair100.setId(100);
        idNamePair100.setName("Name 100");
        idNamePair120.setId(120);
        idNamePair120.setName("Name 120");
        collections.setId(40);
        collections.setName("Mixed collections");
        collections.setPrimitivesCollection(newArrayList("Text 1", "Text 2"));
        collections.setEnumCollection(newArrayList(VALUE1, VALUE1, VALUE2));
        collections.setIdNamePairCollection(newArrayList(idNamePair100, idNamePair120));

        return collections;
    }

    /**
     * Create a predefined list of {@link Customer} sample entities.
     *
     * @return The predefined list of {@link Customer} sample entities.
     * @throws Exception
     */
    protected List<Customer> createCustomersSample() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2014, 5, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);

        final Customer customer1 = createCustomer(10, "Harry",
                zonedDateTime, "Diagon Alley", "Behind Leaky Couldron",
                "10127", "London", "UK");
        final Customer customer2 = createCustomer(20, "Ron",
                zonedDateTime, "The Burrow", "102",
                "11001", "Ottery St. Catchpole", "UK");

        return newArrayList(customer1, customer2);
    }

    /**
     * Create a predefined {@link ExpandedPropertiesSample} sample entity.
     *
     * @return The predefined {@link ExpandedPropertiesSample} sample entity.
     */
    protected ExpandedPropertiesSample createExpandedPropertiesSample() {

        final ExpandedPropertiesSample expandedPropertiesEntity = createExpandedPropertiesNoLinksSample();
        expandedPropertiesEntity.setExpandedEntry(createIdNamePair(10L, "Expanded entry"));
        expandedPropertiesEntity.setExpandedFeed(newArrayList(
                createIdNamePair(10L, "Expanded feed entry 1"),
                createIdNamePair(20L, "Expanded feed entry 2")));

        return expandedPropertiesEntity;
    }

    /**
     * Create a predefined {@link ExpandedPropertiesSample} sample entity.
     *
     * @return The predefined {@link ExpandedPropertiesSample} sample entity.
     */
    protected ExpandedPropertiesSample createExpandedPropertiesNoLinksSample() {

        final ExpandedPropertiesSample expandedPropertiesEntity = new ExpandedPropertiesSample();
        expandedPropertiesEntity.setId(5L);
        expandedPropertiesEntity.setName("Expanded Properties Sample");

        return expandedPropertiesEntity;
    }

    /**
     * Create a predefined {@link ExpandedPropertiesSample} sample entity.
     *
     * @return The predefined {@link ExpandedPropertiesSample} sample entity.
     */
    protected ComplexKeySample createComplexKeySample() {

        final ComplexKeySample complexKeySample = new ComplexKeySample();
        complexKeySample.setId(15L);
        complexKeySample.setName("ComplexKey");
        complexKeySample.setPeriod(Period.of(3, 30, 30));

        return complexKeySample;
    }

    /**
     * Create a predefined {@link EntityTypeSample} sample entity.
     *
     * @return The predefined {@link EntityTypeSample} sample entity.
     */
    protected EntityTypeSample createEntityTypeSample() {
        return createEntityType("id.10", "Some inherited value", "Simple value", "Inherited value",
                Arrays.asList(new String[] {"Value 1", "Value 2"}));
    }

    /**
     * Create a predefined list of {@link ComplexTypeSample} types.
     *
     * @return The predefined list of {@link ComplexTypeSample} types.
     */
    protected List<ComplexTypeSample> createComplexTypeListSample() {
        return asList(createComplexType("Simple 1", "Inherited 1"), createComplexType("Simple 2", "Inherited 2"));
    }

    /**
     * Create an instance of {@link ComplexTypeSample} with the given details.
     *
     * @param simpleProperty    The simple property.
     * @param inheritedProperty The inherited property.
     * @return The created instance.
     */
    protected ComplexTypeSample createComplexType(String simpleProperty, String inheritedProperty) {
        ComplexTypeSample complexTypeSample = new ComplexTypeSample();
        complexTypeSample.setSimpleProperty(simpleProperty).setInheritedProperty(inheritedProperty);
        return complexTypeSample;
    }

    /**
     * Create an instance of {@link SingletonSample} with predefined test UUID.
     *
     * @return The created instance
     */
    protected SingletonSample createSingletonSample() {
        SingletonSample singletonSample = new SingletonSample();
        singletonSample.setId(SINGLETON_SAMPLE_TEST_UUID);
        return singletonSample;
    }

    private IdNamePairSample createIdNamePair(long id, String name) {

        IdNamePairSample idNamePair = new IdNamePairSample();
        idNamePair.setId(id);
        idNamePair.setName(name);

        return idNamePair;
    }
}
