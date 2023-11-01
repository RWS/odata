/**
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
package com.sdl.odata.service.util

import akka.actor.{Actor, ActorContext, ActorRef}
import com.sdl.odata.service.actor.MessageHandlerRegistry._
import com.sdl.odata.service.actor.ODataMessageRouter
import com.sdl.odata.service.protocol.{ODataActorMessage, RegisterMessageHandler}
import com.sdl.odata.service.spring.ActorProducer
import org.slf4j.{Logger, LoggerFactory}

/**
 * Akka Util Class is resppnsible for registering routes for actors.
 *
 */
object AkkaUtil {
  private val logger: Logger = LoggerFactory.getLogger("AkkaUtil")

  def registerRoute(messageType: Class[_ <: ODataActorMessage], actorType: Class[_ <: Actor])(implicit producer: ActorProducer) {
    messageRouter().tell(RegisterMessageHandler(messageType, actorType.getSimpleName), null)
  }

  private def messageRouter()(implicit producer : ActorProducer): ActorRef =
    actorRef(classOf[ODataMessageRouter])

  private def actorRef(actorType: Class[_ <: Actor])(implicit producer: ActorProducer): ActorRef = {
    producer.actorRef(actorType.getSimpleName)
  }

  def routeMessage(actorProducer: ActorProducer, context: ActorContext, message: ODataActorMessage) {
    logger.debug(s"Routing message: $message")

    val messageType = message.getClass
    if (contains(messageType)) {
      get(messageType).foreach {
        beanName =>
          logger.debug(s"Sending message to: $beanName")

          actorProducer.tell(beanName, message, context.self, context)
      }
    } else {
      logger.warn(s"No handler registered for message type: $messageType")
    }
  }
}
