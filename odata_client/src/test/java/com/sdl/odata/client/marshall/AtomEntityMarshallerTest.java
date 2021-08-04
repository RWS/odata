/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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

import com.sdl.odata.client.BasicODataClientQuery;
import com.sdl.odata.client.FunctionImportClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.marshall.ODataEntityMarshaller;
import com.sdl.odata.client.api.marshall.ODataEntityUnmarshaller;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.SingletonSample;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sdl.odata.test.model.Category.BOOKS;
import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link AtomEntityMarshaller}.
 */
public class AtomEntityMarshallerTest {

    private static final String ODATA_SERVICE_MOCK_URL = "http://mock.com/odata.svc";

    private ODataEntityMarshaller oDataEntityMarshaller;
    private ODataEntityUnmarshaller oDataEntityUnmarshaller;

    @Before
    public void setup() {
        oDataEntityMarshaller = new AtomEntityMarshaller(getEdmEntityClasses(), ODATA_SERVICE_MOCK_URL);
        oDataEntityUnmarshaller = new AtomEntityUnmarshaller(getEdmEntityClasses(), ODATA_SERVICE_MOCK_URL);
    }

    @Test
    public void testMarshallEntity() throws ODataClientException {
        Product lordOfTheRingsBook = new Product()
                .setId(11)
                .setName("The Lord of the Rings")
                .setCategory(BOOKS);
        BasicODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(Product.class)
                .build();
        String marshalledEntity = oDataEntityMarshaller.marshallEntity(lordOfTheRingsBook, query);
        Object unmarshalledEntity = oDataEntityUnmarshaller.unmarshallEntity(marshalledEntity, query);

        assertNotNull(unmarshalledEntity);
        assertEquals(lordOfTheRingsBook, unmarshalledEntity);
    }

    @Test
    public void testUnmarshallingPrimitiveType() throws ODataClientException {
        String primitiveODataResponse = "<metadata:value xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" " +
                "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" " +
                "metadata:context=\"http://localhost:8086/client/v4/content.svc/" +
                "$metadata#GetComponentPresentationContentFunctionImport\">This is ComponentPresentation content." +
                "</metadata:value>";
        Object result = oDataEntityUnmarshaller.unmarshallEntity(primitiveODataResponse,
                new FunctionImportClientQuery.Builder()
                        .withEntityType(String.class)
                        .withFunctionName("SomeFakeFunction")
                        .build());

        assertEquals(result, "This is ComponentPresentation content.");
    }

    @Test
    public void testUnmarshallingPrimitiveTypeWithWrongSource() throws ODataClientException {
        String primitiveODataResponse = "unmarshalling response";
        Object result = oDataEntityUnmarshaller.unmarshallEntity(primitiveODataResponse,
                new FunctionImportClientQuery.Builder()
                        .withEntityType(String.class)
                        .withFunctionName("SomeFakeFunction")
                        .build());

        assertNull(result);
    }

    @Test
    public void testMarshallEntities() throws ODataClientException {
        Product lordOfTheRingsBook = new Product()
                .setId(11)
                .setName("The Lord of the Rings")
                .setCategory(BOOKS);
        Product gameOfThronesBook = new Product()
                .setId(11)
                .setName("Game of Thrones")
                .setCategory(BOOKS);


        List<Product> products = Stream.of(lordOfTheRingsBook, gameOfThronesBook).collect(Collectors.toList());
        BasicODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(Product.class)
                .build();
        String marshalledEntities = oDataEntityMarshaller.marshallEntity(products, query);
        List<?> unmarshallEntities = oDataEntityUnmarshaller.unmarshall(marshalledEntities, query);

        assertNotNull(unmarshallEntities);
        assertFalse(unmarshallEntities.isEmpty());
        for (Object unmarshallEntity : unmarshallEntities) {
            assertTrue(products.contains(unmarshallEntity));
        }
    }

    @Test
    public void testSingletonSampleEntity() throws ODataClientException {
        SingletonSample singletonSample = new SingletonSample();

        singletonSample.setId(UUID.randomUUID());

        BasicODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(SingletonSample.class)
                .build();

        String marshalledEntity = oDataEntityMarshaller.marshallEntity(singletonSample, query);
        Object unmarshalledEntity = oDataEntityUnmarshaller.unmarshallEntity(marshalledEntity, query);

        assertNotNull(unmarshalledEntity);
        assertEquals(singletonSample, unmarshalledEntity);
    }
}
