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
package com.sdl.odata.parser

import com.sdl.odata.api.parser._
import com.sdl.odata.api.service.MediaType
import org.scalatest.FunSuite

class QueryOptionsParsersTest extends FunSuite with ParserTestHelpers {

  test("format") {
    implicit val p = parser.format

    testSuccess("$format=atom", FormatOption(MediaType.ATOM_XML))
    testSuccess("$format=ATOM", FormatOption(MediaType.ATOM_XML))

    testSuccess("$format=json", FormatOption(MediaType.JSON))
    testSuccess("$format=JSON", FormatOption(MediaType.JSON))

    testSuccess("$format=xml", FormatOption(MediaType.XML))
    testSuccess("$format=XML", FormatOption(MediaType.XML))

    testSuccess("$format=application/atom+xml", FormatOption(MediaType.ATOM_XML))
    testSuccess("$format=application/json", FormatOption(MediaType.JSON))
    testSuccess("$format=application/xml", FormatOption(MediaType.XML))

    testNoSuccess("$format=text")
  }

  test("andExpr") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://localhost:8080/odata.svc"
    val relativeUri = "/Products?$filter=id le 20 and id eq 15"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot, ResourcePathUri(
        EntitySetPath("Products", None), List(FilterOption(
          AndExpr(
            LeExpr(EntityPathExpr(None, Some(PropertyPathExpr("id", None))), LiteralExpr(NumberLiteral(20))),
            EqExpr(EntityPathExpr(None, Some(PropertyPathExpr("id", None))), LiteralExpr(NumberLiteral(15))))
        )
        )
      ))
    )
  }

  test("Multiple and/or expressions") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://localhost:8080/odata.svc"
    val relativeUri1 = "/Products?$filter=id le 20 or id gt 10 and name eq 'Computer'"
    val relativeUri2 = "/Products?$filter=id gt 10 and name eq 'Computer' or id le 20"

    // Shows that 'and' has precedence above 'or'

    testSuccess(serviceRoot + relativeUri1,
      ODataUri(
        serviceRoot,
        ResourcePathUri(
          EntitySetPath("Products", None),
          List(
            FilterOption(
              OrExpr(
                LeExpr(EntityPathExpr(None, Some(PropertyPathExpr("id", None))), LiteralExpr(NumberLiteral(20))),
                AndExpr(
                  GtExpr(EntityPathExpr(None, Some(PropertyPathExpr("id", None))), LiteralExpr(NumberLiteral(10))),
                  EqExpr(EntityPathExpr(None, Some(PropertyPathExpr("name", None))), LiteralExpr(StringLiteral("Computer")))
                )
              )
            )
          )
        )
      )
    )

    testSuccess(serviceRoot + relativeUri2,
      ODataUri(
        serviceRoot,
        ResourcePathUri(
          EntitySetPath("Products", None),
          List(
            FilterOption(
              OrExpr(
                AndExpr(
                  GtExpr(EntityPathExpr(None, Some(PropertyPathExpr("id", None))), LiteralExpr(NumberLiteral(10))),
                  EqExpr(EntityPathExpr(None, Some(PropertyPathExpr("name", None))), LiteralExpr(StringLiteral("Computer")))
                ),
                LeExpr(EntityPathExpr(None, Some(PropertyPathExpr("id", None))), LiteralExpr(NumberLiteral(20)))
              )
            )
          )
        )
      )
    )
  }

  test("orderby") {
    implicit val p = parser.orderby("ODataDemo.Customer")

    testSuccess("$orderby=name asc", OrderByOption(List(
      AscendingOrderByItem(EntityPathExpr(None, Some(PropertyPathExpr("name", None)))))))

    testSuccess("$orderby=name desc", OrderByOption(List(
      DescendingOrderByItem(EntityPathExpr(None, Some(PropertyPathExpr("name", None)))))))

    // If no 'asc' or 'desc' is specified, ascending order should be used
    testSuccess("$orderby=name", OrderByOption(List(
      AscendingOrderByItem(EntityPathExpr(None, Some(PropertyPathExpr("name", None)))))))
  }

  test("$refsupport") {
    implicit val p = parser.expand("ODataDemo.Customer")

    testSuccess("$expand=Orders/$ref",
      ExpandOption(
        List(
          PathRefExpandItem(None, NavigationPropertyExpandPathSegment("Orders", None), List())
        )
      )
    )
  }

  test("selectAddress") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$select=address"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            SelectOption(
              List(
                PathSelectItem(None, ComplexPropertySelectPathSegment("address", None, None))
              )
            )
          )
        )
      )
    )
  }

  test("$expand Orders") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$expand=Orders"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(ExpandOption(
            List(
              PathExpandItem(None,
                NavigationPropertyExpandPathSegment("Orders", None), List())))))))
  }

  test("$expand *") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$expand=*"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(EntitySetPath("Customers", None),
          List(ExpandOption(List(
            AllExpandItem(List())))))))
  }

  test("$expand & $level=10") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$expand=Orders($levels=10)"

    testSuccess(serviceRoot + relativeUri, ODataUri(serviceRoot,
      ResourcePathUri(EntitySetPath("Customers", None),
        List(ExpandOption(List(PathExpandItem(None,
          NavigationPropertyExpandPathSegment("Orders", None), List(
            LevelsQueryOption(10)))))))))
  }

  test("$expand & $apply") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers(1)?$expand=Orders($apply=groupby((id), aggregate($count as OrderCount)))"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(EntitySetPath("Customers",
          Some(EntityCollectionPath(None,
            Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(1)),None))))),
          List(ExpandOption(
            List(PathExpandItem(None,NavigationPropertyExpandPathSegment("Orders",None),
              List(ApplyOption(ApplyExpr("groupby",
                ApplyMethodCallExpr(ApplyPropertyExpr(
                  List(EntityPathExpr(None,Some(PropertyPathExpr("id",None))))),
                  ApplyFunctionExpr("aggregate","$count as OrderCount"))))))))))))
  }

  test("$expand + $ref") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$expand=Orders/$ref"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            ExpandOption(
              List(
                PathRefExpandItem(None,
                  NavigationPropertyExpandPathSegment("Orders", None), List())))))))
  }

  test("$expand + all refs expand item") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$expand=*/$ref,Orders"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            ExpandOption(List(
              AllRefExpandItem,
              PathExpandItem(None,
                NavigationPropertyExpandPathSegment("Orders", None), List())))))))
  }


  test("$expand custom options") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$expand=Orders&$skip=1&$orderby=$it&$top=1"

    testSuccess(serviceRoot + relativeUri, ODataUri(
      serviceRoot,
      ResourcePathUri(EntitySetPath("Customers", None),
        List(ExpandOption(
          List(PathExpandItem(None,
            NavigationPropertyExpandPathSegment("Orders", None), List()))),
          SkipOption(1),
          OrderByOption(List(
            AscendingOrderByItem(ImplicitVariableExpr(None)))),
          TopOption(1)))))
  }

  test("$select terminal property") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$select=Phone"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            SelectOption(
              List(
                PathSelectItem(None,
                  TerminalPropertySelectPathSegment("Phone"))))))))
  }

  test("$select + function") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$select=com.sdl.example.*"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            SelectOption(
              List(
                SchemaAllSelectItem("com.sdl.example")))))))
  }

  test("multiple expands") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val multiExpandUri = "/Customers?$select=SomeCategory,Orders&$expand=id"
    // not supported for now
    testNoSuccess(serviceRoot + multiExpandUri)

    val includedExpandUri = "/Customers?$expand=Orders($select=SomeOrderLine)"
    // not supported for now
    testNoSuccess(serviceRoot + includedExpandUri)

  }

  test("complex expands") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    // looks illogical but we have only Customer model to check this url
    val relativeUri = "/Customers?$expand=ODataDemo.Customer/Orders"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            ExpandOption(List(
              PathExpandItem(Some("ODataDemo.Customer"),
                NavigationPropertyExpandPathSegment("Orders", None), List())))))))
  }

  test("$skip check") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?$skip=3"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            SkipOption(3)))))

  }

  test("alias + implicit value") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://hello/odata.svc"
    val relativeUri = "/Customers?@name=$it"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", None),
          List(
            AliasAndValueOption("name",
              ImplicitVariableExpr(None))))))
  }

  test("keyValue for context") {
    implicit val p = parser.keyValuePair("ODataDemo.Customer")

    testSuccess("id=1", ("id", NumberLiteral(1)))
  }

  // apply option test
  test("apply option") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://localhost:8080/odata.svc"
    val relativeUri = "/Products?$apply=groupby((id, name), aggregate($count as ProductCount))"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot, ResourcePathUri(
        EntitySetPath("Products", None),
        List(ApplyOption(
          ApplyExpr("groupby",
            ApplyMethodCallExpr(ApplyPropertyExpr(
              List(EntityPathExpr(None, Some(PropertyPathExpr("id", None))),
                EntityPathExpr(None, Some(PropertyPathExpr("name", None))))),
              ApplyFunctionExpr("aggregate", "$count as ProductCount"))))))))
  }

  // count option test
  test("count option") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://localhost:8080/odata.svc"
    val relativeUri = "/Products?$count=true"

    testSuccess(serviceRoot + relativeUri,
      ODataUri(serviceRoot, ResourcePathUri(
        EntitySetPath("Products", None),
        List(CountOption(true)))))
  }

}
