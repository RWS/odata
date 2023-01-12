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
package com.sdl.odata.parser

import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory
import org.scalatest.FunSuite
import com.sdl.odata.test.model._

trait ParserTestHelpers {
  this: FunSuite =>

  val parser: ODataUriParser = {
    val factory = new AnnotationEntityDataModelFactory()

    factory.addClass(classOf[Address])
    factory.addClass(classOf[Category])
    factory.addClass(classOf[CollectionsSample])
    factory.addClass(classOf[Customer])
    factory.addClass(classOf[VIPCustomer])
    factory.addClass(classOf[EnumSample])
    factory.addClass(classOf[ExampleFlags])
    factory.addClass(classOf[IdNamePairSample])
    factory.addClass(classOf[Order])
    factory.addClass(classOf[OrderLine])
    factory.addClass(classOf[PrimitiveTypesSample])
    factory.addClass(classOf[Product])
    factory.addClass(classOf[SingletonSample])
    factory.addClass(classOf[ComplexKeySample])
    factory.addClass(classOf[NamedSingleton])
    factory.addClass(classOf[FunctionSample])
    factory.addClass(classOf[UnboundFunctionSample])
    factory.addClass(classOf[FunctionImportSample])
    factory.addClass(classOf[ActionSample])
    factory.addClass(classOf[UnboundActionSample])
    factory.addClass(classOf[ActionImportSample])

    new ODataUriParser(factory.buildEntityDataModel())
  }

  def testSuccess[T](input: String, expected: T)(implicit p: parser.Parser[_]) {
    parser.parseAll(p, input) match {
      case result: parser.Success[_] => assert(result.get == expected)
      case result: parser.NoSuccess  => fail(result.toString)
    }
  }

  def testNoSuccess(input: String, message: String = "")(implicit p: parser.Parser[_]) {
    parser.parseAll(p, input) match {
      case result: parser.Success[_] => fail(if (!message.isEmpty) message else s"Expected parse error for input: $input")
      case result: parser.NoSuccess  => // Expected result
    }
  }
}
