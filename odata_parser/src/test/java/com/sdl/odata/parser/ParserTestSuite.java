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
package com.sdl.odata.parser;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ComparisonExpr;
import com.sdl.odata.api.parser.EntityPathExpr;
import com.sdl.odata.api.parser.FilterOption;
import com.sdl.odata.api.parser.LiteralExpr;
import com.sdl.odata.api.parser.NumberLiteral;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.PropertyPathExpr;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.test.model.ActionImportSample;
import com.sdl.odata.test.model.ActionSample;
import com.sdl.odata.test.model.Address;
import com.sdl.odata.test.model.CollectionsSample;
import com.sdl.odata.test.model.Customer;
import com.sdl.odata.test.model.FunctionImportSample;
import com.sdl.odata.test.model.FunctionSample;
import com.sdl.odata.test.model.IdNamePairSample;
import com.sdl.odata.test.model.Order;
import com.sdl.odata.test.model.OrderLine;
import com.sdl.odata.test.model.PrimitiveTypesSample;
import com.sdl.odata.test.model.Product;
import com.sdl.odata.test.model.UnboundActionSample;
import com.sdl.odata.test.model.UnboundFunctionSample;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.List;
import scala.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Parser Test Suite.
 */
public class ParserTestSuite {
    /**
     * OData URI.
     */
    public ODataUri uri;
    /**
     * OData Parser.
     */
    public ODataParserImpl parser = new ODataParserImpl();
    /**
     * Service Root URL.
     */
    public static final String SERVICE_ROOT = "http://localhost:8080/odata.svc/";

    private static final Logger LOG = LoggerFactory.getLogger(ParserLogicalTest.class);

    protected EntityDataModel model;

    @Before
    public void setUp() throws Exception {
        LOG.info("Initializing EntityDataModel");
        AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
        factory.addClass(Address.class);
        factory.addClass(CollectionsSample.class);
        factory.addClass(Customer.class);
        factory.addClass(IdNamePairSample.class);
        factory.addClass(Order.class);
        factory.addClass(OrderLine.class);
        factory.addClass(PrimitiveTypesSample.class);
        factory.addClass(Product.class);
        factory.addClass(ActionSample.class);
        factory.addClass(UnboundActionSample.class);
        factory.addClass(ActionImportSample.class);
        factory.addClass(FunctionSample.class);
        factory.addClass(UnboundFunctionSample.class);
        factory.addClass(FunctionImportSample.class);

        model = factory.buildEntityDataModel();
    }


    public void processLeftAndRight(ComparisonExpr expr) {
        EntityPathExpr left = (EntityPathExpr) expr.left();
        LiteralExpr right = (LiteralExpr) expr.right();
        PropertyPathExpr propertyPath = (PropertyPathExpr) left.subPath().get();
        assertThat(propertyPath.propertyName(), is("id"));
        NumberLiteral numberLiteral = (NumberLiteral) right.value();
        assertThat(numberLiteral.value(), is(new BigDecimal(new java.math.BigDecimal(20))));
    }

    public FilterOption getSingleOption(ODataUri oDataUri) {
        ResourcePathUri relative = (ResourcePathUri) oDataUri.relativeUri();
        List<QueryOption> options = relative.options();
        assertThat(options.size(), is(1));
        return (FilterOption) options.head();
    }

}
