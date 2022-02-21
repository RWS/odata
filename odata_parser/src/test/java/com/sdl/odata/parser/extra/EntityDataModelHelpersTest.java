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
package com.sdl.odata.parser.extra;

import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.Property;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.edm.model.TypeDefinition;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.parser.ParserTestSuite;
import com.sdl.odata.test.model.Address;
import org.junit.Before;
import org.junit.Test;
import scala.Option;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    @Before
    public void setup() {
        parser = new ODataUriParser(model);
    }

    @Test
    public void testEntityHelpersMethods() {
        assertThat(parser.isEntityType(ORDER), is(true));
        assertThat(parser.isEntityType("Edm.Int64"), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentEntityCheck() {
        assertThat(parser.isEntityType("AnyAnotherEntity"), is(true));
    }

    @Test
    public void testEntityDefinition() {
        Option<TypeDefinition> type = parser.getTypeDefinition(ORDER);
        assertFalse(type.isDefined());
    }

    @Test
    public void testKeyProperty() {
        assertThat(parser.isPrimitiveKeyPropertyOf(ORDER, "id"), is(true));
    }


    @Test
    public void testNavigationProperty() {
        Type type = model.getType(Address.class);
        ComplexType addressType = (ComplexType) type;
        Property streetProp = (Property) addressType.getStructuralProperty("Street");

        assertThat(parser.isEntityNavigationProperty(streetProp), is(false));
        assertThat(parser.isPrimitiveCollectionProperty(streetProp), is(false));
        assertThat(parser.isEntitySingleNavigationProperty(streetProp), is(false));
        assertThat(parser.isEntityCollectionNavigationProperty(streetProp), is(false));
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
