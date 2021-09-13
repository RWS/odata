/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import org.scalatest.FunSuite
import com.sdl.odata.api.parser._
import com.sdl.odata.api.service.MediaType

class ODataUriParsersTest extends FunSuite with ParserTestHelpers {

  test("odataUri => ServiceRootUri") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://localhost:8080/odata.svc"
    testSuccess(serviceRoot, ODataUri(serviceRoot, ServiceRootUri(None)))
    testSuccess(serviceRoot + "/", ODataUri(serviceRoot, ServiceRootUri(None)))
    testSuccess(serviceRoot + "?$format=json", ODataUri(serviceRoot, ServiceRootUri(Some(MediaType.JSON))))
    testSuccess(serviceRoot + "/?$format=xml", ODataUri(serviceRoot, ServiceRootUri(Some(MediaType.XML))))
  }

  test("odataUri => BatchUri") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://somewhere.com/xyz.svc"
    testSuccess(serviceRoot + "/$batch", ODataUri(serviceRoot, BatchUri))
  }


  test("batchUri") {
    implicit val p = parser.batchUri

    testSuccess("$batch", BatchUri)
    testNoSuccess("$BATCH", "'$batch' should be case-sensitive")
  }

  test("odataUri => EntityUri") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://somewhere.com/xyz.svc"
    testSuccess(serviceRoot + "/$entity?$id=test", ODataUri(serviceRoot, EntityUri(None, List(IdOption("test")))))
    testSuccess(serviceRoot + "/$entity/ODataDemo.Customer?$id=test",
      ODataUri(serviceRoot, EntityUri(Some("ODataDemo.Customer"), List(IdOption("test")))))

    testNoSuccess(serviceRoot + "/$entity/ODataDemo.NonExistent?$id=test",
      "Parsing should fail when name is not a valid entity type name")
  }

  test("odataUri => ActionImport uri") {
    implicit val p = parser.odataUri
    testSuccess("http://some.com/xyz.svc/ODataDemoActionImport", ODataUri("http://some.com/xyz.svc",
      ResourcePathUri(ActionImportCall("ODataDemoActionImport"),List())))
  }

  test("odataUri => Bound Action uri") {
    implicit val p = parser.odataUri
    testSuccess("http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction", ODataUri("http://some.com/xyz.svc",
      ResourcePathUri(EntitySetPath("Customers",
        Some(EntityCollectionPath(None,
          Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(2)),
            Some(EntityPath(None,
              Some(BoundActionCallPath("ODataDemo.ODataDemoAction"))))))))),List())))
  }

  test("odataUri => MetadataUri") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://somewhere.com/xyz.svc"
    testSuccess(serviceRoot + "$metadata", ODataUri(serviceRoot, MetadataUri(None, None)))
    testSuccess(serviceRoot + "/$metadata", ODataUri(serviceRoot, MetadataUri(None, None)))
    testSuccess(serviceRoot + "$metadata/", ODataUri(serviceRoot, MetadataUri(None, None)))
    testSuccess(serviceRoot + "/$metadata?$format=xml", ODataUri(serviceRoot, MetadataUri(Some(MediaType.XML), None)))
  }

  test("odataUri => ResourcePathUri") {
    implicit val p = parser.odataUri

    val serviceRoot = "http://somewhere.com/xyz.svc"
    testSuccess(serviceRoot + "/Customers",
      ODataUri(serviceRoot, ResourcePathUri(EntitySetPath("Customers", None), List())))

    testSuccess(serviceRoot + "/Customers('xyz')?$format=json",
      ODataUri(serviceRoot,
        ResourcePathUri(
          EntitySetPath("Customers", Some(
            EntityCollectionPath(None, Some(
              KeyPredicatePath(SimpleKeyPredicate(StringLiteral("xyz")), None))))),
          List(FormatOption(MediaType.JSON)))))
  }
}
