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
package com.sdl.odata.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.RelativeUri;
import com.sdl.odata.api.parser.ResourcePathUri;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Parser Order Test.
 *
 */
public class ParserOrderTest extends ParserTestSuite {

    @Test
    public void testFirstCustomer() throws ODataException {
        uri = parser.parseUri(SERVICE_ROOT + "Customers(0)", model);
        assertNotNull(uri);
        RelativeUri relative = uri.relativeUri();
        assertNotNull(relative);
        assertTrue(relative instanceof ResourcePathUri);
        assertTrue(relative.toString().contains("Customers"));
        assertTrue(relative.toString().contains("NumberLiteral(0)"));
    }

    @Test
    public void testSecondOrder() throws ODataException {
        uri = parser.parseUri(SERVICE_ROOT + "Orders(1)", model);
        RelativeUri relative = uri.relativeUri();
        assertNotNull(relative);
        assertTrue(relative.toString().contains("Orders"));
        assertTrue(relative.toString().contains("NumberLiteral(1)"));
        assertNotNull(uri);
    }

    @Test
    public void testProductsNumberLiteral() throws ODataException {
        // Notice , that i starts from -1 that is also valid
        for (int i = -1; i < 9; i++) {
            uri = parser.parseUri(SERVICE_ROOT + "Products(" + i + ")", model);
            RelativeUri relative = uri.relativeUri();
            assertNotNull(relative);
            assertTrue(relative.toString().contains("Products"));
            assertTrue(relative.toString().contains("NumberLiteral(" + i + ")"));
        }
    }

}
