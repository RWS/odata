/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import scala.util.parsing.combinator.RegexParsers

trait NamesAndIdentifiersParser extends RegexParsers {
  this: EntityDataModelHelpers =>

  def singleQualifiedTypeName: Parser[String] = (qualifiedEntityTypeName | qualifiedComplexTypeName |
    qualifiedTypeDefinitionName | qualifiedEnumTypeName |
    primitiveTypeName) withFailureMessage "Expected a fully-qualified type name"

  def qualifiedTypeName: Parser[String] = singleQualifiedTypeName |
    "Collection(" ~> singleQualifiedTypeName <~ ")" ^^ { case name => s"Collection($name)" }

  def qualifiedEntityTypeName: Parser[String] = qualifiedName.filter(isEntityType)
    .withFailureMessage("Expected a fully-qualified entity type name")

  def qualifiedComplexTypeName: Parser[String] = qualifiedName.filter(isComplexType)
    .withFailureMessage("Expected a fully-qualified complex type name")

  def qualifiedTypeDefinitionName: Parser[String] = qualifiedName.filter(isTypeDefinition)
    .withFailureMessage("Expected a fully-qualified type definition type name")

  def qualifiedEnumTypeName: Parser[String] = qualifiedName.filter(isEnumType)
    .withFailureMessage("Expected a fully-qualified enum type name")

  // NOTE: Differs slightly from ABNF rules; this includes the "." at the end
  def namespace: Parser[String] = """([\p{L}\p{Nl}_][\p{L}\p{Nl}\p{Nd}\p{Mn}\p{Mc}\p{Pc}\p{Cf}]*\.)+""".r

  def qualifiedName: Parser[String] = namespace ~ odataIdentifier ^^ { case ns ~ name => ns + name }

  def entitySetName: Parser[String] = odataIdentifier.filter(isEntitySet)
    .withFailureMessage("Expected an entity set name")

  def singletonEntity: Parser[String] = odataIdentifier.filter(isSingleton)
    .withFailureMessage("Expected a singleton entity name")

  // Definition from TSimpleIdentifier in edm.xsd
  def odataIdentifier: Parser[String] = """[\p{L}\p{Nl}_][\p{L}\p{Nl}\p{Nd}\p{Mn}\p{Mc}\p{Pc}\p{Cf}]*""".r

  def primitiveTypeName: Parser[String] = "Edm." ~ ("Binary" | "Boolean" | "Byte" | "Date" | "DateTimeOffset" |
    "Decimal" | "Double" | "Duration" | "Guid" | "Int16" | "Int32" | "Int64" | "SByte" | "Single" | "Stream" |
    "String" | "TimeOfDay" | spatialTypeName) ^^ {
    case ns ~ name => ns + name
  } withFailureMessage "Expected a fully-qualified primitive type name"

  def spatialTypeName: Parser[String] = abstractSpatialTypeName ~ opt(concreteSpatialTypeName) ^^ {
    case abstractName ~ Some(concreteName) => abstractName + concreteName
    case abstractName ~ None => abstractName
  }

  def abstractSpatialTypeName: Parser[String] = "Geography" | "Geometry"

  def concreteSpatialTypeName: Parser[String] = "Collection" | "LineString" | "MultiLineString" | "MultiPoint" |
    "MultiPolygon" | "Point" | "Polygon"

  def primitiveProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isPrimitiveSinglePropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a primitive single property in the type $contextTypeName")

  def primitiveKeyProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isPrimitiveKeyPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a primitive key property in the type $contextTypeName")

  def primitiveColProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isPrimitiveCollectionPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a primitive collection property in the type $contextTypeName")

  def complexProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isComplexSinglePropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a complex single property in the type $contextTypeName")

  def complexColProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isComplexCollectionPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a complex collection property in the type $contextTypeName")

  def streamProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isStreamPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a stream property in the type $contextTypeName")

  def navigationProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isEntityNavigationPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a navigation property that refers to (a collection) of entity(ies) in the type $contextTypeName")

  def entityNavigationProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isEntitySingleNavigationPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a navigation property that refers to a single entity in the type $contextTypeName")

  def entityColNavigationProperty(contextTypeName: String): Parser[String] = odataIdentifier
    .filter(isEntityCollectionNavigationPropertyOf(contextTypeName))
    .withFailureMessage(s"Expected the name of a navigation property that refers to a collection of entities in the type $contextTypeName")

  def action: Parser[String] = odataIdentifier
    .filter(isAction)
    .withFailureMessage(s"Expected the name of an action")

  def actionImport: Parser[String] = odataIdentifier
    .filter(isActionImport)
    .withFailureMessage(s"Expected the name of an action import")

  def function: Parser[String] = odataIdentifier.filter(isFunction)
    .withFailureMessage("Expected a function name")

  def functionImport: Parser[String] = odataIdentifier.filter(isFunctionImport)
    .withFailureMessage("Expected a function import name")

}
