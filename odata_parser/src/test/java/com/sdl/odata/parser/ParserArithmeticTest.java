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
package com.sdl.odata.parser;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.AddExpr;
import com.sdl.odata.api.parser.ArithmeticExpr;
import com.sdl.odata.api.parser.DivExpr;
import com.sdl.odata.api.parser.EntityPathExpr;
import com.sdl.odata.api.parser.FilterOption;
import com.sdl.odata.api.parser.GtExpr;
import com.sdl.odata.api.parser.LiteralExpr;
import com.sdl.odata.api.parser.ModExpr;
import com.sdl.odata.api.parser.MulExpr;
import com.sdl.odata.api.parser.NumberLiteral;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.PropertyPathExpr;
import com.sdl.odata.api.parser.SubExpr;
import org.junit.Test;
import scala.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Parser Arithmetic Test.
 *
 */
public class ParserArithmeticTest extends ParserTestSuite {

    @Test
    public void testArithmeticOperators() throws ODataException {
        GtExpr addGtExpr = getExprFromOperator("add");
        AddExpr addExpr = (AddExpr) addGtExpr.left();
        processArithmeticTree(addExpr, addGtExpr);

        GtExpr subGtExpr = getExprFromOperator("sub");
        SubExpr subExpr = (SubExpr) subGtExpr.left();
        processArithmeticTree(subExpr, subGtExpr);

        GtExpr mulGtExpr = getExprFromOperator("mul");
        MulExpr mulExpr = (MulExpr) mulGtExpr.left();
        processArithmeticTree(mulExpr, mulGtExpr);

        GtExpr divGtExpr = getExprFromOperator("div");
        DivExpr divExpr = (DivExpr) divGtExpr.left();
        processArithmeticTree(divExpr, divGtExpr);

        GtExpr modGtExpr = getExprFromOperator("mod");
        ModExpr modExpr = (ModExpr) modGtExpr.left();
        processArithmeticTree(modExpr, modGtExpr);
    }

    private void processArithmeticTree(ArithmeticExpr addExpr, GtExpr expression) {
        EntityPathExpr entityPathExpr = (EntityPathExpr) addExpr.left();
        PropertyPathExpr propertyPathExpr = (PropertyPathExpr) entityPathExpr.subPath().get();
        assertThat(propertyPathExpr.propertyName(), is("id"));

        LiteralExpr literalExpr = (LiteralExpr) addExpr.right();
        NumberLiteral nubmerLiteral = (NumberLiteral) literalExpr.value();
        assertThat(nubmerLiteral.value(), is(new BigDecimal(new java.math.BigDecimal(5))));

        LiteralExpr literal = (LiteralExpr) expression.right();
        NumberLiteral number = (NumberLiteral) literal.value();
        assertThat(number.value(), is(new BigDecimal(new java.math.BigDecimal(10))));
    }

    public GtExpr getExprFromOperator(String operator) throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id " + operator + " 5 gt 10", model);
        FilterOption option = getSingleOption(uri);
        return (GtExpr) option.expression();
    }
}
