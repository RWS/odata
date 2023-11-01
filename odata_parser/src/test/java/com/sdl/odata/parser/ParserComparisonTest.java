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

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.parser.EqExpr;
import com.sdl.odata.api.parser.FilterOption;
import com.sdl.odata.api.parser.GeExpr;
import com.sdl.odata.api.parser.GtExpr;
import com.sdl.odata.api.parser.LeExpr;
import com.sdl.odata.api.parser.LtExpr;
import com.sdl.odata.api.parser.NeExpr;
import com.sdl.odata.api.parser.ODataUri;
import org.junit.jupiter.api.Test;

/**
 * Parser Comparison Test.
 *
 */
public class ParserComparisonTest extends ParserTestSuite {


    @Test
    public void testGreaterThanNumber() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id gt 20", model);
        FilterOption option = getSingleOption(uri);
        GtExpr expr = (GtExpr) option.expression();
        processLeftAndRight(expr);
    }

    @Test
    public void testGreaterOrEqual() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id ge 20", model);
        FilterOption option = getSingleOption(uri);
        GeExpr expr = (GeExpr) option.expression();
        processLeftAndRight(expr);
    }

    @Test
    public void testLessThan() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id lt 20", model);
        FilterOption option = getSingleOption(uri);
        LtExpr expr = (LtExpr) option.expression();
        processLeftAndRight(expr);
    }


    @Test
    public void testEqual() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id eq 20", model);
        FilterOption option = getSingleOption(uri);
        EqExpr expr = (EqExpr) option.expression();
        processLeftAndRight(expr);
    }

    @Test
    public void testNotEqual() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id ne 20", model);
        FilterOption option = getSingleOption(uri);
        NeExpr expr = (NeExpr) option.expression();
        processLeftAndRight(expr);
    }

    @Test
    public void testLessThanOrEqual() throws ODataException {
        ODataUri uri = parser.parseUri(SERVICE_ROOT + "Products?$filter=id le 20", model);
        FilterOption option = getSingleOption(uri);
        LeExpr expr = (LeExpr) option.expression();
        processLeftAndRight(expr);
    }
}
