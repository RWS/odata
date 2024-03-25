/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.parser.extra;

import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.Property;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.edm.model.TypeDefinition;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.parser.ParserTestSuite;
import com.sdl.odata.test.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Entity Data Model Helpers Test.
 *
 */
public class EntityDataModelHelpersTest extends ParserTestSuite {

    /**
     * The OData Demo Order.
     */
    public static final String ORDER = "ODataDemo.Order";

    private ODataUriParser parser;

    @BeforeEach
    public void setup() {
        parser = new ODataUriParser(model);
    }

    @Test
    public void testEntityHelpersMethods() {
        assertTrue(parser.isEntityType(ORDER));
        assertFalse(parser.isEntityType("Edm.Int64"));
    }

    @Test
    public void testIllegalArgumentEntityCheck() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.isEntityType("AnyAnotherEntity")
        );
    }

    @Test
    public void testEntityDefinition() {
        Option<TypeDefinition> type = parser.getTypeDefinition(ORDER);
        assertFalse(type.isDefined());
    }

    @Test
    public void testKeyProperty() {
        assertTrue(parser.isPrimitiveKeyPropertyOf(ORDER, "id"));
    }

    @Test
    public void testNavigationProperty() {
        Type type = model.getType(Address.class);
        ComplexType addressType = (ComplexType) type;
        Property streetProp = (Property) addressType.getStructuralProperty("Street");

        assertFalse(parser.isEntityNavigationProperty(streetProp));
        assertFalse(parser.isPrimitiveCollectionProperty(streetProp));
        assertFalse(parser.isEntitySingleNavigationProperty(streetProp));
        assertFalse(parser.isEntityCollectionNavigationProperty(streetProp));
    }

    @Test
    public void testActionDefinition() {
        assertTrue(parser.isAction("ODataDemoAction"));
        assertFalse(parser.isAction("wrongActionName"));
    }

    @Test
    public void testActionImportDefinition() {
        assertTrue(parser.isActionImport("ODataDemoActionImport"));
        assertFalse(parser.isActionImport("wrongActionImportName"));
    }

    @Test
    public void testFunctionDefinition() {
        assertTrue(parser.isFunction("ODataDemoFunction"));
        assertFalse(parser.isFunction("wrongFunctionName"));
    }

    @Test
    public void testFunctionImportDefinition() {
        assertTrue(parser.isFunctionImport("ODataDemoFunctionImport"));
        assertFalse(parser.isFunctionImport("wrongFunctionImportName"));
    }

    @Test
    public void testGetFunctionReturnType() {
        String result = parser.getFunctionReturnType("ODataDemoFunction").get();
        assertNotNull(result);
        assertEquals("Edm.String", result);
    }

    @Test
    public void testGetFunctionImportReturnType() {
        String result = parser.getFunctionImportReturnType("ODataDemoFunctionImport").get();
        assertNotNull(result);
        assertEquals("Edm.String", result);
    }
}
