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
package com.sdl.odata.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.AndExpr;
import com.sdl.odata.api.parser.BooleanMethodCallExpr;
import com.sdl.odata.api.parser.ComparisonExpr;
import com.sdl.odata.api.parser.CompositeExpr;
import com.sdl.odata.api.parser.EntityPathExpr;
import com.sdl.odata.api.parser.EqExpr;
import com.sdl.odata.api.parser.Expression;
import com.sdl.odata.api.parser.FilterOption;
import com.sdl.odata.api.parser.LeExpr;
import com.sdl.odata.api.parser.LiteralExpr;
import com.sdl.odata.api.parser.NumberLiteral;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.OrExpr;
import com.sdl.odata.api.parser.PropertyPathExpr;
import com.sdl.odata.api.parser.StringLiteral;
import org.junit.jupiter.api.Test;
import scala.collection.Iterator;
import scala.collection.immutable.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Parser Logical Test.
 *
 */
public class ParserLogicalTest extends ParserTestSuite {

    private static final String URI = "Products?$filter=id le 20 %s id eq 20";
    private static final String QUERY_URI = "Customers?$filter=%s(name,'John')";

    @Test
    public void testLogicalNegation() throws ODataException {
        testWithStringFunctions("startswith");
        testWithStringFunctions("endswith");
        testWithStringFunctions("contains");
        testWithStringFunctions("geo.intersects");
    }

    @Test
    public void testLogicalAnd() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + String.format(URI, "and"), model);
        FilterOption option = getSingleOption(uri);
        assertTrue(option.expression() instanceof AndExpr);
        AndExpr expr = (AndExpr) option.expression();
        processExpr(expr);
    }


    @Test
    public void testLogicalOr() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + String.format(URI, "or"), model);

        FilterOption option = getSingleOption(uri);
        assertTrue(option.expression() instanceof OrExpr);
        OrExpr expr = (OrExpr) option.expression();
        processExpr(expr);
    }

    private void testWithStringFunctions(String boolMethod) throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + String.format(QUERY_URI, boolMethod), model);
        processQueryFunction(getSingleOption(uri), boolMethod);
    }

    private void processQueryFunction(FilterOption option, String boolMethod) {
        BooleanMethodCallExpr methodCall = (BooleanMethodCallExpr) option.expression();
        assertEquals(boolMethod, methodCall.methodName());
        List<Expression> args = methodCall.args();
        Iterator iterator = args.iterator();
        while (iterator.hasNext()) {
            Object cursor = iterator.next();
            if (cursor instanceof EntityPathExpr) {
                EntityPathExpr pathExpr = (EntityPathExpr) cursor;
                PropertyPathExpr path = (PropertyPathExpr) pathExpr.subPath().get();
                assertEquals(boolMethod, methodCall.methodName());
                assertEquals("name", path.propertyName());
            } else if (cursor instanceof LiteralExpr) {
                LiteralExpr literalExpr = (LiteralExpr) cursor;
                StringLiteral stringLiteral = (StringLiteral) literalExpr.value();
                assertEquals("John", stringLiteral.value());
            }
        }
    }

    private void processExpr(CompositeExpr expr) {
        LeExpr leExpr = (LeExpr) expr.left();
        processExpr(leExpr);
        EqExpr eqExpr = (EqExpr) expr.right();
        processExpr(eqExpr);
    }

    private void processExpr(ComparisonExpr leExpr) {
        EntityPathExpr leftPathExpr = (EntityPathExpr) leExpr.left();
        PropertyPathExpr leftPath = (PropertyPathExpr) leftPathExpr.subPath().get();
        assertEquals("id", leftPath.propertyName());
        LiteralExpr literalExpr = (LiteralExpr) leExpr.right();
        NumberLiteral numberLiteral = (NumberLiteral) literalExpr.value();
        assertEquals(new scala.math.BigDecimal(new java.math.BigDecimal(20)), numberLiteral.value());
    }
}
