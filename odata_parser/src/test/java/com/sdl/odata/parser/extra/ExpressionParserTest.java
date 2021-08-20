/*
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
package com.sdl.odata.parser.extra;

import com.sdl.odata.api.parser.EntityPathExpr;
import com.sdl.odata.api.parser.EntitySetRootExpr;
import com.sdl.odata.api.parser.EqExpr;
import com.sdl.odata.api.parser.FilterOption;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.PathExpr;
import com.sdl.odata.api.parser.PropertyPathExpr;
import com.sdl.odata.api.parser.QueryOption;
import com.sdl.odata.api.parser.ResourcePathUri;
import com.sdl.odata.api.parser.SimpleKeyPredicate;
import com.sdl.odata.api.parser.StringLiteral;
import com.sdl.odata.parser.ODataUriParser;
import com.sdl.odata.parser.ParserTestSuite;
import org.junit.Test;
import scala.Option;
import scala.collection.Iterator;
import scala.collection.immutable.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Expression Parser Test.
 */
public class ExpressionParserTest extends ParserTestSuite {

    @Test
    public void testEntitySetRootElement() {
        ODataUriParser parser = new ODataUriParser(model);
        ODataUri target = parser.parseUri(SERVICE_ROOT + "Customers?$filter=Phone eq $root/Customers('A1245')/Phone");

        assertThat(target.serviceRoot() + "/", is(SERVICE_ROOT));

        ResourcePathUri resourcePathUri = (ResourcePathUri) target.relativeUri();

        List<QueryOption> options = resourcePathUri.options();
        assertThat(options.size(), is(1));

        Iterator<QueryOption> iter = options.iterator();

        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof FilterOption) {
                FilterOption option = (FilterOption) obj;

                EqExpr expr = (EqExpr) option.expression();
                EntityPathExpr pathExpr = (EntityPathExpr) expr.left();
                Option<PathExpr> subPath = pathExpr.subPath();
                PropertyPathExpr propertyPath = (PropertyPathExpr) subPath.get();

                assertThat(propertyPath.propertyName(), is(notNullValue()));
                assertThat(propertyPath.propertyName(), is("Phone"));

                EntitySetRootExpr rootExpr = (EntitySetRootExpr) expr.right();
                assertThat(rootExpr.entitySetName(), is("Customers"));

                SimpleKeyPredicate predicate = (SimpleKeyPredicate) rootExpr.keyPredicate();
                StringLiteral value = (StringLiteral) predicate.value();
                assertThat(value.value(), is("A1245"));

            }
        }
    }

}
