/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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

import com.sdl.odata.api.parser.ODataUriUtil.isFunctionCallUri
import com.sdl.odata.api.processor.{ODataFunctionProcessor, ODataQueryProcessor}
import com.sdl.odata.service.protocol.{ReadOperation, Render}
import com.sdl.odata.service.spring.ActorProducer
import com.sdl.odata.service.util.AkkaUtil._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class ODataQueryProcessorActor @Autowired()(actorProducer: ActorProducer, queryProcessor: ODataQueryProcessor,
                                            oDataFunctionProcessor: ODataFunctionProcessor) extends ODataActor {

    def receive = {
    case ReadOperation(actorContext, data) =>
      log.debug("ODataQueryProcessor found and submitting for query result")

      val result = if (isFunctionCallUri(actorContext.requestContext.getUri))
        oDataFunctionProcessor.doFunction(actorContext.requestContext) else
        queryProcessor.query(actorContext.requestContext, data.orNull)
      log.debug("Execution completed, submitting result to event bus")

      routeMessage(actorProducer, context, Render(actorContext, result))
  }
}
