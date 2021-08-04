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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.annotations.EdmFunctionImport;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.ExampleFlags;
import com.sdl.odata.test.model.FunctionImportSample;
import com.sdl.odata.test.model.FunctionSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.OrderLine;
import com.sdl.odata.test.model.Product;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link AnnotationFunctionImportFactory}.
 */
public class AnnotationFunctionImportFactoryTest {

    @Test
    public void testFullyQualifiedFunctionImportName() {
        String fullyQualifiedFunctionImportName = AnnotationFunctionImportFactory.getFullyQualifiedFunctionImportName(
                FunctionImportSample.class.getAnnotation(EdmFunctionImport.class),
                FunctionImportSample.class);

        assertEquals("ODataDemo.ODataDemoFunctionImport", fullyQualifiedFunctionImportName);
    }

    @Test
    public void testFullyQualifiedWrongFunctionImportName() {
        String fullyQualifiedFunctionImportName = AnnotationFunctionImportFactory.getFullyQualifiedFunctionImportName(
                WrongFunctionImportSample.class.getAnnotation(EdmFunctionImport.class),
                WrongFunctionImportSample.class);

        assertEquals("com.sdl.odata.edm.factory.annotations.WrongFunctionImportSample",
                fullyQualifiedFunctionImportName);
    }

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
        factory.addClass(FunctionSample.class);
        factory.addClass(WrongFunctionImportSample.class);
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
        factory.addClass(FunctionSample.class);
        factory.addClass(FunctionImportWithoutEntitySetDefinedSample.class);
        factory.setSchemaAlias("ODataDemo", "TestAlias");

        factory.buildEntityDataModel();
    }

    /**
     * Function import sample without defined name and other annotation fields.
     */
    @EdmFunctionImport(entitySet = "Orders")
    public static class WrongFunctionImportSample {

        private String stringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }

    /**
     * Function import sample without defined entity set.
     */
    @EdmFunctionImport(namespace = "ODataDemo", function = "ODataDemoFunction")
    public static class FunctionImportWithoutEntitySetDefinedSample {

        private String stringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }
}
