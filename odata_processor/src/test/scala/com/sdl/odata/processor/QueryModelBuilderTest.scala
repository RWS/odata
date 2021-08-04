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
package com.sdl.odata.processor

import com.sdl.odata.api.parser._
import com.sdl.odata.api.processor.query._
import com.sdl.odata.api.service.{MediaType, ODataRequestContext}
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory
import com.sdl.odata.processor.model.{ODataMobilePhone, ODataPerson}
import org.scalatest.FunSuite

class QueryModelBuilderTest extends FunSuite {

  private val entityDataModel = new AnnotationEntityDataModelFactory()
    .addClass(classOf[ODataPerson])
    .addClass(classOf[ODataMobilePhone])
    .buildEntityDataModel()

  test("/Persons") {
    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons", None), List()))
    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[SelectOperation],
      "Expected a SelectOperation, but found: " + query.operation.getClass.getName)

    val op = query.operation.asInstanceOf[SelectOperation]
    assert(op.entitySetName === "Persons")
  }

  test("/Persons('1')") {
    val id = "1"

    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons",
      Some(EntityCollectionPath(None, Some(KeyPredicatePath(SimpleKeyPredicate(StringLiteral(id)), None))))), List()))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[SelectByKeyOperation],
      "Expected a SelectByKeyOperation, but found: " + query.operation.getClass.getName)

    val op1 = query.operation.asInstanceOf[SelectByKeyOperation]
    val key = op1.key
    assert(key.size === 1)
    assert(key("id") === id)

    val op2 = op1.source
    assert(op2.isInstanceOf[SelectOperation])
    assert(op2.asInstanceOf[SelectOperation].entitySetName === "Persons")
  }

  test("/Persons(id='1')") {
    val id = "1"

    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons",
      Some(EntityCollectionPath(None, Some(KeyPredicatePath(CompoundKeyPredicate(Map("id" -> StringLiteral(id))), None))))), List()))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[SelectByKeyOperation],
      "Expected a SelectByKeyOperation, but found: " + query.operation.getClass.getName)

    val op1 = query.operation.asInstanceOf[SelectByKeyOperation]
    val key = op1.key
    assert(key.size === 1)
    assert(key("id") === id)

    val op2 = op1.source
    assert(op2.isInstanceOf[SelectOperation])
    assert(op2.asInstanceOf[SelectOperation].entitySetName === "Persons")
  }

  test("/Persons('1')/familyName") {
    val id = "1"

    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons",
      Some(EntityCollectionPath(None, Some(KeyPredicatePath(CompoundKeyPredicate(Map("id" -> StringLiteral(id))),
        Some(EntityPath(None, Some(PropertyPath("familyName", None))))))))), List()))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[SelectPropertiesOperation],
      "Expected a SelectPropertiesOperation, but found: " + query.operation.getClass.getName)

    val op1 = query.operation.asInstanceOf[SelectPropertiesOperation]
    val propertyNames = op1.propertyNames
    assert(propertyNames.size === 1)
    assert(propertyNames(0) === "familyName")

    val op2 = op1.source
    assert(op2.isInstanceOf[SelectByKeyOperation])
    val key = op2.asInstanceOf[SelectByKeyOperation].key
    assert(key.size === 1)
    assert(key("id") === id)

    val op3 = op2.asInstanceOf[SelectByKeyOperation].source
    assert(op3.isInstanceOf[SelectOperation])
    assert(op3.asInstanceOf[SelectOperation].entitySetName === "Persons")
  }

  test("/Persons?$filter=familyName eq 'Test'") {
    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons", None),
      List(FilterOption(EqExpr(PropertyPathExpr("familyName", None), LiteralExpr(StringLiteral("Test")))))))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[CriteriaFilterOperation])
    val op1 = query.operation.asInstanceOf[CriteriaFilterOperation]

    assert(op1.criteria.isInstanceOf[ComparisonCriteria])
    val cr1 = op1.criteria.asInstanceOf[ComparisonCriteria]
    assert(cr1.operator === EqOperator)

    assert(cr1.left.isInstanceOf[PropertyCriteriaValue])
    val cv1 = cr1.left.asInstanceOf[PropertyCriteriaValue]
    assert(cv1.propertyName === "familyName")

    assert(cr1.right.isInstanceOf[LiteralCriteriaValue])
    val cv2 = cr1.right.asInstanceOf[LiteralCriteriaValue]
    assert(cv2.value === "Test")

    assert(op1.source.isInstanceOf[SelectOperation])
    val op2 = op1.source.asInstanceOf[SelectOperation]
    assert(op2.entitySetName === "Persons")
  }

  test("/Persons?$top=10") {
    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons", None), List(TopOption(10))))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[LimitOperation])
    val op1 = query.operation.asInstanceOf[LimitOperation]

    assert(op1.count === 10)

    assert(op1.source.isInstanceOf[SelectOperation])
    val op2 = op1.source.asInstanceOf[SelectOperation]
    assert(op2.entitySetName === "Persons")
  }

  test("/Persons?$skip=20") {
    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons", None), List(SkipOption(20))))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[SkipOperation])
    val op1 = query.operation.asInstanceOf[SkipOperation]

    assert(op1.count === 20)

    assert(op1.source.isInstanceOf[SelectOperation])
    val op2 = op1.source.asInstanceOf[SelectOperation]
    assert(op2.entitySetName === "Persons")
  }

  test("/Persons?$expand=configurationItems") {
    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons", None),
      List(ExpandOption(
        List(PathExpandItem(None, NavigationPropertyExpandPathSegment("configurationItems", None), List()))))))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[ExpandOperation])
    val op1 = query.operation.asInstanceOf[ExpandOperation]

    val fields = op1.expandProperties
    assert(fields.size === 1)
    assert(fields(0) === "configurationItems")

    assert(op1.source.isInstanceOf[SelectOperation])
    val op2 = op1.source.asInstanceOf[SelectOperation]
    assert(op2.entitySetName === "Persons")
  }

  test("/Persons?$orderby=name desc&$format=json") {
    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons", None),
      List(OrderByOption(List(DescendingOrderByItem(PropertyPathExpr("name", None)))), FormatOption(MediaType.JSON))))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[OrderByOperation])
    val op1 = query.operation.asInstanceOf[OrderByOperation]

    val fields = op1.orderByProperties
    assert(fields.size === 1)
    val field1 = fields(0)
    assert(field1.direction === Descending)
    assert(field1.propertyName === "name")

    assert(op1.source.isInstanceOf[SelectOperation])
    val op2 = op1.source.asInstanceOf[SelectOperation]
    assert(op2.entitySetName === "Persons")
  }

  test("/Persons('1')/mobilePhones") {
    val id = "1"

    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons",
      Some(EntityCollectionPath(None, Some(KeyPredicatePath(CompoundKeyPredicate(Map("id" -> StringLiteral(id))),
        Some(EntityPath(None, Some(PropertyPath("mobilePhones", None))))))))), List()))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    assert(query.operation.isInstanceOf[JoinOperation],
      "Expected a JoinOperation, but found: " + query.operation.getClass.getName)
    val opJoin = query.operation.asInstanceOf[JoinOperation]

    assert(opJoin.joinPropertyName === "mobilePhones",
      "Expected join property name 'mobilePhones', but found something else instead: " + opJoin.joinPropertyName)

    assert(opJoin.leftSource.isInstanceOf[SelectByKeyOperation],
      "Expected a SelectByKeyOperation on the left side, but found: " + opJoin.leftSource.getClass.getName)
    val opLeft = opJoin.leftSource.asInstanceOf[SelectByKeyOperation]

    val key = opLeft.key
    assert(key.size === 1, "Expected 1 key field, but found " + key.size + " instead")
    assert(key("id") === id, "Expected key field to be named 'id', but found something else instead: " + key)

    assert(opLeft.source.isInstanceOf[SelectOperation],
      "Expected a SelectOperation, but found: " + opLeft.source.getClass.getName)
    val opLeftSource = opLeft.source.asInstanceOf[SelectOperation]

    assert(opLeftSource.entitySetName === "Persons")

    assert(opJoin.rightSource.isInstanceOf[SelectOperation],
      "Expected a SelectOperation on the right side, but found: " + opJoin.rightSource.getClass.getName)
    val opRight = opJoin.rightSource.asInstanceOf[SelectOperation]

    assert(opRight.entitySetName === "MobilePhones")
  }

  test("/Persons('1')/mobilePhones?$skip=10&$orderby=phoneNumber") {
    val id = "1"

    val uri = ODataUri("", ResourcePathUri(EntitySetPath("Persons",
      Some(EntityCollectionPath(None, Some(KeyPredicatePath(SimpleKeyPredicate(StringLiteral(id)),
        Some(EntityPath(None, Some(PropertyPath("mobilePhones", None))))))))),
      List(SkipOption(10), OrderByOption(List(AscendingOrderByItem(PropertyPathExpr("phoneNumber", None)))))))

    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))

    val expected = ODataQuery(
      OrderByOperation(
        SkipOperation(
          JoinOperation(
            SelectByKeyOperation(
              SelectOperation("Persons"),
              Map("id" -> id)
            ),
            SelectOperation("MobilePhones"),
            "mobilePhones",
            JoinSelectRight
          ),
          10
        ),
        List(OrderByProperty("phoneNumber", Ascending))
      )
    )

    assert(query === expected)
  }

  // groupby test
  test("groupby") {
    val uri = ODataUri("", ResourcePathUri(
      EntitySetPath("Persons", None),
      List(ApplyOption(ApplyExpr("groupby", ApplyMethodCallExpr(ApplyPropertyExpr(
        List(EntityPathExpr(None, Some(PropertyPathExpr("id", None))))),
        ApplyFunctionExpr("aggregate", "$count as PersonCount")))))))
    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))
    val expected = ODataQuery(ApplyOperation(SelectOperation("Persons", true), "groupby", List("id"), ApplyFunction("aggregate", "$count as PersonCount")))
    assert(query === expected)
  }

  // $count test
  test("$count") {
    val uri = ODataUri("", ResourcePathUri(
      EntitySetPath("Persons", None),
      List(CountOption(true))))
    val query = new QueryModelBuilder(entityDataModel).build(new ODataRequestContext(null, uri, entityDataModel))
    val expected = ODataQuery(CountOperation(SelectOperation("Persons", true), true))
    assert(query === expected)
  }
}
