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

import java.time.{LocalDate, LocalTime, Period, ZoneId, ZonedDateTime}
import java.util.UUID

import com.sdl.odata.api.edm.model.EntityDataModel
import com.sdl.odata.api.parser.ODataUriUtil._
import com.sdl.odata.api.parser._
import com.sdl.odata.api.service.MediaType
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory
import com.sdl.odata.test.model._
import org.scalatest.FunSuite

/**
  * This unit test for ODataUriUtil.scala (which is in OData_api module)
  * This test should know about entity data model that is why as of know I am keeping here
  */
class ODataUriUtilTest extends FunSuite with ParserTestHelpers {
  val entityDataModel: EntityDataModel = {
    val factory = new AnnotationEntityDataModelFactory()

    factory.addClass(classOf[Address])
    factory.addClass(classOf[Category])
    factory.addClass(classOf[CollectionsSample])
    factory.addClass(classOf[Customer])
    factory.addClass(classOf[EnumSample])
    factory.addClass(classOf[ExampleFlags])
    factory.addClass(classOf[IdNamePairSample])
    factory.addClass(classOf[Order])
    factory.addClass(classOf[OrderLine])
    factory.addClass(classOf[PrimitiveTypesSample])
    factory.addClass(classOf[SingletonSample])
    factory.addClass(classOf[NamedSingleton])
    factory.addClass(classOf[FunctionSample])
    factory.addClass(classOf[UnboundFunctionSample])
    factory.addClass(classOf[FunctionImportSample])
    factory.addClass(classOf[ActionSample])
    factory.addClass(classOf[UnboundActionSample])
    factory.addClass(classOf[ActionImportSample])

    factory.buildEntityDataModel()
  }

  test("resolveTargetType => entity set") {
    val url: String = "http://localhost:8080/odata.svc/Customers"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) => assert(typeName == "ODataDemo.Customer" && isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => function import") {
    val url: String = "http://localhost:8080/odata.svc/ODataDemoFunctionImport"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) => assert(typeName == "Edm.String" && !isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => function") {
    val url: String = "http://localhost:8080/odata.svc/Customers(2)/ODataDemo.ODataDemoFunction"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) => assert(typeName == "Edm.String" && !isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => action import") {
    val url: String = "http://localhost:8080/odata.svc/ODataDemoActionImport"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) => assert(typeName == "ODataDemo.Customer" && isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => action") {
    val url: String = "http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) => assert(typeName == "ODataDemo.Customer" && isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => key predicate") {
    val url: String = "http://localhost:8080/odata.svc/Customers(1)"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) => assert(typeName == "ODataDemo.Customer" && !isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => key predicate property") {
    val url: String = "http://localhost:8080/odata.svc/Customers(1)/name"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, propertyName)) => assert(typeName == "Edm.String" && !isCollection && propertyName.isDefined && propertyName.get == "name")
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("resolveTargetType => collection of phone numbers in customer") {
    val url: String = "http://localhost:8080/odata.svc/Customers(1)/Phone"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, _)) =>
        assert(typeName == "Edm.String" && isCollection)
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("getEntitySetName") {
    assert(getEntitySetName(parser.parseUri("http://localhost:8080/odata.svc/Customers")) ===
      Some("Customers"))
    assert(getEntitySetName(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/Phone")) ===
      Some("Phone"))
  }

  test("getEntitySetId => entity set") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers")) ===
      Some("http://localhost:8080/odata.svc/Customers"))
  }

  test("getEntitySetId => collection of phones in customer") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/Phone")) ===
      Some("http://localhost:8080/odata.svc/Customers(1)/Phone"))
  }

  test("getEntitySetId => bound function call on entity") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.ODataDemoFunction")) ===
      Some("http://localhost:8080/odata.svc/Customers(1)"))
  }

  test("getEntitySetId => bound function call on entity set") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoFunction")) ===
      Some("http://localhost:8080/odata.svc/Customers"))
  }

  test("getEntitySetId => bound action call on entity") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.ODataDemoAction")) ===
      Some("http://localhost:8080/odata.svc/Customers(1)"))
  }

  test("getEntitySetId => bound action call on entity set") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoAction")) ===
      Some("http://localhost:8080/odata.svc/Customers"))
  }

  test("getEntitySetId => specific customer") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)")) ===
      Some("http://localhost:8080/odata.svc/Customers(1)"))
  }

  test("getEntitySetId => complex key sample") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(id=15,name='ComplexKey',period=duration'P3Y30M30D')")) ===
      Some("http://localhost:8080/odata.svc/ComplexKeySamples(id=15,name='ComplexKey',period=duration'P3Y30M30D')"))
  }

  test("getEntitySetId => customer with text id") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers('test123')?$format=json")) ===
      Some("http://localhost:8080/odata.svc/Customers('test123')"))
  }

  test("getEntitySetId => complex key with text id") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(id='test123',name='ComplexKey',period=duration'P3Y30M30D')?$format=json")) ===
      Some("http://localhost:8080/odata.svc/ComplexKeySamples(id='test123',name='ComplexKey',period=duration'P3Y30M30D')"))
  }

  test("getEntitySetId => singleton sample") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/SingletonSample")) ===
      Some("http://localhost:8080/odata.svc/SingletonSample"))
  }

  test("getEntitySetId => customer - vip customer") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer")) ===
      Some("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer"))
  }

  test("getEntitySetId => customer with id - vip customer") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.VIPCustomer")) ===
      Some("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.VIPCustomer"))
  }

  test("getEntitySetId => customer - vip customer with id") {
    assert(getEntitySetId(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer(1)")) ===
      Some("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer(1)"))
  }

  test("resolveTargetType => collection of address in Customer") {
    val url: String = "http://localhost:8080/odata.svc/Customers(1)/address"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    resolveTargetType(oDataUri, entityDataModel) match {
      case Some(TargetType(typeName, isCollection, propertyName)) => assert(typeName == "ODataDemo.Address" && isCollection && propertyName.get == "address")
      case result => fail(s"Unexpected result: $result")
    }
  }

  test("get format parameter from service root") {
    val url: String = "http://localhost:8080/odata.svc?$format=test/testing"
    val oDataUri: ODataUri = new ODataUriParser(entityDataModel).parseUri(url)
    getFormatOption(oDataUri) match {
      case Some(x) => assert(x === FormatOption(new MediaType("test", "testing")))
      case _ => fail("no format")
    }
  }

  test("context url => service document") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc")) ===
      Some("http://localhost:8080/odata.svc/$metadata"))
  }

  test("context url => entity set") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers"))
  }

  test("context url => entity") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers/$entity"))

    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(id=15,name='ComplexKey',period=duration'P3Y30M30D')")) ===
        Some("http://localhost:8080/odata.svc/$metadata#ComplexKeySamples/$entity"))
  }

  test("context url => entity with literal") {
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers('test123')?$format=json")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers/$entity"))

    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers('test123')/Orders?$format=json")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers('test123')/Orders"))

    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(id=15,name='ComplexKey',period=duration'P3Y30M30D')?$format=json")) ===
        Some("http://localhost:8080/odata.svc/$metadata#ComplexKeySamples/$entity"))
  }

  test("context url => singleton") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/SingletonSample")) === Some("http://localhost:8080/odata.svc/$metadata#SingletonSample"))
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/SingletonSample/id")) === Some("http://localhost:8080/odata.svc/$metadata#SingletonSample/id"))
  }

  test("context url => named singleton") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/SingletonWithName")) === Some("http://localhost:8080/odata.svc/$metadata#SingletonWithName"))
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/SingletonWithName/id")) === Some("http://localhost:8080/odata.svc/$metadata#SingletonWithName/id"))
  }

  test("context url => derived type") {
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers/ODataDemo.VIPCustomer"))
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.VIPCustomer")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers/ODataDemo.VIPCustomer/$entity"))
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer(1)")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers/ODataDemo.VIPCustomer/$entity"))
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.VIPCustomer/vip_id")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers(1)/ODataDemo.VIPCustomer/vip_id"))
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer(1)/vip_id")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers/ODataDemo.VIPCustomer(1)/vip_id"))
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/ODataDemo.VIPCustomer/vip_address/Street")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers(1)/ODataDemo.VIPCustomer/vip_address/Street"))
  }

  test("context url => complex property") {
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(122)/address")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers(122)/address"))

    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers('ALFKI')/address")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers('ALFKI')/address")
    )
  }

  test("context url => collection field of an entity set") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/Orders")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers(1)/Orders"))

    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/Orders(2)/orderLines")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers(1)/Orders(2)/orderLines"))
  }

  test("context url => entity of an entity set") {
    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/Orders(123)")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers(1)/Orders/$entity"))

    assert(getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)/Orders(123)/orderLines(5)")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers(1)/Orders(123)/orderLines/$entity"))
  }

  test("context url => simple property") {
    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/Customers(122)/name")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers(122)/name"))

    assert(
      getContextUrl(parser.parseUri("http://localhost:8080/odata.svc/ComplexKeySamples(id=15,name='ComplexKey',period=duration'P3Y30M30D')/name")) ===
        Some("http://localhost:8080/odata.svc/$metadata#ComplexKeySamples(id=15,name='ComplexKey',period=duration'P3Y30M30D')/name"))
  }

  test("context url => entity set (write operation)") {
    assert(getContextUrlWriteOperation(parser.parseUri("http://localhost:8080/odata.svc/Customers")) ===
      Some("http://localhost:8080/odata.svc/$metadata#Customers/$entity"))
  }

  test("context url => singleton (write operation)") {
    assert(getContextUrlWriteOperation(parser.parseUri("http://localhost:8080/odata.svc/SingletonSample")) ===
      Some("http://localhost:8080/odata.svc/$metadata#SingletonSample/$entity"))
  }

  test("context url => singleton with name(write operation)") {
    assert(getContextUrlWriteOperation(parser.parseUri("http://localhost:8080/odata.svc/SingletonWithName")) ===
      Some("http://localhost:8080/odata.svc/$metadata#SingletonWithName/$entity"))
  }

  test("context url => derived type (write operation)") {
    assert(
      getContextUrlWriteOperation(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.VIPCustomer")) ===
        Some("http://localhost:8080/odata.svc/$metadata#Customers/ODataDemo.VIPCustomer/$entity"))
  }

  test("formatLiteral") {
    assert(formatLiteral(NullLiteral) === "null")
    assert(formatLiteral(TrueLiteral) === "true")
    assert(formatLiteral(FalseLiteral) === "false")
    assert(formatLiteral(NumberLiteral(BigDecimal(2.3))) === "2.3")
    assert(formatLiteral(StringLiteral("test")) === "'test'")
    assert(formatLiteral(LocalDateLiteral(LocalDate.of(2014, 7, 1))) === "2014-07-01")
    assert(formatLiteral(LocalTimeLiteral(LocalTime.of(9, 49, 37, 18))) === "09:49:37.000000018")
    assert(formatLiteral(DateTimeLiteral(ZonedDateTime.of(2012, 2, 29, 13, 5, 48, 31, ZoneId.of("UTC").normalized()))) === "2012-02-29T13:05:48.000000031Z")
    assert(formatLiteral(PeriodLiteral(Period.of(3, 30, 30))) === "duration'P3Y30M30D'")
    assert(formatLiteral(GuidLiteral(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"))) === "38400000-8cf0-11bd-b23e-10b96e4ef00d")
  }

  test("test isFunctionCallUri using function import url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoFunctionImport(par1=1,par2='str1')")
    assert(ODataUriUtil.isFunctionCallUri(odataUri) === true)
  }

  test("test isFunctionCallUri using function url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/Customers(1)/ODataDemo.ODataDemoFunction")
    assert(ODataUriUtil.isFunctionCallUri(odataUri) === true)
  }

  test("test isFunctionCallUri using entity url") {
    val odataUri = parser.parseUri("http://localhost:8080/odata.svc/Customers(1)")
    assert(ODataUriUtil.isFunctionCallUri(odataUri) === false)
  }

  test("test getFunctionCallName using function import url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoFunctionImport")
    assert(ODataUriUtil.getFunctionCallName(odataUri) === None)
  }

  test("test getFunctionCallName using function url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoFunction")
    assert(ODataUriUtil.getFunctionCallName(odataUri) === Some("ODataDemo.ODataDemoFunction"))
  }

  test("test getFunctionCallName using entity url") {
    val odataUri = parser.parseUri("http://localhost:8080/odata.svc/Customers(1)")
    assert(ODataUriUtil.getFunctionCallName(odataUri) === None)
  }

  test("test getFunctionImportCallName using function import url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoFunctionImport")
    assert(ODataUriUtil.getFunctionImportCallName(odataUri) === Some("ODataDemoFunctionImport"))
  }

  test("test getFunctionImportCallName using entity url") {
    val odataUri = parser.parseUri("http://localhost:8080/odata.svc/Customers(1)")
    assert(ODataUriUtil.getFunctionImportCallName(odataUri) === None)
  }

  test("test getFunctionCallParameters using function url with parameters") {
    val odataUri = parser
      .parseUri("http://some.com/xyz.svc/Customers(1)/ODataDemo.ODataDemoFunction(par1=5,par2=10,par3='foo')")
    assert(ODataUriUtil.getFunctionCallParameters(odataUri) === Some(Map("par1" -> "5", "par2" -> "10", "par3" -> "foo")))
  }

  test("getFunctionCallParameters using function import url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoFunctionImport(par1=1,par2='bar')")
    assert(ODataUriUtil.getFunctionImportCallParameters(odataUri) === Some(Map("par1" -> "1", "par2" -> "bar")))
  }

  test("isActionCall test on ActionImport url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoActionImport")
    assert(ODataUriUtil.isActionCallUri(odataUri) === true)
  }

  test("isActionCall test on Action url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction")
    assert(ODataUriUtil.isActionCallUri(odataUri) === true)
  }

  test("isActionCall test on entity url") {
    val odataUri = parser.parseUri("http://localhost:8080/odata.svc/Customers(111)")
    assert(ODataUriUtil.isActionCallUri(odataUri) === false)
  }

  test("getActionCallName test on ActionImport url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoActionImport")
    assert(ODataUriUtil.getActionCallName(odataUri) === None)
  }

  test("getActionCallName test on Action url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction")
    assert(ODataUriUtil.getActionCallName(odataUri) === Some("ODataDemo.ODataDemoAction"))
  }

  test("getActionCallName test on entity url") {
    val odataUri = parser.parseUri("http://localhost:8080/odata.svc/Customers(111)")
    assert(ODataUriUtil.getActionCallName(odataUri) === None)
  }

  test("getActionImportCallName test on ActionImport url") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/ODataDemoActionImport")
    assert(ODataUriUtil.getActionImportCallName(odataUri) === Some("ODataDemoActionImport"))
  }

  test("getActionImportCallName test on entity url") {
    val odataUri = parser.parseUri("http://localhost:8080/odata.svc/Customers(111)")
    assert(ODataUriUtil.getActionImportCallName(odataUri) === None)
  }

  test("getBoundEntityName test on action call") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/Customers(2)/ODataDemo.ODataDemoAction")
    assert(ODataUriUtil.getBoundEntityName(odataUri) === Some("Customers"))
  }

  test("getBoundEntityName test on singleton call") {
    val odataUri = parser.parseUri("http://some.com/xyz.svc/SingletonSample/ODataDemo.ODataDemoAction")
    assert(ODataUriUtil.getBoundEntityName(odataUri) === Some("SingletonSample"))
  }

  test("extract entity key => for entity set") {
    val customer = new Customer().setId(1)

    assert(extractEntityWithKeys(parser.parseUri("http://localhost:8080/odata.svc/Customers(1)"), entityDataModel) ===
      Some(customer))
  }

  test("extract entity key => for singletons") {
    val singletonSample = new SingletonSample()

    assert(extractEntityWithKeys(parser.parseUri("http://localhost:8080/odata.svc/SingletonSample"), entityDataModel) ===
      Some(singletonSample))
  }

  test("extract entity key => for named singletons") {
    val singletonWithName = new NamedSingleton()

    assert(extractEntityWithKeys(parser.parseUri("http://localhost:8080/odata.svc/SingletonWithName"), entityDataModel) ===
      Some(singletonWithName))
  }

  test("getOperationReturnType -> bound action call") {
    assert(getOperationReturnType(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoAction"), entityDataModel) ===
      "Customers")
  }

  test("getOperationReturnType -> bound function call") {
    assert(getOperationReturnType(parser.parseUri("http://localhost:8080/odata.svc/Customers/ODataDemo.ODataDemoFunction"), entityDataModel) ===
      "Edm.String")
  }

  test("getOperationReturnType -> action import call") {
    assert(getOperationReturnType(parser.parseUri("http://localhost:8080/odata.svc/ODataDemoActionImport"), entityDataModel) ===
      "Customers")
  }

  test("getOperationReturnType -> function import call") {
    assert(getOperationReturnType(parser.parseUri("http://localhost:8080/odata.svc/ODataDemoFunctionImport"), entityDataModel) ===
      "Edm.String")
  }

  test("host containing '.svc' part") {
    val host = "http://localhost:8080/odata.svc"
    val uri = s"$host/Customers(1)"
    assert(parser.parseUri(uri) ===
      ODataUri(host, ResourcePathUri(EntitySetPath("Customers", Some(EntityCollectionPath(None, Some(KeyPredicatePath(SimpleKeyPredicate(NumberLiteral(1)), None))))), List()))
    )
  }
}
