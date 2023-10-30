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
package com.sdl.odata.parser

import com.sdl.odata.api.parser._
import org.scalatest.FunSuite

class ExpressionsParsersTest extends FunSuite with ParserTestHelpers {

  test("Method call expr with comparison expr") {
    implicit val p = parser.boolCommonExpr("ODataDemo.Customer")

    testSuccess("indexof(name, 'lfreds') eq 1",
      EqExpr(
        MethodCallExpr("indexof",
          List(EntityPathExpr(None, Some(PropertyPathExpr("name", None))), LiteralExpr(StringLiteral("lfreds")))),
        LiteralExpr(NumberLiteral(1))
      )
    )
  }

  test("length method call with comparison expr") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://server/test.svc"
    val relativeUri = "/Customers?$filter=length(name) eq 19"
    testSuccess(serviceRoot + relativeUri,
      ODataUri("http://server/test.svc",
        ResourcePathUri(EntitySetPath("Customers", None), List(
          FilterOption(
            EqExpr(
              MethodCallExpr("length", List(EntityPathExpr(None, Some(PropertyPathExpr("name",None))))),
              LiteralExpr(NumberLiteral(19))
            )
          )
        ))
      )
    )
  }

}
