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
package com.sdl.odata.service.actor

import com.sdl.odata.api.service.ODataRequestContextUtil.{checkSupportedType, isBatchOperation, isReadOperation, isWriteOperation}
import com.sdl.odata.parser.ODataBatchRequestContent
import com.sdl.odata.service.protocol._
import com.sdl.odata.service.spring.ActorProducer
import com.sdl.odata.service.util.AkkaUtil._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * The main processor that is responsible for handling the initial request and determine what steps to execute
 * @param actorProducer The actor producer for creating child actors
 */
@Component
@Scope("prototype")
class ODataRequestProcessorActor @Autowired()(actorProducer: ActorProducer) extends ODataActor {


  override def receive = {
    case ParseResult(actorContext, uri) =>
      // Add URI to request context
      val newRequestContext = actorContext.requestContext.withUri(uri)
      val newActorContext = ODataActorContext(newRequestContext, actorContext.origin)

      // Note: Check if there is at least one format specified but none of the ones specified are supported
      // In such case the framework should do an early return with a status code 415 unsupported media type
      checkSupportedType(newRequestContext)

      log.debug("Parse Result received. Sending Unmarshall request operation")

      if (isReadOperation(actorContext.requestContext)) {
        //Early redirect, if we are only reading, let's immediately do that, no need to unmarshall
        routeMessage(actorProducer, context, ReadOperation(newActorContext, Option(null)))
      } else {
        routeMessage(actorProducer, context, Unmarshall(newActorContext))
      }
    case UnmarshallResult(actorContext, data) =>
      val method = actorContext.requestContext.getRequest.getMethod
      if (isBatchOperation(actorContext.requestContext)) {
        log.debug("Unmarshall Result received. Sending for BatchOperation")

        routeMessage(actorProducer, context, BatchOperation(actorContext,
          data.asInstanceOf[Some[ODataBatchRequestContent]]))
      } else if (isWriteOperation(actorContext.requestContext)) {
        log.debug("Unmarshall Result received. Sending for WriteOperation")

        routeMessage(actorProducer, context, WriteOperation(actorContext, data))
      } else if (isReadOperation(actorContext.requestContext)) {
        log.debug("Unmarshall Result received. Sending for ReadOperation")

        routeMessage(actorProducer, context, ReadOperation(actorContext, data))
      } else {
        log.warning(s"Unmarshall Result received. But given http method $method is not supported")
        throw new UnsupportedOperationException(s"Given http method $method is not supported")
      }

    case OperationResult(actorContext, data) =>
      log.debug("Operation Result received. Sending Marshall request operation")

      routeMessage(actorProducer, context, Render(actorContext, data))
  }
}
