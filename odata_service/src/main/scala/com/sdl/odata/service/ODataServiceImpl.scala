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
package com.sdl.odata.service

import java.util.concurrent.TimeUnit.MILLISECONDS

import akka.actor.PoisonPill
import akka.pattern.ask
import akka.util.Timeout
import com.sdl.odata.api.service.{ODataRequest, ODataResponse, ODataService}
import com.sdl.odata.service.actor.ODataMessageRouter
import com.sdl.odata.service.protocol.{InitialServiceRequest, ServiceResponse}
import com.sdl.odata.service.spring.ActorProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * The OData Service Implementation
 *
 * ODataServiceImpl is the service which manages the lifecycle of ODataRequest.
 * First, it is responsible for handling ODataRequest, waiting for it's result and response back.
 *
 */
@Component
class ODataServiceImpl @Autowired() (producer: ActorProducer) extends ODataService {
  import com.sdl.odata.service.ODataServiceImpl._

  override def handleRequest(request: ODataRequest): ODataResponse = {
    LOG.debug("Handling request: {}", request)

    implicit val timeout = new Timeout(1000000000l, MILLISECONDS)

    val start = System.currentTimeMillis()
    val messageRouter = producer.actorRef(classOf[ODataMessageRouter].getSimpleName)
    val fut = ask(messageRouter, InitialServiceRequest(request)).mapTo[ServiceResponse]
    val serviceResponse = Await.result(fut, Duration.Inf)

    //kill the message router
    messageRouter.tell(PoisonPill, null)

    val stop = System.currentTimeMillis()

    LOG.debug("Request completed in " + (stop - start))

    serviceResponse.response
  }
}

object ODataServiceImpl {
  val LOG = LoggerFactory.getLogger(classOf[ODataServiceImpl])
}
