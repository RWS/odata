/**
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
package com.sdl.odata.service.actor

import com.sdl.odata.api.ODataErrorCode.UNKNOWN_ERROR
import com.sdl.odata.api._
import com.sdl.odata.api.processor.ProcessorResult
import com.sdl.odata.api.processor.datasource.{ODataDataSourceException, ODataEntityNotFoundException}
import com.sdl.odata.api.renderer.{ODataRenderer, RendererFactory}
import com.sdl.odata.api.service.ODataResponse
import com.sdl.odata.api.service.ODataResponse.Status._
import com.sdl.odata.service.protocol.{ErrorMessage, ODataActorContext, Render, ServiceResponse}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class ODataRendererActor @Autowired()(rendererFactory: RendererFactory) extends ODataActor {

  def receive = {
    case ErrorMessage(actorContext, ex) =>
      val responseBuilder = new ODataResponse.Builder()
      ex match {
        case odataKnownException:ODataException =>
          renderError(actorContext, odataKnownException, responseBuilder)

          odataKnownException match {
            case clientException: ODataUnsupportedMediaTypeException =>
              setStatus(actorContext, responseBuilder, UNSUPPORTED_MEDIA_TYPE)
            case clientException: ODataEntityNotFoundException =>
              setStatus(actorContext, responseBuilder, NOT_FOUND)
            case clientException: ODataClientException =>
              setStatus(actorContext, responseBuilder, BAD_REQUEST)
            case serverException: ODataDataSourceException =>
              setStatus(actorContext, responseBuilder, BAD_REQUEST)
            case serverException: ODataServerException =>
              setStatus(actorContext, responseBuilder, INTERNAL_SERVER_ERROR)
            case _ =>
              setStatus(actorContext, responseBuilder, INTERNAL_SERVER_ERROR)
          }
        case _ =>
          renderError(actorContext, new ODataServerException(UNKNOWN_ERROR, s"${ex.getClass.getName}: ${ex.getMessage}"), responseBuilder)
          setStatus(actorContext, responseBuilder, INTERNAL_SERVER_ERROR)
      }
      actorContext.origin ! ServiceResponse(actorContext, responseBuilder.build())
    case Render(actorContext, result) =>
      val responseBuilder = new ODataResponse.Builder()
      if (result.getData != null) {
        renderResult(actorContext, result, responseBuilder)
      }
      responseBuilder.setStatus(result.getStatus)
      if (result.getHeaders.size() > 0) {
        responseBuilder.setHeaders(result.getHeaders)
      }

      actorContext.origin ! ServiceResponse(actorContext, responseBuilder.build())
  }

  /**
   * Temporary solution for catching batch requests within ODataRenderActor.
   * If it's batch request we should set OK status even if we catch an error.
   * @param actorContext actorContext
   * @param responseBuilder responseBuilder
   * @param status response status
   */
  def setStatus(actorContext: ODataActorContext, responseBuilder: ODataResponse.Builder, status: ODataResponse.Status) {
    if (actorContext.requestContext.getRequest.getUri.contains("$batch"))
      responseBuilder.setStatus(ODataResponse.Status.OK)
    else
      responseBuilder.setStatus(status);
  }

  /**
   * Render an error.
   *
   * @param actorContext The actor context.
   * @param exception The source exception to use to render the error.
   * @param responseBuilder The response builder.
   */
  def renderError(actorContext: ODataActorContext, exception: ODataException, responseBuilder: ODataResponse.Builder) {
    getRenderer(actorContext, exception) match {
      case Some(renderer) =>
        renderer.render(actorContext.requestContext, exception, responseBuilder)

      case None =>
        renderErrorAsText(exception, responseBuilder)
    }
  }

  /**
   * Render the result from the processed operation.
   *
   * @param actorContext The actor context.
   * @param result The result to render.
   * @param responseBuilder The response builder.
   */
  def renderResult(actorContext: ODataActorContext, result: ProcessorResult, responseBuilder: ODataResponse.Builder) {
    getRenderer(actorContext, result.getData) match {
      case Some(renderer) =>
        renderer.render(actorContext.requestContext, result.getData, responseBuilder)

      case None =>
        renderError(actorContext, new ODataServerException(UNKNOWN_ERROR, "No renderer available"), responseBuilder)
    }
  }

  /**
   * Render an error as plain text.
   *
   * @param ex The source exception to use to render the error.
   * @param responseBuilder The response builder.
   */
  def renderErrorAsText(ex: Exception, responseBuilder: ODataResponse.Builder) {
    responseBuilder.setBodyText(Option(ex.getMessage).getOrElse("Unknown error"), "UTF-8")
    responseBuilder.setStatus(INTERNAL_SERVER_ERROR)
  }

  private def getRenderer(actorContext: ODataActorContext, data: AnyRef): Option[ODataRenderer] = {
    import scala.collection.JavaConversions._
    val r = rendererFactory.getRenderers
    r.map(renderer => (renderer.score(actorContext.requestContext, data), renderer))
      .filter({ case (score, _) => score > 0})
      .sortBy({ case (score, _) => -score})
      .map({ case (_, renderer) => renderer})
      .headOption
  }
}
