/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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

import scala.beans.{BooleanBeanProperty, BeanProperty}
import scala.collection.JavaConverters._

/**
 * OData query.
 * 
 * @param operation The query operation.
 */
case class ODataQuery(operation: QueryOperation)

/**
 * Query operation.
 */
trait QueryOperation {

  /**
   * The name of the entity set from which this query operation selects entities.
   *
   * @return The name of the entity set from which this query operation selects entities.
   */
  def entitySetName: String

  /**
   * Indicates if this is a "select distinct" (a query that returns at most one result) or not.
   *
   * @return `true` if this is a "select distinct", `false` otherwise.
   */
  def selectDistinct: Boolean

  /**
   * Creates a `SelectPropertiesOperation` on top of this query operation, selecting the specified properties.
   * 
   * @param propertyNames Names of the properties to select.
   * @return A `SelectPropertiesOperation` that selects the specified properties, with this query operation as the source.
   */
  def select(propertyNames: String*): SelectPropertiesOperation = SelectPropertiesOperation(this, propertyNames.toList)

  /**
   * Creates a `CriteriaFilterOperation` on top of this query operation, specifying criteria to filter the results
   * of this query operation.
   * 
   * @param criteria The criteria to filter the results of this query operation with.
   * @return A `CriteriaFilterOperation` that applies the specified criteria, with this query operation as the source. 
   */
  def where(criteria: Criteria): CriteriaFilterOperation = CriteriaFilterOperation(this, criteria)
}

/**
 * Select operation. Selects entities from a specified entity set.
 * 
 * @param entitySetName The name of the entity set.
 * @param selectDistinct `true` if this is a "select distinct", `false` otherwise.
 */
case class SelectOperation(@BeanProperty entitySetName: String, @BooleanBeanProperty selectDistinct: Boolean = true)
  extends QueryOperation

// Used to specify which side of a join to select from
sealed trait JoinSelect
case object JoinSelectLeft extends JoinSelect
case object JoinSelectRight extends JoinSelect

/**
 * Join operation. Joins the results of two source query operations.
 *
 * Note that this only supports a limited, fixed form of join. The entities from the left source operation are supposed
 * to have a property of which the name is specified by `joinPropertyName`, which refers to entities from the right
 * source operation. The result of the join operation is a collection of entities from the right source operation.
 *
 * It is equivalent to the following JPQL query: `SELECT x FROM LeftSource l JOIN l.joinPropertyName r`
 * where `x` is either the left side or right side (`l` or `r`).
 *
 * This form of join is what the OData framework needs to do when in the URL there is a `PropertyPath` element that
 * refers to a navigation property. For example:
 *
 * http://myserver/odata.svc/ConfigurationItems('xyz')/Values
 *
 * This translates into an `ODataQuery` that looks like this:
 *
 * ODataQuery(
 *   JoinOperation(
 *     SelectByKeyOperation(SelectOperation("ConfigurationItems"), Map("ID" -> "xyz")),
 *     SelectOperation("ConfigurationValues"),
 *     "Values",
 *     JoinSelectRight
 *   )
 * )
 *
 * This translates into the following JPQL query:
 *
 * SELECT e2 FROM ConfigurationItems e1 JOIN e1.Values e2 WHERE e1.ID = 'xyz'
 * 
 * @param leftSource The left source.
 * @param rightSource The right source.
 * @param joinPropertyName The name of a navigation property in the left source that refers to entities in the right source.
 * @param joinSelect Specifies whether to select entities from the left or right side of the join.
 * @param outerJoin Specifies whether this is an outer join or not (if not, it is an inner join).
 */
case class JoinOperation(@BeanProperty leftSource: QueryOperation, @BeanProperty rightSource: QueryOperation,
                         @BeanProperty joinPropertyName: String, @BeanProperty joinSelect: JoinSelect,
                         @BooleanBeanProperty outerJoin: Boolean = false) extends QueryOperation {

  def entitySetName: String = joinSelect match {
    case JoinSelectLeft => leftSource.entitySetName
    case JoinSelectRight => rightSource.entitySetName
  }

  def selectDistinct: Boolean = joinSelect match {
    case JoinSelectLeft => leftSource.selectDistinct
    case JoinSelectRight => rightSource.selectDistinct
  }
}

/**
 * Filter operation. A filter operation modifies the output of another query operation by deciding for each element of
 * the output if it should be included or not. A filter operation does not change the content or type of the elements.
 */
sealed trait FilterOperation extends QueryOperation {

  /**
   * The source operation for this filter operation.
   * 
   * @return The source operation for this filter operation.
   */
  def source: QueryOperation

  def entitySetName: String = source.entitySetName

  def selectDistinct: Boolean = source.selectDistinct
}

/**
 * Select by key operation. This operation selects a single entity by key.
 * 
 * @param source The source operation.
 * @param key The key to filter on.
 */
case class SelectByKeyOperation(@BeanProperty source: QueryOperation, @BeanProperty key: Map[String, Any])
  extends FilterOperation {
  def getKeyAsJava: java.util.Map[String, Any] = key.asJava
}

/**
 * Skip operation. A skip operation is a filter operation that skips the first N elements of the source query operation.
 * 
 * @param source The source operation.
 * @param count The number of elements to skip.
 */
case class SkipOperation(@BeanProperty source: QueryOperation, @BeanProperty count: Int) extends FilterOperation

/**
 * Limit operation. A limit operation is a filter operation that limits the output to the first N elements of the
 * source query operation.
 * 
 * @param source The source operation.
 * @param count The maximum number of elements to return. 
 */
case class LimitOperation(@BeanProperty source: QueryOperation, @BeanProperty count: Int) extends FilterOperation

/**
  * Count operation. A count operation is a transform operation that gives back the count of
  * source query operation.
  * $skip and $limit should not affect the value of count.
  *
  * @param source The source operation.
  * @param trueFalse Value of count operat
  */
case class CountOperation(@BeanProperty source: QueryOperation, @BeanProperty trueFalse: Boolean)
  extends TransformOperation

/**
  * Value operation. A value operation is a transform operation that gets only primitive value of the
  * SelectPropertiesOperation.
  * /Persons('123')/firstName/@value
  *
  * @param source The source operation.
  */
case class ValueOperation(@BeanProperty source: QueryOperation)
  extends TransformOperation

/**
 * Criteria filter operation. A criteria filter operation filters the output of a source query operation using criteria
 * (like the "where" clause of an SQL statement).
 * 
 * @param source The source operation.
 * @param criteria The criteria to filter on.
 */
case class CriteriaFilterOperation(@BeanProperty source: QueryOperation, @BeanProperty criteria: Criteria)
  extends FilterOperation

/**
 * Transform operation. A transform operation modifies the output of another query operation and may change the content
 * or type of the elements.
 */
sealed trait TransformOperation extends QueryOperation {

  /**
   * The source operation for this transform operation.
   *
   * @return The source operation for this transform operation.
   */
  def source: QueryOperation

  def entitySetName: String = source.entitySetName

  def selectDistinct: Boolean = source.selectDistinct
}

/**
 * Expand operation. An expand operation is a transform operation that expands properties in the output of the source
 * query operation.
 * 
 * @param source The source operation.
 * @param expandProperties The properties to expand.
 */
case class ExpandOperation(@BeanProperty source: QueryOperation, @BeanProperty expandProperties: List[String])
  extends TransformOperation {
  def getExpandPropertiesAsJava: java.util.List[String] = expandProperties.asJava
}

sealed trait OrderByDirection

case object Ascending extends OrderByDirection {
  override def toString = "ASC"
}

case object Descending extends OrderByDirection {
  override def toString = "DESC"
}

case class OrderByProperty(@BeanProperty propertyName: String, @BeanProperty direction: OrderByDirection)

object OrderByProperty {
  def asc(fieldName: String): OrderByProperty = OrderByProperty(fieldName, Ascending)
  def desc(fieldName: String): OrderByProperty = OrderByProperty(fieldName, Descending)
}

/**
 * Order by operation. An order by operation is a transform operation that sorts the output of a source query operation
 * on one or more properties.
 *
 * @param source The source operation.
 * @param orderByProperties The properties to sort the results on.
 */
case class OrderByOperation(@BeanProperty source: QueryOperation, @BeanProperty orderByProperties: List[OrderByProperty])
  extends TransformOperation {
  def getOrderByPropertiesAsJava: java.util.List[OrderByProperty] = orderByProperties.asJava
}

/**
 * Select properties operation. A transform operation that selects one or more non-entity properties of each entity
 * from the source query operation.
 *
 * @param source The source operation.
 * @param propertyNames The names of the properties to select.
 */
case class SelectPropertiesOperation(@BeanProperty source: QueryOperation, @BeanProperty propertyNames: List[String])
  extends TransformOperation {
  def getPropertyNamesAsJava: java.util.List[String] = propertyNames.asJava
}

/**
 * Transform operation contains the inner function of an apply option.
 * 
 * @param methodName The name of the function.
 * @param expression The expression string for the function.
 */
case class ApplyFunction(@BeanProperty methodName: String, @BeanProperty expression: String)

/**
 *  Transform operation holding data of the $apply option.
 *  
 *  @param source The source operation.
 *  @param functionName The name of the apply custom function.
 *  @param propertyNames String list of the selected properties for the custom function.
 *  @param method Inner method of the custom function.
 */
case class ApplyOperation(@BeanProperty source: QueryOperation, @BeanProperty functionName: String,
                          @BeanProperty propertyNames: List[String], @BeanProperty method: ApplyFunction)
extends TransformOperation {
  def getApplyPropertiesAsJava: java.util.List[String] = propertyNames.asJava
}
