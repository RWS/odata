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
package com.sdl.odata.client.marshall;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.client.BasicODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.marshall.ODataEntityUnmarshaller;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.SingletonSample;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sdl.odata.client.util.MarshallingTestUtilities.atomMarshall;
import static com.sdl.odata.client.util.MarshallingTestUtilities.createODataUri;
import static com.sdl.odata.client.util.MarshallingTestUtilities.marshalPrimitives;
import static com.sdl.odata.test.model.Category.BOOKS;
import static com.sdl.odata.test.model.Category.ELECTRONICS;
import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * The Atom Response Unmarshaller Test.
 */
public class AtomResponseUnmarshallerTest {

    private static final String SERVICE_URL = "http://mock/odata.svc";

    private ODataEntityUnmarshaller unmarshaller;

    @Before
    public void setup() {
        unmarshaller = new AtomEntityUnmarshaller(getEdmEntityClasses(), SERVICE_URL);
    }

    @Test
    public void testUnmarshallEntities() throws ODataClientException, ODataException, UnsupportedEncodingException {
        List<Product> products = Stream.of(
                createProduct(11, "Book 11", BOOKS),
                createProduct(12, "Electronics 12", ELECTRONICS))
                .collect(Collectors.toList());
        String marshalledProducts = atomMarshall(
                products, createODataUri("http://mock/odata.svc/Products"));
        List<?> unmarshalledProducts = unmarshaller.unmarshall(marshalledProducts,
                new BasicODataClientQuery.Builder()
                        .withEntityType(Product.class)
                        .build());

        assertNotNull(unmarshalledProducts);
        assertEquals(unmarshalledProducts.size(), 2);
        assertEquals(products.get(0), unmarshalledProducts.get(0));
        assertEquals(products.get(1), unmarshalledProducts.get(1));
    }

    @Test
    public void testUnmarshallEntity() throws ODataException, ODataClientException, UnsupportedEncodingException {
        Product product = createProduct(11, "Book 11", BOOKS);
        String marshalledProduct = atomMarshall(product,
                createODataUri("http://mock/odata.svc/Products(11)"));
        Object unmarshalledProduct = unmarshaller.unmarshallEntity(marshalledProduct,
                new BasicODataClientQuery.Builder()
                        .withEntityType(Product.class)
                        .build());

        assertNotNull(unmarshalledProduct);
        assertEquals(product, unmarshalledProduct);
    }

    @Test
    public void testUnmarshallSingletonEntity()
            throws ODataException, ODataClientException, UnsupportedEncodingException {
        SingletonSample singletonSample = new SingletonSample();
        singletonSample.setId(UUID.randomUUID());

        String marshalledSingleton = atomMarshall(
                singletonSample, createODataUri("http://mock/odata.svc/SingletonSample"));
        Object unmarshalledSingleton = unmarshaller.unmarshallEntity(marshalledSingleton,
                new BasicODataClientQuery.Builder()
                        .withEntityType(Product.class)
                        .build());

        assertNotNull(unmarshalledSingleton);
        assertEquals(singletonSample, unmarshalledSingleton);
    }

    @Test
    public void testCollectionPrimitives() throws ODataException, UnsupportedEncodingException, ODataClientException {
        List<String> strings = asList("test1", "test2", "test3");
        String marshalledStringList = marshalPrimitives(strings,
                createODataUri("http://localhost:8080/odata.svc/Customers(1)/Phone"));
        List<?> resultStringList = (List<?>) unmarshaller.unmarshallEntity(marshalledStringList,
                new BasicODataClientQuery.Builder().withEntityType(List.class).build());

        assertNotNull(resultStringList);
        assertFalse(resultStringList.isEmpty());
        assertEquals(3, resultStringList.size());
    }

    private Product createProduct(int id, String name, Category category) {
        return new Product()
                .setId(id)
                .setName(name)
                .setCategory(category);
    }
}
