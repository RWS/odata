/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import akka.actor.SupervisorStrategy.{Resume, Stop}
import akka.actor._
import com.sdl.odata.api.edm.registry.ODataEdmRegistry
import com.sdl.odata.api.service.ODataRequestContext
import com.sdl.odata.service.protocol._
import com.sdl.odata.service.spring.ActorProducer
import com.sdl.odata.service.util.AkkaUtil
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * ODataMessageRouter is responsible for creating a routing logic and execute the logic sequentially.
 *
 * It acts as an event bus where events can be submitted in a generic manner,
 * which then gets picked up by the interested actors.
 */
@Component("ODataMessageRouter")
@Scope(value = "prototype")
class ODataMessageRouter @Autowired()(serviceRegistry: ODataEdmRegistry, actorProducer: ActorProducer) extends ODataActor {
  import com.sdl.odata.service.actor.MessageHandlerRegistry._
  import com.sdl.odata.service.actor.ODataMessageRouter._
  import AkkaUtil._

  var origin: Option[ActorRef] = None
  var requestContext: Option[ODataRequestContext] = None

  //The main supervisor strategy, all child actors escalate to this strategy
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case exp: Exception =>
      LOG.debug("Sending error message for exception:", exp)
      routeMessage(actorProducer, context, ErrorMessage(ODataActorContext(requestContext.get, origin.get), exp))
      Resume
    case error: Error =>
      LOG.error("Runtime Exception occurred. Shutting down the Actor System", error)
      routeMessage(actorProducer, context, ErrorMessage(ODataActorContext(requestContext.get, origin.get), error))
      Stop
  }

  def receive = {
    case RegisterMessageHandler(messageType, beanName) => registerMessageHandler(messageType, beanName)

    case UnregisterMessageHandler(messageType, beanName) => unregisterMessageHandler(messageType, beanName)

    case msg: InitialServiceRequest => handleInitialServiceRequest(msg)
  }

  def registerMessageHandler(messageType: Class[_ <: ODataActorMessage], beanName: String) = {
    log.debug(s"Registering handler for message type: $messageType => $sender")
    add(messageType, beanName)
  }

  def unregisterMessageHandler(messageType: Class[_ <: ODataActorMessage], beanName: String) = {
    log.debug(s"Unregistering handler for message type: $messageType => $sender")
    remove(messageType, beanName)
  }

  def handleInitialServiceRequest(serviceRequest: InitialServiceRequest) {
    log.debug("Handling initial service request")

    origin = Some(sender)

    requestContext = Some(new ODataRequestContext(serviceRequest.request, serviceRegistry.getEntityDataModel))
    routeMessage(actorProducer, context, ServiceRequest(ODataActorContext(requestContext.get, sender)))
  }
}

object ODataMessageRouter {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[ODataMessageRouter])
}
