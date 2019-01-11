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

import scala.util.Try
import scala.util.parsing.combinator.RegexParsers
import com.sdl.odata.api.parser._
import com.sdl.odata.api.edm.model.{EnumType, MetaType, EnumMember}
import java.time.{Period, ZonedDateTime, LocalTime, LocalDate}
import java.util.UUID

trait LiteralsParser extends RegexParsers {
  this: NamesAndIdentifiersParser with EntityDataModelHelpers =>

  // NOTE: This does not correspond exactly to the OData ABNF specification;
  // it's a simplified version which works for all practical purposes.

  def primitiveLiteral: Parser[Literal] = nullLiteral | booleanLiteral | guidLiteral | dateOrTimeLiteral |
    stringLiteral | enumLiteral | binaryLiteral | numberLiteral | geoLiteral

  def nullLiteral: Parser[NullLiteral.type] = "null" ^^^ NullLiteral

  def booleanLiteral: Parser[BooleanLiteral] = booleanValue ^^ {
    case value => if (value) TrueLiteral else FalseLiteral
  }

  def booleanValue: Parser[Boolean] = "(?i)true".r ^^^ true | "(?i)false".r ^^^ false

  def numberLiteral: Parser[NumberLiteral] = numberValue ^^ NumberLiteral

  def numberValue: Parser[BigDecimal] = """(\+|-)?\d+(\.\d+)?(e(\+|-)?\d+)?""".r ^^ BigDecimal.apply

  def stringLiteral: Parser[StringLiteral] = stringValue ^^ StringLiteral

  def stringValue: Parser[String] = "'" ~> """[^']*(?:''[^']*)*""".r <~ "'" ^^ { case s => s.replaceAll("''", "'")}

  def enumLiteral: Parser[EnumLiteral] = qualifiedEnumTypeName into {
    enumTypeName =>
      "'" ~> rep1sep(enumMemberValue(enumTypeName), ",") <~ "'" ^^ {
        case values => EnumLiteral(enumTypeName, values)
      }
  }

  def enumMemberValue(enumTypeName: String): Parser[EnumMember] =
    namedEnumMemberValue(enumTypeName) | integerEnumMemberValue(enumTypeName)

  def namedEnumMemberValue(enumTypeName: String): Parser[EnumMember] = {
    val enumType = entityDataModel.getType(enumTypeName)
    if (enumType == null || enumType.getMetaType() != MetaType.ENUM)
      failure(s"Enum type not found: $enumTypeName")
    else {
      val result = odataIdentifier map { memberName => Option(enumType.asInstanceOf[EnumType].getMember(memberName)) }
      result filter (_.isDefined) map (_.get) withFailureMessage s"Invalid enum member name for enum: $enumTypeName"
    }
  }

  def integerEnumMemberValue(enumTypeName: String): Parser[EnumMember] = {
    val enumType = entityDataModel.getType(enumTypeName)
    if (enumType == null || enumType.getMetaType() != MetaType.ENUM)
      failure(s"Enum type not found: $enumTypeName")
    else {
      val result = """\d+""".r map { memberValue => Option(enumType.asInstanceOf[EnumType].getMember(memberValue.toLong)) }
      result filter (_.isDefined) map (_.get) withFailureMessage s"Invalid enum member value for enum: $enumTypeName"
    }
  }

  // NOTE: dateTimeOffsetValue must be first, because dateValue matches the first part of dateTimeOffsetValue
  def dateOrTimeLiteral: Parser[Literal] = dateTimeLiteral | localDateLiteral | localTimeLiteral | periodLiteral

  def localDateLiteral: Parser[LocalDateLiteral] = localDateValue ^^ LocalDateLiteral

  def localDateValue: Parser[LocalDate] = """\d{4}-\d{2}-\d{2}""".r into tryParse { case s => LocalDate.parse(s) }

  def localTimeLiteral: Parser[LocalTimeLiteral] = localTimeValue ^^ LocalTimeLiteral

  def localTimeValue: Parser[LocalTime] = """\d{2}:\d{2}(:\d{2}(\.\d{1,9})?)?""".r into tryParse { case s => LocalTime.parse(s) }

  def dateTimeLiteral: Parser[DateTimeLiteral] = dateTimeValue ^^ DateTimeLiteral

  def dateTimeValue: Parser[ZonedDateTime] =
    """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2}(\.d{1,9})?)?(Z|((\+|-)\d{2}:\d{2}))""".r into tryParse { case s => ZonedDateTime.parse(s) }

  def periodLiteral: Parser[PeriodLiteral] = "(?i)duration'".r ~> periodValue <~ "'".r ^^ PeriodLiteral

  def periodValue: Parser[Period] =
    """([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?""".r into tryParse { case s => Period.parse(s) }

  def guidLiteral: Parser[GuidLiteral] = guidValue ^^ GuidLiteral

  def guidValue: Parser[UUID] =
    """[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}""".r ^^ UUID.fromString

  def binaryLiteral: Parser[BinaryLiteral] = "(?i)binary'" ~> binaryValue <~ "'".r ^^ BinaryLiteral

  // Note: Base64 decoding is not supported yet
  def binaryValue: Parser[Array[Byte]] = """[A-Za-z0-9\-_=]*""".r ^^ { case s => new Array(0) }

  def geoLiteral: Parser[GeoLiteral.type] = geographyLiteral | geometryLiteral

  // Note: Geography literals are not supported yet by the parser
  def geographyLiteral: Parser[GeoLiteral.type] = "(?i)geography'[^']*'".r ^^^ GeoLiteral

  // Note: Geometry literals are not supported yet by the parser
  def geometryLiteral: Parser[GeoLiteral.type] = "(?i)geometry'[^']*'".r ^^^ GeoLiteral


  def tryParse[T, U](block: T => U)(value: T): Parser[U] = Try(block(value)) match {
    case scala.util.Success(result) => success(result)
    case scala.util.Failure(error) => failure(error.getMessage)
  }
}
