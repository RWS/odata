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
package com.sdl.odata.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.ODataUriParseException;
import com.sdl.odata.api.parser.RelativeUri;
import com.sdl.odata.api.parser.ServiceRootUri;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.CollectionsSample;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.test.model.OrderLine;
import com.sdl.odata.test.model.PrimitiveTypesSample;
import com.sdl.odata.test.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Parser Operation Test.
 *
 */
public class ParserOperationTest extends ParserTestSuite {

    @Test
    public void testServiceRoot() throws ODataException {
        uri = parser.parseUri(SERVICE_ROOT, model);
        assertNotNull(uri);
        // Service root should be full qualified name of localhost + odata.svc preffix
        assertEquals(SERVICE_ROOT.substring(0, SERVICE_ROOT.length() - 1), uri.serviceRoot());
        assertNotNull(uri.relativeUri());

        RelativeUri relative = uri.relativeUri();
        assertTrue(relative instanceof ServiceRootUri);
    }

    @Test
    public void testIncorrectModel() {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();

        assertThrows(IllegalArgumentException.class, () -> {
                    factory.addClass(EmptyDummy.class);
                    parser.parseUri(SERVICE_ROOT, factory.buildEntityDataModel());
                }
        );
    }

    @Test
    public void testIfExistingModelIsAbsent() {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        // Order.class is not added
        factory.addClass(Address.class);
        factory.addClass(CollectionsSample.class);
        factory.addClass(Customer.class);
        factory.addClass(IdNamePairSample.class);

        factory.addClass(OrderLine.class);
        factory.addClass(PrimitiveTypesSample.class);
        factory.addClass(Product.class);

        assertThrows(ODataUriParseException.class, () ->
                parser.parseUri(SERVICE_ROOT + "Orders(1)", factory.buildEntityDataModel())
        );
    }

    /**
     * Empty non-model for AnnotationEntityDataModelFactory
     * Using this empty class should fail the parser.
     */
    public class EmptyDummy {
    }

}
