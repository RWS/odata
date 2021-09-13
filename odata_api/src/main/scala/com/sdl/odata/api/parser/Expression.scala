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
package com.sdl.odata.api.parser

// commonExpr
sealed trait Expression

// commonExpr = primitiveLiteral
case class LiteralExpr(value: Literal) extends Expression

// commonExpr = parameterAlias
case class ParameterAliasExpr(alias: String) extends Expression

// commonExpr = arrayOrObject
case class JsonDataExpr() extends Expression

// functionExpr
case class FunctionCallExpr(functionName: String, args: Map[String, FunctionExprParam], subPath: Option[PathExpr]) extends Expression with SubPathExpr

sealed trait FunctionExprParam
case class AliasFunctionExprParam(alias: String) extends FunctionExprParam
case class ExpressionFunctionExprParam(expression: Expression) extends FunctionExprParam

// negateExpr
case class NegateExpr(expression: Expression) extends Expression

// methodCallExpr
case class MethodCallExpr(methodName: String, args: List[Expression]) extends Expression

// applyPropertyExpr
case class ApplyPropertyExpr(args: List[Expression]) extends Expression

// applyfunctionExpr
case class ApplyFunctionExpr(methodName: String, args: String) extends Expression

// methodCallExpr
case class ApplyMethodCallExpr(properties: ApplyPropertyExpr, function: ApplyFunctionExpr) extends Expression

// applyExpr
case class ApplyExpr(methodName: String, args: ApplyMethodCallExpr) extends Expression

// castExpr
case class CastExpr(expression: Option[Expression], typeName: String) extends Expression

// addExpr, subExpr, mulExpr, divExpr, modExpr
sealed trait ArithmeticExpr extends Expression {
  def left: Expression
  def right: Expression
}

case class AddExpr(left: Expression, right: Expression) extends ArithmeticExpr
case class SubExpr(left: Expression, right: Expression) extends ArithmeticExpr
case class MulExpr(left: Expression, right: Expression) extends ArithmeticExpr
case class DivExpr(left: Expression, right: Expression) extends ArithmeticExpr
case class ModExpr(left: Expression, right: Expression) extends ArithmeticExpr


// Path expression - allows navigation into (collections of) entities, complex objects and primitive values
sealed trait PathExpr extends Expression

trait SubPathExpr {
  def subPath: Option[PathExpr]
}

// See OData Version 4.0 Part 2: URL Conventions paragraph 5.1.1.6.5
sealed trait RootExpr extends PathExpr
case class EntitySetRootExpr(entitySetName: String, keyPredicate: KeyPredicate, subPath: Option[EntityPathExpr]) extends RootExpr with SubPathExpr
case class SingletonRootExpr(singletonName: String, subPath: Option[EntityPathExpr]) extends RootExpr with SubPathExpr

// '$it', which refers to an element of the collection of entities identified by the resource path
// See OData Version 4.0 Part 2: URL Conventions paragraph 5.1.1.6.4
case class ImplicitVariableExpr(subPath: Option[EntityPathExpr]) extends PathExpr with SubPathExpr

case class LambdaVariableExpr(variableName: String, subPath: Option[EntityPathExpr]) extends PathExpr with SubPathExpr

// collectionNavigationExpr
// nested path can be: KeyPredicatePathExpr, CountPathExpr, BoundFunctionCallPathExpr, AnyPathExpr, AllPathExpr
case class EntityCollectionPathExpr(derivedTypeName: Option[String], subPath: Option[PathExpr]) extends PathExpr with SubPathExpr

// keyPredicate
case class KeyPredicatePathExpr(keyPredicate: KeyPredicate, subPath: Option[EntityPathExpr]) extends PathExpr with SubPathExpr

// singleNavigationExpr
// nested path can be: PropertyPathExpr, BoundFunctionCallPathExpr
case class EntityPathExpr(derivedTypeName: Option[String], subPath: Option[PathExpr]) extends PathExpr with SubPathExpr

// collectionPathExpr = count / boundFunctionExpr / anyExpr / allExpr
// covered by: CountPathExpr, BoundFunctionCallPathExpr, AnyPathExpr, AllPathExpr

// complexPathExpr
// nested path can be: PropertyPathExpr, BoundFunctionCallPathExpr
case class ComplexPathExpr(derivedTypeName: Option[String], subPath: Option[PathExpr]) extends PathExpr with SubPathExpr

// singlePathExpr = boundFunctionExpr
// covered by: BoundFunctionCallPathExpr

// propertyPathExpr
// restriction on type of nested path depends on property type
case class PropertyPathExpr(propertyName: String, subPath: Option[PathExpr]) extends PathExpr with SubPathExpr

// count
case object CountPathExpr extends PathExpr

// anyExpr, allExpr
case class AnyPathExpr(lambda: Option[LambdaVariableAndPredicate]) extends PathExpr
case class AllPathExpr(lambda: LambdaVariableAndPredicate) extends PathExpr

case class LambdaVariableAndPredicate(variableName: String, predicate: BooleanExpr)

// boundFunctionExpr
case class BoundFunctionCallPathExpr(functionName: String, args: Map[String, FunctionExprParam], subPath: Option[PathExpr]) extends PathExpr with SubPathExpr


// boolCommonExpr
sealed trait BooleanExpr extends Expression

// boolMethodCallExpr
case class BooleanMethodCallExpr(methodName: String, args: List[Expression]) extends BooleanExpr

// isofExpr
case class IsOfExpr(expression: Option[Expression], typeName: String) extends BooleanExpr

// notExpr
case class NotExpr(expression: BooleanExpr) extends BooleanExpr

// eqExpr, neExpr, ltExpr, leExpr, gtExpr, geExpr, hasExpr
sealed trait ComparisonExpr extends BooleanExpr {
  def left: Expression
  def right: Expression
}

case class EqExpr(left: Expression, right: Expression) extends ComparisonExpr
case class NeExpr(left: Expression, right: Expression) extends ComparisonExpr
case class LtExpr(left: Expression, right: Expression) extends ComparisonExpr
case class LeExpr(left: Expression, right: Expression) extends ComparisonExpr
case class GtExpr(left: Expression, right: Expression) extends ComparisonExpr
case class GeExpr(left: Expression, right: Expression) extends ComparisonExpr
case class HasExpr(left: Expression, right: Expression) extends ComparisonExpr

// andExpr, orExpr
sealed trait CompositeExpr extends BooleanExpr {
  def left: BooleanExpr
  def right: BooleanExpr
}

case class AndExpr(left: BooleanExpr, right: BooleanExpr) extends CompositeExpr
case class OrExpr(left: BooleanExpr, right: BooleanExpr) extends CompositeExpr
