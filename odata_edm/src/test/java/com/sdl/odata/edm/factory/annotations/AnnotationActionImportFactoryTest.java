/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.annotations.EdmActionImport;
import com.sdl.odata.test.model.ActionSample;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.ExampleFlags;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.OrderLine;
import com.sdl.odata.test.model.Product;
import org.junit.Test;

/**
 * Annotation Action Import Factory test.
 */
public class AnnotationActionImportFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testLookupGetFunctionFail() throws ODataEdmException {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();

        factory.addClass(Address.class);
        factory.addClass(Category.class);
        factory.addClass(Customer.class);
        factory.addClass(Order.class);
        factory.addClass(OrderLine.class);
        factory.addClass(Product.class);
        factory.addClass(ExampleFlags.class);
        factory.addClass(ActionSample.class);
        factory.addClass(WrongActionImportSample.class);
        factory.setSchemaAlias("ODataDemo", "TestAlias");

        factory.buildEntityDataModel();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLookupGetFunctionNoEntitySetFail() throws ODataEdmException {
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();

        factory.addClass(Address.class);
        factory.addClass(Category.class);
        factory.addClass(Customer.class);
        factory.addClass(Order.class);
        factory.addClass(OrderLine.class);
        factory.addClass(Product.class);
        factory.addClass(ExampleFlags.class);
        factory.addClass(ActionSample.class);
        factory.addClass(ActionImportWithoutEntitySetDefinedSample.class);
        factory.setSchemaAlias("ODataDemo", "TestAlias");

        factory.buildEntityDataModel();
    }

    /**
     * Action import sample without defined name and other annotation fields.
     */
    @EdmActionImport(entitySet = "Customers")
    public static class WrongActionImportSample {
    }

    /**
     * Action import sample without defined entity set.
     */
    @EdmActionImport
    public static class ActionImportWithoutEntitySetDefinedSample {
    }
}
