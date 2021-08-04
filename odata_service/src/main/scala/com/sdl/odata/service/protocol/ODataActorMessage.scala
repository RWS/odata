/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.service.protocol

import akka.actor.ActorRef
import com.sdl.odata.api.parser.ODataUri
import com.sdl.odata.api.processor.ProcessorResult
import com.sdl.odata.api.service.{ODataRequest, ODataRequestContext, ODataResponse}
import com.sdl.odata.parser.ODataBatchRequestContent

case class ODataActorContext(requestContext: ODataRequestContext, origin: ActorRef)

sealed trait ODataActorMessage

// Register an actor to handle a specified type of message
case class RegisterMessageHandler(messageType: Class[_ <: ODataActorMessage], beanName: String) extends ODataActorMessage

// Unregister an actor the handle a specified type of message
case class UnregisterMessageHandler(messageType: Class[_ <: ODataActorMessage], beanName: String) extends ODataActorMessage

case class ErrorMessage(actorContext: ODataActorContext, ex: Throwable) extends ODataActorMessage

// Initial request sent by ODataServiceImpl to ODataMessageRouter
case class InitialServiceRequest(request: ODataRequest) extends ODataActorMessage

case class ServiceRequest(actorContext: ODataActorContext) extends ODataActorMessage

case class ParseUri(actorContext: ODataActorContext) extends ODataActorMessage

case class ParseResult(actorContext: ODataActorContext, uri: ODataUri) extends ODataActorMessage

case class Unmarshall(actorContext: ODataActorContext) extends ODataActorMessage

case class UnmarshallResult(actorContext: ODataActorContext, data: Option[AnyRef]) extends ODataActorMessage

case class ReadOperation(actorContext: ODataActorContext, data: Option[AnyRef]) extends ODataActorMessage

case class WriteOperation(actorContext: ODataActorContext, data: Option[AnyRef]) extends ODataActorMessage

case class OperationResult(actorContext: ODataActorContext, result: ProcessorResult) extends ODataActorMessage

case class Render(actorContext: ODataActorContext, result: ProcessorResult) extends ODataActorMessage

case class ServiceResponse(actorContext: ODataActorContext, response: ODataResponse) extends ODataActorMessage

case class BatchOperation(actorContext: ODataActorContext, data: Option[ODataBatchRequestContent]) extends ODataActorMessage

case class BatchOperationResult(actorContext: ODataActorContext, result: List[ProcessorResult]) extends ODataActorMessage
