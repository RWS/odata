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

import java.nio.charset.StandardCharsets.UTF_8

import com.sdl.odata.api.ODataErrorCode.UNKNOWN_ERROR
import com.sdl.odata.api._
import com.sdl.odata.api.processor.ProcessorResult
import com.sdl.odata.api.processor.datasource.{ODataDataSourceException, ODataEntityNotFoundException}
import com.sdl.odata.api.processor.query.QueryResult
import com.sdl.odata.api.processor.query.QueryResult.ResultType
import com.sdl.odata.api.renderer.{ODataRenderer, RendererFactory}
import com.sdl.odata.api.service.ODataResponse.Status._
import com.sdl.odata.api.service.{ODataContentStreamer, ODataResponse}
import com.sdl.odata.service.protocol.{ErrorMessage, ODataActorContext, Render, ServiceResponse}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class ODataRendererActor @Autowired()(rendererFactory: RendererFactory) extends ODataActor {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[ODataRendererActor])

  def receive = {
    case ErrorMessage(actorContext, e) =>
      val responseBuilder = new ODataResponse.Builder()
      e match {
        case clientException: ODataUnsupportedMediaTypeException =>
          logger.error(s"Invalid request: '${e.getMessage}'", e)
          renderError(actorContext, clientException, responseBuilder)
          setStatus(actorContext, responseBuilder, UNSUPPORTED_MEDIA_TYPE)
        case clientException: ODataEntityNotFoundException =>
          logger.warn(s"Entity not found: '${e.getMessage}'", e)
          renderError(actorContext, clientException, responseBuilder)
          setStatus(actorContext, responseBuilder, NOT_FOUND)
        case clientException: ODataClientException =>
          logger.error(s"Invalid request - ${e.getClass.getName}: '${e.getMessage}'", e)
          renderError(actorContext, clientException, responseBuilder)
          setStatus(actorContext, responseBuilder, BAD_REQUEST)
        case serverException: ODataDataSourceException =>
          logger.error(s"Error during datasource access: '${e.getMessage}'", e)
          renderError(actorContext, serverException, responseBuilder)
          setStatus(actorContext, responseBuilder, BAD_REQUEST)
        case serverException: ODataServerException =>
          logger.error(s"Exception during response rendering - ${e.getClass.getName}: '${e.getMessage}'", e)
          renderError(actorContext, serverException, responseBuilder)
          setStatus(actorContext, responseBuilder, INTERNAL_SERVER_ERROR)
        case knownException: ODataException =>
          logger.error(s"Exception during response rendering - ${e.getClass.getName}: '${e.getMessage}'", e)
          renderError(actorContext, knownException, responseBuilder)
          setStatus(actorContext, responseBuilder, INTERNAL_SERVER_ERROR)
        case _ =>
          logger.error(s"Unexpected exception during response rendering - ${e.getClass.getName}: '${e.getMessage}'", e)
          renderError(actorContext, new ODataServerException(UNKNOWN_ERROR, s"${e.getClass.getName}: ${e.getMessage}"),
            responseBuilder)
          setStatus(actorContext, responseBuilder, INTERNAL_SERVER_ERROR)
      }
      actorContext.origin ! ServiceResponse(actorContext, responseBuilder.build())
    case Render(actorContext, result) =>
      val responseBuilder = new ODataResponse.Builder()
      Option(result.getQueryResult) match {
        case Some(queryResult) => queryResult.getType match {
          case ResultType.STREAM =>
            getRenderer(actorContext, result.getQueryResult) match {
              case Some(renderer) =>
                responseBuilder.setODataContent(
                  new ODataContentStreamer(renderer, actorContext.requestContext, result.getQueryResult))
              case None => renderError(actorContext,
                new ODataServerException(UNKNOWN_ERROR, "No renderer available"), responseBuilder)
            }
          case _ => if (result.getData != null) {
            renderResult(actorContext, result, responseBuilder)
          }
        }
        case None => if (result.getData != null) {
          renderResult(actorContext, result, responseBuilder)
        }
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
    *
    * @param actorContext    actorContext
    * @param responseBuilder responseBuilder
    * @param status          response status
    */
  def setStatus(actorContext: ODataActorContext, responseBuilder: ODataResponse.Builder, status: ODataResponse.Status) {
    if (actorContext.requestContext.getRequest.getUri.contains("$batch"))
      responseBuilder.setStatus(ODataResponse.Status.OK)
    else
      responseBuilder.setStatus(status)
  }

  /**
    * Render an error.
    *
    * @param actorContext    The actor context.
    * @param exception       The source exception to use to render the error.
    * @param responseBuilder The response builder.
    */
  def renderError(actorContext: ODataActorContext, exception: ODataException, responseBuilder: ODataResponse.Builder) {
    val exceptionResult = QueryResult.from(exception)
    getRenderer(actorContext, exceptionResult) match {
      case Some(renderer) =>
        renderer.render(actorContext.requestContext, exceptionResult, responseBuilder)

      case None =>
        renderErrorAsText(exception, responseBuilder)
    }
  }

  /**
    * Render the result from the processed operation.
    *
    * @param actorContext    The actor context.
    * @param result          The result to render.
    * @param responseBuilder The response builder.
    */
  def renderResult(actorContext: ODataActorContext, result: ProcessorResult, responseBuilder: ODataResponse.Builder) {
    getRenderer(actorContext, result.getQueryResult) match {
      case Some(renderer) =>
        renderer.render(actorContext.requestContext, result.getQueryResult, responseBuilder)
      case None =>
        renderError(actorContext, new ODataServerException(UNKNOWN_ERROR, "No renderer available"), responseBuilder)
    }
  }

  /**
    * Render an error as plain text.
    *
    * @param ex              The source exception to use to render the error.
    * @param responseBuilder The response builder.
    */
  def renderErrorAsText(ex: Exception, responseBuilder: ODataResponse.Builder) {
    responseBuilder.setBodyText(Option(ex.getMessage).getOrElse("Unknown error"), UTF_8.name())
    responseBuilder.setStatus(INTERNAL_SERVER_ERROR)
  }

  private def getRenderer(actorContext: ODataActorContext, data: QueryResult): Option[ODataRenderer] = {
    import scala.collection.JavaConverters._
    val r = rendererFactory.getRenderers
    r.asScala.map(renderer => (renderer.score(actorContext.requestContext, data), renderer))
      .filter({ case (score, _) => score > 0 })
      .sortBy({ case (score, _) => -score })
      .map({ case (_, renderer) => renderer })
      .headOption
  }
}
