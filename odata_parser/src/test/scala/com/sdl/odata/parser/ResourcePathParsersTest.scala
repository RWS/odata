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
package com.sdl.odata.parser

import com.sdl.odata.api.parser._
import org.scalatest.FunSuite

class ResourcePathParsersTest extends FunSuite with ParserTestHelpers {

  test("resourcePath => EntitySetPath") {
    implicit val p = parser.resourcePath

    testSuccess("Customers", EntitySetPath("Customers", None))

    testSuccess("Customers(23)", EntitySetPath("Customers",
      Some(EntityCollectionPath(None, Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(23)), None))))))
  }

  test("resourcePath => SingletonPath") {
    implicit val p = parser.resourcePath

    testSuccess("SingletonSample", SingletonPath("SingletonSample", None))
    testSuccess("SingletonSample/$ref", SingletonPath("SingletonSample", Some(EntityPath(None, Some(RefPath)))))

    testSuccess("SingletonSample/ODataDemo.SingletonSample",
      SingletonPath("SingletonSample", Some(EntityPath(Some("ODataDemo.SingletonSample"), None))))
  }

  test("resourcePath => CrossJoinPath") {
    implicit val p = parser.resourcePath

    testSuccess("$crossjoin(Customers)", CrossJoinPath(List("Customers")))

    testNoSuccess("$crossjoin()", "It should require at least one entity set name")
    testNoSuccess("$crossjoin(InvalidName)", "It should fail if the name is not an entity set name")
  }

  test("resourcePath => AllPath") {
    implicit val p = parser.resourcePath

    testSuccess("$all", AllPath)

    testNoSuccess("$ALL", "'$all' should be case-sensitive")
  }

  test("single navigation path") {
    implicit val p = parser.singleNavPath("ODataDemo.Customer")

    testSuccess("/id", PropertyPath("id", None))
    testSuccess("/Orders(1)", PropertyPath("Orders",
      Some(EntityCollectionPath(None,
        Some(KeyPredicatePath(
          SimpleKeyPredicate(NumberLiteral(1)),None))))))
  }

  test("resourcePath => ActionImport call") {
    implicit val p = parser.resourcePath

    testSuccess("ODataDemoActionImport", ActionImportCall("ODataDemoActionImport"))
  }

  test("resourcePath => bound Action call to entity") {
    implicit val p = parser.resourcePath

    testSuccess("Customers(2)/ODataDemo.ODataDemoAction", EntitySetPath("Customers",
      Some(EntityCollectionPath(None,
        Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(2)),
          Some(EntityPath(None,
            Some(BoundActionCallPath("ODataDemo.ODataDemoAction"))))))))))
  }

  test("resourcePath with no params => FunctionImportCall") {
    implicit val p = parser.resourcePath

    testSuccess("ODataDemoFunctionImport", FunctionImportCall("ODataDemoFunctionImport", None, None))
  }

  test("resourcePath with params => FunctionImportCall(&params)") {
    implicit val p = parser.resourcePath

    testSuccess("ODataDemoFunctionImport(par1=1,par2=5)", FunctionImportCall(
      "ODataDemoFunctionImport",
      Option(Map("par1" -> LiteralFunctionParam(NumberLiteral(1)), "par2" -> LiteralFunctionParam(NumberLiteral(5)))),
      None))
  }

  test("resourcePath with params and $value => FunctionImportCall(&params)/$value") {
    implicit val p = parser.resourcePath

    testSuccess("ODataDemoFunctionImport(par1=1,par2=5)/$value", FunctionImportCall(
      "ODataDemoFunctionImport",
      Option(Map("par1" -> LiteralFunctionParam(NumberLiteral(1)), "par2" -> LiteralFunctionParam(NumberLiteral(5)))),
      Option(EntityPath(None,Some(ValuePath)))
    ))
  }

  test("resourcePath with params and EntityPath => FunctionImportCall(&params)/ODataDemo.Order") {
    implicit val p = parser.resourcePath

    testSuccess("ODataDemoFunctionImport(par1=1,par2=5)/ODataDemo.Order", FunctionImportCall(
      "ODataDemoFunctionImport",
      Option(Map("par1" -> LiteralFunctionParam(NumberLiteral(1)), "par2" -> LiteralFunctionParam(NumberLiteral(5)))),
      Option(EntityPath(Some("ODataDemo.Order"),None))
    ))
  }

  test("resourcePath with no params from Customers => BoundFunctionCallPath") {
    implicit val p = parser.resourcePath

    testSuccess("Customers/ODataDemo.ODataDemoFunction",
      EntitySetPath("Customers",
        Some(EntityCollectionPath(None,
          Some(BoundFunctionCallPath("ODataDemo.ODataDemoFunction",None,None))))))
  }

  test("resourcePath from Customers => BoundActionCallPath") {
    implicit val p = parser.resourcePath

    testSuccess("Customers/ODataDemo.ODataDemoAction",
      EntitySetPath("Customers",
        Some(EntityCollectionPath(None,
          Some(BoundActionCallPath("ODataDemo.ODataDemoAction"))))))
  }

  test("resourcePath with no params => BoundFunctionCallPath") {
    implicit val p = parser.resourcePath

    testSuccess("Customers(2)/ODataDemo.ODataDemoFunction",
      EntitySetPath("Customers", Some(EntityCollectionPath(None,
        Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(2)),
          Some(EntityPath(None,
            Some(BoundFunctionCallPath("ODataDemo.ODataDemoFunction",None,None))))))))))
  }

  test("resourcePath with parentheses => BoundFunctionCallPath()") {
    implicit val p = parser.resourcePath

    testSuccess("Customers(2)/ODataDemo.ODataDemoFunction()",
      EntitySetPath("Customers", Some(EntityCollectionPath(None,
        Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(2)),
          Some(EntityPath(None,
            Some(BoundFunctionCallPath("ODataDemo.ODataDemoFunction",Option(Map()),None))))))))))
  }

  test("resourcePath with parentheses and $value => BoundFunctionCallPath()/$value") {
    implicit val p = parser.resourcePath

    testSuccess("Customers(2)/ODataDemo.ODataDemoFunction()/$value",
      EntitySetPath("Customers",
        Some(EntityCollectionPath(None,
          Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(2)),
            Some(EntityPath(None,
              Some(BoundFunctionCallPath("ODataDemo.ODataDemoFunction",
                Some(Map()),Some(EntityPath(None,
                  Some(ValuePath)))))))))))))
  }
}
