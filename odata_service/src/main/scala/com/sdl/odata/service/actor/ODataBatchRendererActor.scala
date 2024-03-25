/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.processor.query.QueryResult
import com.sdl.odata.api.service.ODataResponse
import com.sdl.odata.renderer.batch.ODataBatchRequestRenderer
import com.sdl.odata.service.protocol.{BatchOperationResult, ServiceResponse}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

/**
 * Renderer class for preparing the Batch Request Response.
 */
@Component
@Scope("prototype")
class ODataBatchRendererActor @Autowired()(batchRequestRenderer: ODataBatchRequestRenderer) extends ODataActor  {

  def receive = {
    case BatchOperationResult(actorContext, resultList) =>
      val responseBuilder = new ODataResponse.Builder()
      if (resultList != null) {
        batchRequestRenderer.render(actorContext.requestContext, QueryResult.from(resultList.asJava), responseBuilder)
      }
      responseBuilder.setStatus(ODataResponse.Status.OK)
      actorContext.origin ! ServiceResponse(actorContext, responseBuilder.build())
  }

}
