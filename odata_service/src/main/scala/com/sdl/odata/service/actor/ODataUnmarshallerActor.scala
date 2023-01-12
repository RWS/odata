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
package com.sdl.odata.service.actor

import com.sdl.odata.api.ODataSystemException
import com.sdl.odata.api.unmarshaller.{ODataUnmarshaller, UnmarshallerFactory}
import com.sdl.odata.service.protocol.{ODataActorContext, Unmarshall, UnmarshallResult}
import com.sdl.odata.service.spring.ActorProducer
import com.sdl.odata.service.util.AkkaUtil._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class ODataUnmarshallerActor @Autowired() (actorProducer: ActorProducer, unmarshallerFactory : UnmarshallerFactory) extends ODataActor {

  def receive = {
    case Unmarshall(actorContext) =>
      getUnmarshaller(actorContext) match {
        case Some(unmarshaller) =>
          log.debug("Selected unmarshaller: " + unmarshaller.getClass.getName)
          val data = unmarshaller.unmarshall(actorContext.requestContext)
          log.debug("Request unmarshalled, letting event bus know about it")

          routeMessage(actorProducer, context, UnmarshallResult(actorContext, Option(data)))
        case None =>
          throw new ODataSystemException("No unmarshaller available")
      }
  }

  private def getUnmarshaller(actorContext: ODataActorContext): Option[ODataUnmarshaller] = {
    import scala.collection.JavaConverters._

    val u = unmarshallerFactory.getUnmarshallers

    u.asScala.map(unmarshaller => (unmarshaller.score(actorContext.requestContext), unmarshaller))
      .filter({ case (score, _) => score > 0 })
      .sortBy({ case (score, _) => -score })
      .map({ case (_, unmarshaller) => unmarshaller })
      .headOption
  }
}
