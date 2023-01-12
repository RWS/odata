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
package com.sdl.odata.edm.factory.annotations;

import com.google.common.collect.Lists;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.Facets;
import com.sdl.odata.api.edm.model.FunctionImport;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.OnDeleteAction;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.Property;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.test.model.ActionImportSample;
import com.sdl.odata.test.model.ActionSample;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.EntityWithNameSpaceSample;
import com.sdl.odata.test.model.ExampleFlags;
import com.sdl.odata.test.model.FunctionImportSample;
import com.sdl.odata.test.model.FunctionSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.OrderLine;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.UnboundActionSample;
import com.sdl.odata.test.model.UnboundFunctionSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AnnotationEntityDataModelFactory}.
 *
 */
public class AnnotationEntityDataModelFactoryTest {

    private static final String NAMESPACE = "ODataDemo";
    private static final String ALIAS = "TestAlias";

    private AnnotationEntityDataModelFactory factory;
    private EntityDataModel model;

    @BeforeEach
    public void setUp() throws Exception {
        factory = new AnnotationEntityDataModelFactory();

        factory.addClass(Address.class);
        factory.addClass(Category.class);
        factory.addClass(Customer.class);
        factory.addClass(Order.class);
        factory.addClass(OrderLine.class);
        factory.addClass(Product.class);
        factory.addClass(ExampleFlags.class);
        factory.addClass(FunctionSample.class);
        factory.addClass(UnboundFunctionSample.class);
        factory.addClass(FunctionImportSample.class);
        factory.addClass(ActionSample.class);
        factory.addClass(UnboundActionSample.class);
        factory.addClass(ActionImportSample.class);
        factory.setSchemaAlias(NAMESPACE, ALIAS);

        model = factory.buildEntityDataModel();
    }

    @Test
    public void testIncorrectClassAdd() {
        assertThrows(IllegalArgumentException.class, () -> factory.addClass(Object.class));
    }

    @Test
    public void testAddClassesMethod() throws ODataEdmException {
        factory = new AnnotationEntityDataModelFactory();

        factory.addClasses(Lists.newArrayList(
                Address.class,
                Category.class,
                Customer.class,
                Order.class,
                OrderLine.class,
                Product.class,
                ExampleFlags.class,
                FunctionSample.class,
                UnboundFunctionSample.class,
                FunctionImportSample.class,
                ActionSample.class,
                UnboundActionSample.class,
                ActionImportSample.class
        ));


        model = factory.buildEntityDataModel();

        assertEquals(4, model.getEntityContainer().getEntitySets().size());
        assertEquals(1, model.getEntityContainer().getActionImports().size());
        assertEquals(1, model.getEntityContainer().getFunctionImports().size());
        assertEquals(2, model.getSchemas().size());
    }

    @Test
    public void testSchemaLookup() {
        assertEquals(2, model.getSchemas().size());

        // Lookup schema by namespace
        Schema schema = model.getSchema(NAMESPACE);
        assertNotNull(schema);
        assertEquals(NAMESPACE, schema.getNamespace());
        assertEquals(ALIAS, schema.getAlias());

        // Lookup schema by alias
        schema = model.getSchema(ALIAS);
        assertNotNull(schema);
        assertEquals(NAMESPACE, schema.getNamespace());
        assertEquals(ALIAS, schema.getAlias());
    }

    @Test
    public void testTypeLookupByFullyQualifiedName() {
        // Lookup type by fully-qualified name
        String customerFullyQualifiedName = NAMESPACE + "." + Customer.class.getSimpleName();
        Type customerType = model.getType(customerFullyQualifiedName);
        assertNotNull(customerType);
        assertEquals(customerFullyQualifiedName, customerType.getFullyQualifiedName());
    }

    @Test
    public void testTypeLookupByAliasQualifiedName() {
        // Lookup type by alias-qualified name
        String customerFullyQualifiedName = NAMESPACE + "." + Customer.class.getSimpleName();
        String customerAliasQualifiedName = ALIAS + "." + Customer.class.getSimpleName();
        Type customerType = model.getType(customerAliasQualifiedName);
        assertNotNull(customerType);
        assertEquals(customerFullyQualifiedName, customerType.getFullyQualifiedName());
    }

    @Test
    public void testTypeLookupByJavaType() {
        Type customerType = model.getType(Customer.class);
        assertEquals(NAMESPACE + "." + Customer.class.getSimpleName(), customerType.getFullyQualifiedName());

        Type orderType = model.getType(Order.class);
        assertEquals(NAMESPACE + "." + Order.class.getSimpleName(), orderType.getFullyQualifiedName());
        assertEquals(MetaType.ENTITY, orderType.getMetaType());
    }

    @Test
    public void testEntityContainer() {
        EntityContainer entityContainer = model.getEntityContainer();

        assertEquals("ODataDemoContainer", entityContainer.getName());
        assertEquals("ODataDemo", entityContainer.getNamespace());
        assertNull(entityContainer.getBaseEntityContainerName());
        assertEquals(4, entityContainer.getEntitySets().size());

        List<String> entitySetsNames = Lists.transform(entityContainer.getEntitySets(),
                EntitySet::getName);

        assertTrue(entitySetsNames.containsAll(List.of("Customers", "Products", "Orders", "OrderLines")));
        assertTrue(entityContainer.getSingletons().isEmpty());
        assertEquals(1, entityContainer.getFunctionImports().size());

        FunctionImport functionImport = entityContainer.getFunctionImports().get(0);
        assertTrue(functionImport.isIncludeInServiceDocument());
        assertEquals("ODataDemoFunctionImport", functionImport.getName());

        EntitySet entitySet = functionImport.getEntitySet();
        assertNull(entitySet);

        com.sdl.odata.api.edm.model.Function function = functionImport.getFunction();
        assertEquals("", function.getEntitySetPath());
        assertEquals("UnboundODataDemoFunction", function.getName());
        assertEquals("ODataDemo", function.getNamespace());
        assertFalse(function.getParameters().isEmpty());
        assertEquals("Edm.String", function.getReturnType());

        assertFalse(function.isBound());
        assertFalse(function.isComposable());

        assertEquals(1, entityContainer.getActionImports().size());
        ActionImport actionImport = entityContainer.getActionImports().get(0);
        assertEquals("ODataDemoActionImport", actionImport.getName());
        entitySet = actionImport.getEntitySet();
        assertEquals("Customers", entitySet.getName());
        assertEquals(1, entitySet.getNavigationPropertyBindings().size());
        assertEquals("ODataDemo.Customer", entitySet.getTypeName());
        assertTrue(entitySet.isIncludedInServiceDocument());

        Action action = actionImport.getAction();
        assertEquals("ODataDemoEntitySetPath", action.getEntitySetPath());
        assertEquals("ODataDemoUnboundAction", action.getName());
        assertEquals("ODataDemo", action.getNamespace());
        assertEquals(2, action.getParameters().size());
        assertEquals("Customers", action.getReturnType());
        assertFalse(action.isBound());
    }

    @Test
    public void testEntityContainerNameSpace() throws ODataEdmException {
        AnnotationEntityDataModelFactory testFactory = new AnnotationEntityDataModelFactory();
        testFactory.addClass(EntityWithNameSpaceSample.class);

        EntityDataModel model2 = testFactory.buildEntityDataModel();

        EntityContainer entityContainer = model2.getEntityContainer();

        assertEquals("ODataDemoSampleContainer", entityContainer.getName());
        assertEquals("ODataDemo.SampleContainer", entityContainer.getNamespace());
    }

    @Test
    public void testAddressStructure() {
        // Test if everything in the type Address is as expected
        Type type = model.getType(Address.class);
        assertTrue(type instanceof ComplexType);

        ComplexType addressType = (ComplexType) type;
        assertEquals(MetaType.COMPLEX, addressType.getMetaType());
        assertEquals(5, addressType.getStructuralProperties().size());

        Property streetProp = (Property) addressType.getStructuralProperty("Street");
        assertNotNull(streetProp);
        assertEquals("Street", streetProp.getName());
        assertEquals(PrimitiveType.STRING.getFullyQualifiedName(), streetProp.getTypeName());
        assertNull(streetProp.getElementTypeName());
        assertFalse(streetProp.isCollection());
        assertFalse(streetProp.isNullable());
        assertNull(streetProp.getDefaultValue());
        assertEquals(60L, streetProp.getMaxLength());
        assertEquals(Facets.PRECISION_UNSPECIFIED, streetProp.getPrecision());
        assertEquals(Facets.SCALE_UNSPECIFIED, streetProp.getScale());
        assertEquals(Facets.SRID_UNSPECIFIED, streetProp.getSRID());
        assertTrue(streetProp.isUnicode());
    }

    @Test
    public void testCustomerStructure() throws NoSuchFieldException {
        // Test if everything in the type Customer is as expected
        Type type = model.getType(Customer.class);
        assertTrue(type instanceof EntityType);

        EntityType customerType = (EntityType) type;
        assertEquals(MetaType.ENTITY, customerType.getMetaType());
        assertNull(customerType.getBaseTypeName());
        assertFalse(customerType.isAbstract());
        assertEquals(7, customerType.getStructuralProperties().size());

        Property idProp = (Property) customerType.getStructuralProperty("id");
        assertEquals("id", idProp.getName());
        assertEquals(PrimitiveType.INT64.getFullyQualifiedName(), idProp.getTypeName());

        assertNull(idProp.getElementTypeName());
        assertFalse(idProp.isCollection());
        assertFalse(idProp.isNullable());
        assertEquals(Customer.class.getDeclaredField("id").getName(), idProp.getJavaField().getName());
        assertNull(idProp.getDefaultValue());
        assertEquals(Facets.MAX_LENGTH_UNSPECIFIED, idProp.getMaxLength());
        assertEquals(Facets.PRECISION_UNSPECIFIED, idProp.getPrecision());
        assertEquals(Facets.SCALE_UNSPECIFIED, idProp.getScale());
        assertEquals(Facets.SRID_UNSPECIFIED, idProp.getSRID());
        assertTrue(idProp.isUnicode());

        Property addressProp = (Property) customerType.getStructuralProperty("address");
        assertEquals("address", addressProp.getName());
        assertEquals("Collection(" +
                model.getType(Address.class).getFullyQualifiedName() + ")", addressProp.getTypeName());
        assertNotNull(addressProp.getElementTypeName());
        assertTrue(addressProp.isCollection());
        assertFalse(addressProp.isNullable());
        assertEquals(Customer.class.getDeclaredField("address").getName(), addressProp.getJavaField().getName());
        assertNull(addressProp.getDefaultValue());

        assertEquals(Facets.MAX_LENGTH_UNSPECIFIED, addressProp.getMaxLength());
        assertEquals(Facets.PRECISION_UNSPECIFIED, addressProp.getPrecision());
        assertEquals(Facets.SCALE_UNSPECIFIED, addressProp.getScale());
        assertEquals(Facets.SRID_UNSPECIFIED, addressProp.getSRID());
        assertTrue(addressProp.isUnicode());

        NavigationProperty ordersNavProp = (NavigationProperty) customerType.getStructuralProperty("Orders");
        assertEquals("Orders", ordersNavProp.getName());
        assertEquals("Collection(" + model.getType(Order.class).getFullyQualifiedName() + ")",
                ordersNavProp.getTypeName());

        assertEquals(model.getType(Order.class).getFullyQualifiedName(), ordersNavProp.getElementTypeName());
        assertTrue(ordersNavProp.isCollection());
        assertFalse(ordersNavProp.isNullable());
        assertEquals(Customer.class.getDeclaredField("orders").getName(), ordersNavProp.getJavaField().getName());

        assertEquals("customer", ordersNavProp.getPartnerName());

        assertFalse(ordersNavProp.containsTarget());
        assertEquals(0, ordersNavProp.getReferentialConstraints().size());

        List<OnDeleteAction> onDeleteActions = ordersNavProp.getOnDeleteActions();

        assertEquals(1, onDeleteActions.size());
        assertEquals(OnDeleteAction.NONE, onDeleteActions.get(0));
    }

    @Test
    public void testCategory() {
        // Test if everything in the type Category is as expected
        Type type = model.getType(Category.class);
        assertTrue(type instanceof EnumType);
        assertEquals(MetaType.ENUM, type.getMetaType());
    }

    @Test
    public void testExampleFlags() {
        Type type = model.getType(ExampleFlags.class);
        assertTrue(type instanceof EnumType);
        assertEquals(MetaType.ENUM, type.getMetaType());
    }
}
