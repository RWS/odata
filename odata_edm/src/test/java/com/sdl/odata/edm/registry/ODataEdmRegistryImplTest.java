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
package com.sdl.odata.edm.registry;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.ExampleFlags;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.OrderLine;
import com.sdl.odata.test.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@code ODataEdmRegistryImpl}.
 */
public class ODataEdmRegistryImplTest {

    private ODataEdmRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new ODataEdmRegistryImpl();
    }

    @Test
    public void testRegisterClasses() throws ODataException {
        registry.registerClasses(Arrays.asList(Address.class, Category.class, Customer.class, ExampleFlags.class,
                Order.class, OrderLine.class, Product.class));

        EntityDataModel entityDataModel = registry.getEntityDataModel();
        assertEquals(2, entityDataModel.getSchemas().size());

        Schema schema = entityDataModel.getSchema("ODataDemo");
        assertNotNull(schema);
        assertEquals(6, schema.getTypes().size());
    }
}
