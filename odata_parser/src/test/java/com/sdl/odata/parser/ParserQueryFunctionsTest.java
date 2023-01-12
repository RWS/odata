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
package com.sdl.odata.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.EntityPathExpr;
import com.sdl.odata.api.parser.EqExpr;
import com.sdl.odata.api.parser.Expression;
import com.sdl.odata.api.parser.FilterOption;
import com.sdl.odata.api.parser.LiteralExpr;
import com.sdl.odata.api.parser.MethodCallExpr;
import com.sdl.odata.api.parser.NumberLiteral;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.PropertyPathExpr;
import org.junit.jupiter.api.Test;
import scala.collection.Iterator;
import scala.collection.immutable.List;
import scala.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parser Query Functions Test.
 *
 */
public class ParserQueryFunctionsTest extends ParserTestSuite {

    @Test
    public void testQueryFunctionsTest() throws ODataException {
        String[] array = new String[]{"length", "indexof", "substring", "tolower", "toupper", "trim", "concat",
                "year", "month", "day", "hour", "minute", "second", "fractionalseconds", "totalseconds",
                "date", "time", "totaloffsetminutes", "mindatetime", "maxdatetime", "now", "round", "floor",
                "ceiling", "geo.distance", "geo.length"};
        for (String operator : array) {
            testQueryFunction(operator);
        }
    }

    private void testQueryFunction(String operator) throws ODataException {
        EqExpr expr = getExprFromOperator(operator);
        MethodCallExpr call = (MethodCallExpr) expr.left();
        assertEquals(operator, call.methodName());
        List<Expression> args = call.args();
        Iterator iter = args.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof EntityPathExpr) {
                EntityPathExpr entityPathExpr = (EntityPathExpr) obj;
                PropertyPathExpr propertyPath = (PropertyPathExpr) entityPathExpr.subPath().get();
                assertEquals("name", propertyPath.propertyName());
            }
        }
        LiteralExpr literal = (LiteralExpr) expr.right();
        NumberLiteral number = (NumberLiteral) literal.value();
        assertEquals(new BigDecimal(new java.math.BigDecimal(19)), number.value());
    }


    public EqExpr getExprFromOperator(String operator) throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Customers?$filter=" + operator + "(name) eq 19", model);
        FilterOption option = getSingleOption(uri);
        return (EqExpr) option.expression();
    }

}
