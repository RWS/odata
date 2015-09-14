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
package com.sdl.odata.api.parser

import com.sdl.odata.api.service.MediaType

trait QueryOptions {
  def options: List[QueryOption]
}

sealed trait QueryOption

sealed trait SystemQueryOption extends QueryOption


case class ExpandOption(items: List[ExpandItem]) extends SystemQueryOption

sealed trait ExpandItem

// Expand item '*'; the only possible option is '$levels'
case class AllExpandItem(options: List[QueryOption]) extends ExpandItem with ExpandItemOptions

// Expand item '*/$ref'
case object AllRefExpandItem extends ExpandItem

// Expand item with an expand path and options (path does not end with '/$ref' or '/$count')
case class PathExpandItem(derivedTypeName: Option[String], path: ExpandPathSegment, options: List[QueryOption]) extends ExpandItem with ExpandPath with ExpandItemOptions

// Expand item with an expand path ending with '/$ref' and options
case class PathRefExpandItem(derivedTypeName: Option[String], path: ExpandPathSegment, options: List[QueryOption]) extends ExpandItem with ExpandPath with ExpandItemOptions

// Expand item with an expand path ending with '/$count' and options
case class PathCountExpandItem(derivedTypeName: Option[String], path: ExpandPathSegment, options: List[QueryOption]) extends ExpandItem with ExpandPath with ExpandItemOptions

// Mixin trait for expand items that have options
trait ExpandItemOptions {
  def options: List[QueryOption]
}

// Mixin trait for expand items that have an expand path
trait ExpandPath {
  def derivedTypeName: Option[String]
  def path: ExpandPathSegment
}

// Expand path segment
sealed trait ExpandPathSegment {
  def propertyName: String
  def derivedTypeName: Option[String]
}

// Expand path segment that refers to a property of a complex type
case class ComplexPropertyExpandPathSegment(propertyName: String, derivedTypeName: Option[String], subPath: ExpandPathSegment) extends ExpandPathSegment

// Expand path segment that refers to a navigation property
case class NavigationPropertyExpandPathSegment(propertyName: String, derivedTypeName: Option[String]) extends ExpandPathSegment


case class LevelsQueryOption(value: Int) extends SystemQueryOption

case class FilterOption(expression: BooleanExpr) extends SystemQueryOption

case class FormatOption(mediaType: MediaType) extends SystemQueryOption

case class IdOption(value: String) extends SystemQueryOption

case class CountOption(value: Boolean) extends SystemQueryOption

case class OrderByOption(items: List[OrderByItem]) extends SystemQueryOption

sealed trait OrderByItem
case class AscendingOrderByItem(expression: Expression) extends OrderByItem
case class DescendingOrderByItem(expression: Expression) extends OrderByItem

case class SearchOption(expression: SearchExpression) extends SystemQueryOption

sealed trait SearchExpression

case class AndSearchExpression(left: SearchExpression, right: SearchExpression) extends SearchExpression
case class OrSearchExpression(left: SearchExpression, right: SearchExpression) extends SearchExpression

sealed trait SearchTerm extends SearchExpression
case class NormalSearchTerm(value: String) extends SearchTerm
case class NegatedSearchTerm(value: String) extends SearchTerm


case class SelectOption(items: List[SelectItem]) extends SystemQueryOption

sealed trait SelectItem

// Select item '*'
case object AllSelectItem extends SelectItem

// Select item 'namespace.*'
case class SchemaAllSelectItem(namespace: String) extends SelectItem

// Select item with a select path
case class PathSelectItem(derivedTypeName: Option[String], path: SelectPathSegment) extends SelectItem with SelectPath

// Select item with a qualified action name
case class ActionSelectItem(derivedTypeName: Option[String], actionName: String) extends SelectItem

// Select item with a qualified function name and function parameter names
case class FunctionSelectItem(derivedTypeName: Option[String], functionName: String, paramNames: List[String]) extends SelectItem

// Mixin trait for select items that have a select path
trait SelectPath {
  def derivedTypeName: Option[String]
  def path: SelectPathSegment
}

// Select path segment
sealed trait SelectPathSegment {
  def propertyName: String
}

// Select path segment that refers to a property of a complex type
case class ComplexPropertySelectPathSegment(propertyName: String, derivedTypeName: Option[String], subPath: Option[SelectPathSegment]) extends SelectPathSegment

// Select path segment at the end of the path that refers to a primitive or navigation property
case class TerminalPropertySelectPathSegment(propertyName: String) extends SelectPathSegment


case class SkipOption(value: Int) extends SystemQueryOption

case class SkipTokenOption(token: String) extends SystemQueryOption

case class TopOption(value: Int) extends SystemQueryOption

case class AliasAndValueOption(alias: String, value: Expression) extends QueryOption

case class CustomOption(name: String, value: Option[String]) extends QueryOption
