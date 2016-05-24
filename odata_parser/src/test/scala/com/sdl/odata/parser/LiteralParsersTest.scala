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

import java.util.UUID

import com.sdl.odata.api.edm.model.EnumType
import com.sdl.odata.api.parser._
import java.time.{Period, ZonedDateTime, LocalDate, LocalTime}
import org.scalatest.FunSuite

class LiteralParsersTest extends FunSuite with ParserTestHelpers {

  test("primitiveLiteral") {
    // Note: Test case not implemented yet
  }

  test("nullLiteral") {
    implicit val p = parser.nullLiteral

    testSuccess("null", NullLiteral)

    // NOTE: According to the specs, 'null' is case-sensitive and must be lower-case, but other types such as
    // boolean values are case-insensitive. This looks inconsistent, is it an error in the specs?
    testNoSuccess("NULL", "'null' is case-sensitive, 'NULL' should not be accepted")
  }

  test("booleanLiteral") {
    implicit val p = parser.booleanLiteral

    testSuccess("true", TrueLiteral)
    testSuccess("false", FalseLiteral)
    testSuccess("TRUE", TrueLiteral)
    testSuccess("FALSE", FalseLiteral)

    testNoSuccess("null")
  }

  test("numberLiteral") {
    implicit val p = parser.numberLiteral

    testSuccess("3", NumberLiteral(3))
    testSuccess("3.5", NumberLiteral(3.5))
    testNoSuccess("'3.5'")
  }

  test("stringLiteral") {
    implicit val p = parser.stringLiteral
    testSuccess("'test''s'", StringLiteral("test's"))
    testSuccess("'''tests'", StringLiteral("'tests"))
    testSuccess("'tests'''", StringLiteral("tests'"))
    testNoSuccess("'test's'")
  }

  test("enumLiteral") {
    implicit val p = parser.enumLiteral

    val value1 = parser.entityDataModel.getType("ODataSample.EnumSample").asInstanceOf[EnumType].getMember("VALUE1")
    val value2 = parser.entityDataModel.getType("ODataSample.EnumSample").asInstanceOf[EnumType].getMember("VALUE2")

    testSuccess("ODataSample.EnumSample'VALUE1'", EnumLiteral("ODataSample.EnumSample", List(value1)))
    testSuccess("ODataSample.EnumSample'VALUE1,VALUE2'", EnumLiteral("ODataSample.EnumSample", List(value1, value2)))
    testNoSuccess("ODataSample.EnumSample'VALUE1,VALUE2,VALUE3'") // VALUE3 is not member of EnumSample
  }

  test("dateOrTimeLiteral") {
    implicit val p = parser.dateOrTimeLiteral
    val dateTimeLiteral = "2014-12-31T23:00-10:00"
    val localDateLiteral = "2014-07-04"
    val localTimeLiteral = "12:12:12.123456789"
    val periodLiteral = "duration'P3Y30M30D'"
    val stringLiteral = "'test'"
    val numberLiteral = "3"

    // dateOrTimeLiteral function only handles dateTimeLiteral | localDateLiteral | localTimeLiteral | periodLiteral
    testSuccess(dateTimeLiteral, DateTimeLiteral(ZonedDateTime.parse(dateTimeLiteral)))
    testSuccess(localDateLiteral, LocalDateLiteral(LocalDate.parse(localDateLiteral)))
    testSuccess(localTimeLiteral, LocalTimeLiteral(LocalTime.parse(localTimeLiteral)))
    testSuccess(periodLiteral, PeriodLiteral(Period.of(3, 30, 30)))

    testNoSuccess(stringLiteral)
    testNoSuccess(numberLiteral)
  }

  test("localDateLiteral") {
    implicit val p = parser.localDateLiteral
    val date = "9999-12-31"
    testSuccess(date, LocalDateLiteral(LocalDate.parse(date)))
    testNoSuccess("9999-99-99")
    testNoSuccess("12345")
    testNoSuccess("1234-12")
    testNoSuccess("1234-12-123")
  }

  test("localTimeLiteral") {
    implicit val p = parser.localTimeLiteral
    testSuccess("12:12", LocalTimeLiteral(LocalTime.parse("12:12")))
    testSuccess("12:12:12", LocalTimeLiteral(LocalTime.parse("12:12:12")))
    testSuccess("12:12:12.8", LocalTimeLiteral(LocalTime.parse("12:12:12.8")))
    testSuccess("12:12:12.123", LocalTimeLiteral(LocalTime.parse("12:12:12.123")))
    testNoSuccess("12:12:12.1234567890")
    testNoSuccess("99:99")
  }

  test("dateTimeLiteral") {
    implicit val p = parser.dateTimeLiteral

    testSuccess("1234-12-31T12:12Z", DateTimeLiteral(ZonedDateTime.parse("1234-12-31T12:12Z")))

  }

  test("periodLiteral") {
    implicit val p = parser.periodLiteral
    testSuccess("duration'P3Y30M30D'", PeriodLiteral(Period.of(3, 30, 30)))
    testNoSuccess("'P1DT10M50.700S'")
  }

  test("guidLiteral") {
    implicit val p = parser.guidLiteral

    testSuccess("38400000-8cf0-11bd-b23e-10b96e4ef00d", GuidLiteral(UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")))
    testSuccess("12345678-1324-1234-1234-012345678912", GuidLiteral(UUID.fromString("12345678-1324-1234-1234-012345678912")))
    testNoSuccess("wrong-input-to-guid-literal-should-fail")
  }


  test("longParameterForStringLiteral") {
    implicit val p = parser.stringValue
    // this uri was causing StackOverflowError through the stringValue function
    val bigParameter = scala.io.Source.fromInputStream(
      getClass.getClassLoader.getResourceAsStream("BigParameterSample1.txt")).mkString
    testSuccess(bigParameter, bigParameter.replace("'", ""))
  }

  test("binaryLiteral") {
    // Note: Test case not implemented yet
  }

  test("geoLiteral") {
    // Note: Test case not implemented yet
  }

  test("geographyLiteral") {
    // Note: Test case not implemented yet, geography literals are not supported yet by the parser.
  }

  test("geometryLiteral") {
    // Note: Test case not implemented yet, geometry literals are not supported yet by the parser.
  }
}
