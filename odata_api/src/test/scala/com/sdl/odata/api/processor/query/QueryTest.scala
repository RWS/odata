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
package com.sdl.odata.api.processor.query

import org.scalatest.FunSuite

class QueryTest extends FunSuite {

  test("/Components?$filter=customMeta.Key eq 'Test' and customMeta.Value eq 'MyTestValue'") {
    // JPA query: SELECT c FROM Components c JOIN c.customMeta cm WHERE cm.Key = ? AND cm.Value = ?
    ODataQuery(
      JoinOperation(
        SelectOperation("Components"),
        SelectOperation("CustomMeta")
          .where(PropertyCriteriaValue("Key").eq(LiteralCriteriaValue("Test"))
            .and(PropertyCriteriaValue("Value").eq(LiteralCriteriaValue("MyTestValue")))),
        "customMeta", JoinSelectRight
      )
    )
  }

  test("/Customers(123)/address") {
    // JPA query: SELECT c.Address FROM Customers c where c.id = ?
    ODataQuery(
      SelectPropertiesOperation(
        SelectByKeyOperation(
            SelectOperation("Customers"),
            Map("id" -> "123")),
        List("address")
      )
    )
  }

  test("/Persons?$expand=details"){
    ODataQuery(
      ExpandOperation(
        SelectOperation("Persons"), List("details")
      )
    )
  }

  test("/Components(ItemId=100,PublicationId=10)/Comments"){
    ODataQuery(
      JoinOperation(
        SelectOperation("Components")
          .where(
            PropertyCriteriaValue("ItemId")
              .eq(LiteralCriteriaValue(100))
              .and(PropertyCriteriaValue("PublicationId").eq(LiteralCriteriaValue(10)))),
        SelectOperation("Comments"),
        "comments", JoinSelectRight
      )
    )
  }

  test("/Promotion('MyColaCampaign')?$filter=hits mul 2 gt 100"){
    ODataQuery(
      SelectByKeyOperation(
        SelectOperation("Promotions"), Map("title" -> "MyColaCampaign")
      )
      .where(
        PropertyCriteriaValue("hits").mul(LiteralCriteriaValue(2)).gt(LiteralCriteriaValue(100))
      )
    )
  }
}
