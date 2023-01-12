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
package com.sdl.odata.api.processor.query

import scala.beans.BeanProperty

/**
 * Criteria to be used by a `CriteriaFilterOperation`.
 */
trait Criteria {

  /**
   * Creates a `CompositeCriteria` combining this criteria with other criteria using the "and" operator.
   *
   * @param right The right hand side criteria.
   * @return A `CompositeCriteria` combining this criteria with other criteria using the "and" operator.
   */
  def and(right: Criteria): CompositeCriteria = CompositeCriteria(AndOperator, this, right)

  /**
   * Creates a `CompositeCriteria` combining this criteria with other criteria using the "or" operator.
   *
   * @param right The right hand side criteria.
   * @return A `CompositeCriteria` combining this criteria with other criteria using the "or" operator.
   */
  def or(right: Criteria): CompositeCriteria = CompositeCriteria(OrOperator, this, right)
}

sealed trait CompositeOperator

case object AndOperator extends CompositeOperator {
  override def toString = "AND"
}

case object OrOperator extends CompositeOperator {
  override def toString = "OR"
}

/**
 * Composite criteria. This combines two criteria with a `CompositeOperator`.
 *
 * @param operator The operator.
 * @param left The left hand side criteria.
 * @param right The right hand side criteria.
 */
case class CompositeCriteria(@BeanProperty operator: CompositeOperator,
                             @BeanProperty left: Criteria, @BeanProperty right: Criteria) extends Criteria

/**
 * Criteria value.
 */
sealed trait CriteriaValue {
  def add(right: CriteriaValue): ArithmeticCriteriaValue = ArithmeticCriteriaValue(AddOperator, this, right)
  def sub(right: CriteriaValue): ArithmeticCriteriaValue = ArithmeticCriteriaValue(SubOperator, this, right)
  def mul(right: CriteriaValue): ArithmeticCriteriaValue = ArithmeticCriteriaValue(MulOperator, this, right)
  def div(right: CriteriaValue): ArithmeticCriteriaValue = ArithmeticCriteriaValue(DivOperator, this, right)
  def mod(right: CriteriaValue): ArithmeticCriteriaValue = ArithmeticCriteriaValue(ModOperator, this, right)

  def eq(right: CriteriaValue): ComparisonCriteria = ComparisonCriteria(EqOperator, this, right)
  def ne(right: CriteriaValue): ComparisonCriteria = ComparisonCriteria(NeOperator, this, right)
  def lt(right: CriteriaValue): ComparisonCriteria = ComparisonCriteria(LtOperator, this, right)
  def le(right: CriteriaValue): ComparisonCriteria = ComparisonCriteria(LeOperator, this, right)
  def gt(right: CriteriaValue): ComparisonCriteria = ComparisonCriteria(GtOperator, this, right)
  def ge(right: CriteriaValue): ComparisonCriteria = ComparisonCriteria(GeOperator, this, right)

  // String functions and geo.intersect function added.
  def startswith(right: CriteriaValue): StartsWithMethodCriteria = StartsWithMethodCriteria(this, right)
  def contains(right: CriteriaValue): ContainsMethodCriteria = ContainsMethodCriteria(this, right)
  def endswith(right: CriteriaValue): EndsWithMethodCriteria = EndsWithMethodCriteria(this, right)
  def geointersects(right: CriteriaValue): GeoIntersectsMethodCriteria = GeoIntersectsMethodCriteria(this, right)
}

sealed trait ArithmeticOperator

case object AddOperator extends ArithmeticOperator {
  override def toString = "+"
}

case object SubOperator extends ArithmeticOperator {
  override def toString = "-"
}

case object MulOperator extends ArithmeticOperator {
  override def toString = "*"
}

case object DivOperator extends ArithmeticOperator {
  override def toString = "/"
}

case object ModOperator extends ArithmeticOperator {
  override def toString = "%"
}

/**
 * Arithmetic criteria value. This combines two criteria values with an `ArithmeticOperator`.
 *
 * @param operator The operator.
 * @param left The left hand side criteria value.
 * @param right The right hand side criteria value.
 */
case class ArithmeticCriteriaValue(@BeanProperty operator: ArithmeticOperator,
                                   @BeanProperty left: CriteriaValue, @BeanProperty right: CriteriaValue)
  extends CriteriaValue

/**
 * Property criteria value.
 *
 * @param propertyName The property name.
 */
case class PropertyCriteriaValue(@BeanProperty propertyName: String) extends CriteriaValue

/**
 * Literal criteria value.
 *
 * @param value The literal value.
 */
case class LiteralCriteriaValue(@BeanProperty value: Any) extends CriteriaValue

sealed trait ComparisonOperator

case object EqOperator extends ComparisonOperator {
  override def toString = "="
}

case object NeOperator extends ComparisonOperator {
  override def toString = "<>"
}

case object LtOperator extends ComparisonOperator {
  override def toString = "<"
}

case object LeOperator extends ComparisonOperator {
  override def toString = "<="
}

case object GtOperator extends ComparisonOperator {
  override def toString = ">"
}

case object GeOperator extends ComparisonOperator {
  override def toString = ">="
}

/**
 * Trait for method criteria.
 */
trait MethodCriteria extends Criteria

/**
 * Comparison criteria. This combines two criteria values with a `ComparisonOperator`.
 *
 * @param operator The operator.
 * @param left The left hand side criteria value.
 * @param right The right hand side criteria value.
 */
case class ComparisonCriteria(@BeanProperty operator: ComparisonOperator,
                              @BeanProperty left: CriteriaValue, @BeanProperty right: CriteriaValue) extends Criteria

/**
 * StartsWithMethodCriteria criteria.
 *
 * @param property The property name.
 * @param stringLiteral The string literal as criteria value.
 */
case class StartsWithMethodCriteria(@BeanProperty property: CriteriaValue, @BeanProperty stringLiteral: CriteriaValue) extends MethodCriteria

/**
 * EndsWithMethodCriteria criteria.
 *
 * @param property The property name.
 * @param stringLiteral The string literal as criteria value.
 */
case class EndsWithMethodCriteria(@BeanProperty property: CriteriaValue, @BeanProperty stringLiteral: CriteriaValue) extends MethodCriteria

/**
 * ContainsMethodCriteria criteria.
 *
 * @param property The property name.
 * @param stringLiteral The string literal as criteria value.
 */
case class ContainsMethodCriteria(@BeanProperty property: CriteriaValue, @BeanProperty stringLiteral: CriteriaValue) extends MethodCriteria

/**
 * GeoIntersectsMethodCriteria criteria.
 *
 * @param property The property name.
 * @param stringLiteral The string literal as criteria value.
 */
case class GeoIntersectsMethodCriteria(@BeanProperty property: CriteriaValue, @BeanProperty stringLiteral: CriteriaValue) extends MethodCriteria
