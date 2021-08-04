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

import com.sdl.odata.api.parser.ODataParser
import com.sdl.odata.service.protocol.{ParseResult, ServiceRequest}
import com.sdl.odata.service.spring.ActorProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class ODataParserActor @Autowired()(actorProducer: ActorProducer, parser: ODataParser) extends ODataActor {

  import com.sdl.odata.service.util.AkkaUtil.routeMessage

  def receive = {
    case ServiceRequest(actorContext) =>
      val rc = actorContext.requestContext
      val parsedUri = parser.parseUri(rc.getRequest().getUri(), rc.getEntityDataModel())

      routeMessage(actorProducer, context, ParseResult(actorContext, parsedUri))
  }
}
