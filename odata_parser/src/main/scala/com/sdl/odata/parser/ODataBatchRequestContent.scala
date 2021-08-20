/*
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

/**
 * ODataBatchRequestContent.
 */
case class ODataBatchRequestContent(requestComponents:List[ODataRequestComponent])

case class BatchRequestHeaders(headers : Map[String, String], headerType: BatchHeaderType)

trait ODataRequestComponent {
  def getHeaders():BatchRequestHeaders
}

case class BatchRequestComponent(requestComponentHeaders: BatchRequestHeaders, requestDetails:Map[String,String]) extends ODataRequestComponent {
  override def getHeaders(): BatchRequestHeaders = requestComponentHeaders
  def getRequestDetails(): Map[String,String] = requestDetails
}

case class ChangeSetRequestComponent(changeSetHeaders: BatchRequestHeaders, changesetRequests:List[BatchRequestComponent], changesetId:String) extends ODataRequestComponent {
  override def getHeaders(): BatchRequestHeaders = changeSetHeaders
  def getChangeSetRequests(): List[BatchRequestComponent] = changesetRequests
  def getChangeSetId(): String = changesetId
}

sealed trait BatchHeaderType

case object BatchTopLevelRequestHeader extends BatchHeaderType
case object IndividualRequestHeader extends BatchHeaderType
case object ChangeSetRequestHeader extends BatchHeaderType

