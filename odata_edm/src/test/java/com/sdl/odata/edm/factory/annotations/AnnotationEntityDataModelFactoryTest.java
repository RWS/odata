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
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AnnotationEntityDataModelFactory}.
 *
 */
public class AnnotationEntityDataModelFactoryTest {

    private static final String NAMESPACE = "ODataDemo";
    private static final String ALIAS = "TestAlias";

    private AnnotationEntityDataModelFactory factory;
    private EntityDataModel model;

    @Before
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

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectClassAdd() {
        factory.addClass(Object.class);
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

        assertThat(model.getEntityContainer().getEntitySets().size(), is(4));
        assertThat(model.getEntityContainer().getActionImports().size(), is(1));
        assertThat(model.getEntityContainer().getFunctionImports().size(), is(1));
        assertThat(model.getSchemas().size(), is(2));
    }

    @Test
    public void testSchemaLookup() {
        assertThat(model.getSchemas().size(), is(2));

        // Lookup schema by namespace
        Schema schema = model.getSchema(NAMESPACE);
        assertNotNull(schema);
        assertThat(schema.getNamespace(), is(NAMESPACE));
        assertThat(schema.getAlias(), is(ALIAS));

        // Lookup schema by alias
        schema = model.getSchema(ALIAS);
        assertNotNull(schema);
        assertThat(schema.getNamespace(), is(NAMESPACE));
        assertThat(schema.getAlias(), is(ALIAS));
    }

    @Test
    public void testTypeLookupByFullyQualifiedName() {
        // Lookup type by fully-qualified name
        String customerFullyQualifiedName = NAMESPACE + "." + Customer.class.getSimpleName();
        Type customerType = model.getType(customerFullyQualifiedName);
        assertNotNull(customerType);
        assertThat(customerType.getFullyQualifiedName(), is(customerFullyQualifiedName));
    }

    @Test
    public void testTypeLookupByAliasQualifiedName() {
        // Lookup type by alias-qualified name
        String customerFullyQualifiedName = NAMESPACE + "." + Customer.class.getSimpleName();
        String customerAliasQualifiedName = ALIAS + "." + Customer.class.getSimpleName();
        Type customerType = model.getType(customerAliasQualifiedName);
        assertNotNull(customerType);
        assertThat(customerType.getFullyQualifiedName(), is(customerFullyQualifiedName));
    }

    @Test
    public void testTypeLookupByJavaType() {
        Type customerType = model.getType(Customer.class);
        assertThat(customerType.getFullyQualifiedName(), is(NAMESPACE + "." + Customer.class.getSimpleName()));

        Type orderType = model.getType(Order.class);
        assertThat(orderType.getFullyQualifiedName(), is(NAMESPACE + "." + Order.class.getSimpleName()));
        assertThat(orderType.getMetaType(), is(MetaType.ENTITY));
    }

    @Test
    public void testEntityContainer() {
        EntityContainer entityContainer = model.getEntityContainer();

        assertThat(entityContainer.getName(), is("ODataDemoContainer"));
        assertThat(entityContainer.getNamespace(), is("ODataDemo"));
        assertThat(entityContainer.getBaseEntityContainerName(), is(nullValue()));

        assertThat(entityContainer.getEntitySets().size(), is(4));
        List<String> entitySetsNames = Lists.transform(entityContainer.getEntitySets(),
                EntitySet::getName);
        assertThat(entitySetsNames, hasItems("Customers", "Products", "Orders", "OrderLines"));

        assertThat(entityContainer.getSingletons().isEmpty(), is(true));

        assertThat(entityContainer.getFunctionImports().size(), is(1));
        FunctionImport functionImport = entityContainer.getFunctionImports().get(0);
        assertTrue(functionImport.isIncludeInServiceDocument());
        assertThat(functionImport.getName(), is("ODataDemoFunctionImport"));
        EntitySet entitySet = functionImport.getEntitySet();
        assertNull(entitySet);

        com.sdl.odata.api.edm.model.Function function = functionImport.getFunction();
        assertThat(function.getEntitySetPath(), is(""));
        assertThat(function.getName(), is("UnboundODataDemoFunction"));
        assertThat(function.getNamespace(), is("ODataDemo"));
        assertThat(function.getParameters().isEmpty(), is(false));
        assertThat(function.getReturnType(), is("Edm.String"));
        assertThat(function.isBound(), is(false));
        assertThat(function.isComposable(), is(false));

        assertThat(entityContainer.getActionImports().size(), is(1));
        ActionImport actionImport = entityContainer.getActionImports().get(0);
        assertThat(actionImport.getName(), is("ODataDemoActionImport"));
        entitySet = actionImport.getEntitySet();
        assertThat(entitySet.getName(), is("Customers"));
        assertThat(entitySet.getNavigationPropertyBindings().size(), is(1));
        assertThat(entitySet.getTypeName(), is("ODataDemo.Customer"));
        assertThat(entitySet.isIncludedInServiceDocument(), is(true));

        Action action = actionImport.getAction();
        assertThat(action.getEntitySetPath(), is("ODataDemoEntitySetPath"));
        assertThat(action.getName(), is("ODataDemoUnboundAction"));
        assertThat(action.getNamespace(), is("ODataDemo"));
        assertThat(action.getParameters().size(), is(2));
        assertThat(action.getReturnType(), is("Customers"));
        assertThat(action.isBound(), is(false));
    }

    @Test
    public void testEntityContainerNameSpace() throws ODataEdmException {
        AnnotationEntityDataModelFactory testFactory = new AnnotationEntityDataModelFactory();
        testFactory.addClass(EntityWithNameSpaceSample.class);

        EntityDataModel model2 = testFactory.buildEntityDataModel();

        EntityContainer entityContainer = model2.getEntityContainer();

        assertThat(entityContainer.getName(), is("ODataDemoSampleContainer"));
        assertThat(entityContainer.getNamespace(), is("ODataDemo.SampleContainer"));
    }

    @Test
    public void testAddressStructure() {
        // Test if everything in the type Address is as expected
        Type type = model.getType(Address.class);
        assertTrue(type instanceof ComplexType);

        ComplexType addressType = (ComplexType) type;
        assertThat(addressType.getMetaType(), is(MetaType.COMPLEX));
        assertThat(addressType.getStructuralProperties().size(), is(5));

        Property streetProp = (Property) addressType.getStructuralProperty("Street");
        assertNotNull(streetProp);
        assertThat(streetProp.getName(), is("Street"));
        assertThat(streetProp.getTypeName(), is(PrimitiveType.STRING.getFullyQualifiedName()));
        assertNull(streetProp.getElementTypeName());
        assertFalse(streetProp.isCollection());
        assertFalse(streetProp.isNullable());
        assertNull(streetProp.getDefaultValue());
        assertThat(streetProp.getMaxLength(), is(60L));
        assertThat(streetProp.getPrecision(), is(Facets.PRECISION_UNSPECIFIED));
        assertThat(streetProp.getScale(), is(Facets.SCALE_UNSPECIFIED));
        assertThat(streetProp.getSRID(), is(Facets.SRID_UNSPECIFIED));
        assertTrue(streetProp.isUnicode());
    }

    @Test
    public void testCustomerStructure() throws NoSuchFieldException {
        // Test if everything in the type Customer is as expected
        Type type = model.getType(Customer.class);
        assertTrue(type instanceof EntityType);

        EntityType customerType = (EntityType) type;
        assertThat(customerType.getMetaType(), is(MetaType.ENTITY));
        assertNull(customerType.getBaseTypeName());
        assertFalse(customerType.isAbstract());
        assertThat(customerType.getStructuralProperties().size(), is(8));

        Property idProp = (Property) customerType.getStructuralProperty("id");
        assertThat(idProp.getName(), is("id"));
        assertThat(idProp.getTypeName(), is(PrimitiveType.INT64.getFullyQualifiedName()));
        assertNull(idProp.getElementTypeName());
        assertFalse(idProp.isCollection());
        assertFalse(idProp.isNullable());
        assertThat(idProp.getJavaField().getName(), is(Customer.class.getDeclaredField("id").getName()));
        assertNull(idProp.getDefaultValue());
        assertThat(idProp.getMaxLength(), is(Facets.MAX_LENGTH_UNSPECIFIED));
        assertThat(idProp.getPrecision(), is(Facets.PRECISION_UNSPECIFIED));
        assertThat(idProp.getScale(), is(Facets.SCALE_UNSPECIFIED));
        assertThat(idProp.getSRID(), is(Facets.SRID_UNSPECIFIED));
        assertTrue(idProp.isUnicode());

        Property addressProp = (Property) customerType.getStructuralProperty("address");
        assertThat(addressProp.getName(), is("address"));
        assertThat(addressProp.getTypeName(), is("Collection(" +
                model.getType(Address.class).getFullyQualifiedName() + ")"));
        assertNotNull(addressProp.getElementTypeName());
        assertTrue(addressProp.isCollection());
        assertFalse(addressProp.isNullable());
        assertThat(addressProp.getJavaField().getName(), is(Customer.class.getDeclaredField("address").getName()));
        assertNull(addressProp.getDefaultValue());
        assertThat(addressProp.getMaxLength(), is(Facets.MAX_LENGTH_UNSPECIFIED));
        assertThat(addressProp.getPrecision(), is(Facets.PRECISION_UNSPECIFIED));
        assertThat(addressProp.getScale(), is(Facets.SCALE_UNSPECIFIED));
        assertThat(addressProp.getSRID(), is(Facets.SRID_UNSPECIFIED));
        assertTrue(addressProp.isUnicode());

        NavigationProperty ordersNavProp = (NavigationProperty) customerType.getStructuralProperty("Orders");
        assertThat(ordersNavProp.getName(), is("Orders"));
        assertThat(ordersNavProp.getTypeName(), is("Collection("
                + model.getType(Order.class).getFullyQualifiedName() + ")"));
        assertThat(ordersNavProp.getElementTypeName(), is(model.getType(Order.class).getFullyQualifiedName()));
        assertTrue(ordersNavProp.isCollection());
        assertFalse(ordersNavProp.isNullable());
        assertThat(ordersNavProp.getJavaField().getName(), is(Customer.class.getDeclaredField("orders").getName()));
        assertThat(ordersNavProp.getPartnerName(), is("customer"));
        assertFalse(ordersNavProp.containsTarget());

        assertThat(ordersNavProp.getReferentialConstraints().size(), is(0));

        List<OnDeleteAction> onDeleteActions = ordersNavProp.getOnDeleteActions();
        assertThat(onDeleteActions.size(), is(1));
        assertThat(onDeleteActions.get(0), is(OnDeleteAction.NONE));
    }

    @Test
    public void testCategory() {
        // Test if everything in the type Category is as expected
        Type type = model.getType(Category.class);
        assertTrue(type instanceof EnumType);
        assertThat(type.getMetaType(), is(MetaType.ENUM));
    }

    @Test
    public void testExampleFlags() {
        Type type = model.getType(ExampleFlags.class);
        assertTrue(type instanceof EnumType);
        assertThat(type.getMetaType(), is(MetaType.ENUM));
    }
}
