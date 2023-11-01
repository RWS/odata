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
package com.sdl.odata.client;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.api.edm.annotations.EdmPropertyRef;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.client.api.ODataClientComponentsProvider;
import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.caller.EndpointCaller;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataNotImplementedException;
import com.sdl.odata.client.api.marshall.ODataEntityMarshaller;
import com.sdl.odata.client.api.marshall.ODataEntityUnmarshaller;
import com.sdl.odata.client.api.model.ODataIdAwareEntity;
import com.sdl.odata.client.marshall.AtomEntityUnmarshaller;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sdl.odata.client.util.MarshallingTestUtilities.atomMarshall;
import static com.sdl.odata.client.util.MarshallingTestUtilities.createODataUri;
import static com.sdl.odata.test.model.Category.BOOKS;
import static com.sdl.odata.test.model.Category.ELECTRONICS;
import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * The Default OData Client Test.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DefaultODataClientTest {

    private static final String SERVICE_URL = "http://mock/odata.svc";
    private static final String MARSHALLED_MOCKED_ENTITY_CONTENT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry>XML_CONTENT_TO_POST</entry>";
    private static final String MARSHALLED_MOCKED_ENTITY_RETURNED_CONTENT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry>XML_CONTENT_RETURNED</entry>";

    @Mock
    private ODataClientComponentsProvider componentsProvider;
    @Mock
    private EndpointCaller endpointCaller;
    @Mock
    private ODataEntityMarshaller marshaller;
    @Mock
    private ODataEntityUnmarshaller unmarshaller;

    private DefaultODataClient client;

    @BeforeEach
    public void setup() throws ODataClientException, MalformedURLException {
        client = new DefaultODataClient();
        client.configure(componentsProvider);
        when(componentsProvider.getEndpointCaller()).thenReturn(endpointCaller);
        when(componentsProvider.getUnmarshaller()).thenReturn(
                new AtomEntityUnmarshaller(getEdmEntityClasses(), SERVICE_URL));
        when(componentsProvider.getWebServiceUrl()).thenReturn(new URL(SERVICE_URL));
        when(componentsProvider.getMarshaller()).thenReturn(marshaller);
        when(marshaller.marshallEntity(any(), any(ODataClientQuery.class)))
                .thenReturn(MARSHALLED_MOCKED_ENTITY_CONTENT);
    }

    @Test
    public void testGetEntities() throws MalformedURLException, ODataClientException, ODataException,
            UnsupportedEncodingException {
        List<Product> products = Stream.of(
                        createProduct(11, "Book 11", BOOKS),
                        createProduct(12, "Electronics 12", ELECTRONICS))
                .collect(Collectors.toList());
        String marshalledProduct = atomMarshall(
                products, createODataUri("http://mock/odata.svc/Products"));
        ODataClientQuery query = new BasicODataClientQuery.Builder().withEntityType(Product.class).build();
        when(endpointCaller.callEndpoint(emptyMap(), new URL(SERVICE_URL + "/" + query.getQuery())))
                .thenReturn(marshalledProduct);
        List<Object> entities = (List<Object>) client.getEntities(emptyMap(), query);

        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals(products.get(0), entities.get(0));
        assertEquals(products.get(1), entities.get(1));
    }

    @Test
    public void testGetEntity()
            throws ODataException, MalformedURLException, UnsupportedEncodingException, ODataClientException {
        Product product = new Product()
                .setId(11)
                .setName("book name")
                .setCategory(Category.BOOKS);

        String marshalledProduct = atomMarshall(
                product, createODataUri("http://mock/odata.svc/Products(11)"));
        ODataClientQuery query = new BasicODataClientQuery.Builder().withEntityType(Product.class).build();
        when(endpointCaller.callEndpoint(emptyMap(),
                new URL(SERVICE_URL + "/" + query.getQuery()))).thenReturn(marshalledProduct);
        Object entity = client.getEntity(emptyMap(), query);

        assertNotNull(entity);
        assertEquals(Product.class, entity.getClass());
        assertEquals(product, entity);
    }

    @Test
    public void testCreateEntity() throws ODataClientException {
        Product product = createProduct(31, "Best book ever", BOOKS);
        when(endpointCaller.doPostEntity(anyMap(),
                any(URL.class), eq(MARSHALLED_MOCKED_ENTITY_CONTENT), any(MediaType.class),
                any(MediaType.class))).thenReturn(MARSHALLED_MOCKED_ENTITY_RETURNED_CONTENT);
        when(componentsProvider.getUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshallEntity(eq(MARSHALLED_MOCKED_ENTITY_RETURNED_CONTENT),
                any(ODataClientQuery.class)))
                .thenReturn(product);
        Object savedProduct = client.createEntity(emptyMap(), product);

        assertNotNull(savedProduct);
        assertEquals(product, savedProduct);
    }

    @Test
    public void testUpdateEntity() throws ODataClientException {
        Book existingBook = new Book("35", "Harry Potter", "J. K. Rowling");
        when(endpointCaller.doPutEntity(anyMap(),
                any(URL.class), eq(MARSHALLED_MOCKED_ENTITY_CONTENT), any(MediaType.class)))
                .thenReturn(MARSHALLED_MOCKED_ENTITY_RETURNED_CONTENT);
        when(componentsProvider.getUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshallEntity(eq(MARSHALLED_MOCKED_ENTITY_RETURNED_CONTENT), any(ODataClientQuery.class)))
                .thenReturn(existingBook);
        Object updatedProduct = client.updateEntity(emptyMap(), existingBook);

        assertNotNull(updatedProduct);
        assertEquals(existingBook, updatedProduct);
    }

    @Test
    public void testGetMetaData() {
        assertThrows(ODataNotImplementedException.class, () ->
                client.getMetaData(emptyMap(),
                        new BasicODataClientQuery.Builder().withEntityType(Product.class).build())
        );
    }

    @Test
    public void testGetLinks() {
        assertThrows(ODataNotImplementedException.class, () ->
                client.getLinks(new BasicODataClientQuery.Builder().withEntityType(Product.class).build())
        );
    }

    @Test
    public void testGetCollections() {
        assertThrows(ODataNotImplementedException.class, () ->
                client.getCollections(new BasicODataClientQuery.Builder().withEntityType(Product.class).build())
        );
    }

    private Product createProduct(int id, String name, Category category) {
        return new Product()
                .setId(id)
                .setName(name)
                .setCategory(category);
    }

    /**
     * Book Aware Entity.
     */
    @EdmEntity(namespace = "ODataDemo", keyRef = {@EdmPropertyRef(path = "id")})
    @EdmEntitySet
    private static class Book implements ODataIdAwareEntity {

        @EdmProperty
        private String id;
        @EdmProperty
        private String name;
        @EdmProperty
        private String author;

        Book(String id, String name, String author) {
            this.id = id;
            this.name = name;
            this.author = author;
        }

        @Override
        public String getId() {
            return null;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

}
