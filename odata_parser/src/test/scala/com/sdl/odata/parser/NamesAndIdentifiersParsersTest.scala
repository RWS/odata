/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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

class NamesAndIdentifiersParsersTest extends FunSuite with ParserTestHelpers {

  test("primitive types name") {
    implicit val p = parser.primitiveTypeName

    testSuccess("Edm.Binary", "Edm.Binary")
    testSuccess("Edm.Boolean", "Edm.Boolean")

    testNoSuccess("Edm.NoEntityExist")
  }

  test("concreteSpatialTypeName") {
    implicit val p = parser.concreteSpatialTypeName

    testSuccess("MultiPolygon", "MultiPolygon")
    testSuccess("Point", "Point")
    testSuccess("Collection", "Collection")
    testNoSuccess("AnotherEntity")

  }

  test("singleQualifiedTypeName"){
    implicit val p = parser.singleQualifiedTypeName
    testSuccess("ODataDemo.Customer", "ODataDemo.Customer")
  }

}
