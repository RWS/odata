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
package com.sdl.odata.service.actor

import java.util

import com.sdl.odata.api.ODataBadRequestException
import com.sdl.odata.api.parser.{ODataBatchParseException, ODataUri}
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory
import com.sdl.odata.api.processor.{ODataQueryProcessor, ProcessorResult}
import com.sdl.odata.api.service.ODataRequest.Method
import com.sdl.odata.api.service.{ChangeSetEntity, MediaType, ODataRequest, ODataRequestContext}
import com.sdl.odata.parser._
import com.sdl.odata.processor.write.BatchMethodHandler
import com.sdl.odata.unmarshaller.atom.ODataAtomParser
import com.sdl.odata.unmarshaller.json.ODataJsonParser
import com.sdl.odata.service.protocol.{BatchOperation, BatchOperationResult}
import com.sdl.odata.service.spring.ActorProducer
import com.sdl.odata.service.util.AkkaUtil._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * OData Batch Processor Actor used for processing batch operations.
 */
@Component
@Scope("prototype")
class ODataBatchProcessorActor @Autowired()(actorProducer: ActorProducer, dataSourceFactory: DataSourceFactory,
                                            oDataQueryProcessor: ODataQueryProcessor) extends ODataActor {

  val ContentTypeHeader = "Content-Type"
  val BatchRequestContentTypePrefix = "multipart/mixed"
  val ContentTypeBoundaryParam = "boundary="

  override def receive: Receive = {
    case BatchOperation(actorContext, data) =>
      log.debug("Started processing OData Batch request")
      checkBatchRequestHeaders(actorContext.requestContext)
      val results: util.List[ProcessorResult] = processBatchOperation(actorContext.requestContext, data.get).asJava
      log.debug("OData Batch request execution complete")

      routeMessage(actorProducer, context, BatchOperationResult(actorContext, results.asScala.toList))
  }

  private def checkBatchRequestHeaders(oDataRequestContext: ODataRequestContext) = {
    def validContentTypeForBatch(contentTypeValue: String): Boolean = {
      val contentTypeSplit = contentTypeValue.split("\\s*;")
      contentTypeSplit.size == 2 && contentTypeSplit(0).trim.equals(BatchRequestContentTypePrefix) && contentTypeSplit(1).trim.startsWith(ContentTypeBoundaryParam)
    }
    val contentTypeHeader = oDataRequestContext.getRequest.getHeader("Content-Type")
    if (contentTypeHeader == null || !validContentTypeForBatch(contentTypeHeader)) {
      throw new ODataBatchParseException("Batch request must contain Content-Type header with value like multipart/mixed;boundary=XXX")
    }
  }

  private def processBatchOperation(oDataRequestContext: ODataRequestContext,
                                   oDataBatchRequestContent: ODataBatchRequestContent): mutable.MutableList[ProcessorResult] = {
    val results: mutable.MutableList[ProcessorResult] = mutable.MutableList()
    oDataBatchRequestContent.requestComponents.foreach {
      case BatchRequestComponent(requestComponentHeaders: BatchRequestHeaders, requestDetails: Map[String, String]) =>
        results += handleBatchRequestComponent(requestComponentHeaders, requestDetails)
      case ChangeSetRequestComponent(changeSetHeaders: BatchRequestHeaders, changeSetRequests: List[BatchRequestComponent], changesetId: String) =>
        results ++= handleChangeSetRequestComponent(changeSetHeaders, changeSetRequests, changesetId)
    }

    def handleBatchRequestComponent(requestComponentHeaders: BatchRequestHeaders, requestDetails: Map[String,String]): ProcessorResult = {
      val queryRequestContext = createODataRequestContext(requestDetails, requestComponentHeaders)
      val queryResult = oDataQueryProcessor.query(queryRequestContext, null)
      new ProcessorResult(queryResult.getStatus, queryResult.getQueryResult, queryResult.getHeaders, queryRequestContext)
    }

    def handleChangeSetRequestComponent(changeSetHeaders: BatchRequestHeaders,
                                        changeSetRequests: List[BatchRequestComponent],
                                        changeSetId: String): List[ProcessorResult] = {
      val changeSetEntities: List[ChangeSetEntity] = changeSetRequests.map((requestComponent: BatchRequestComponent) => {
        val componentRequestContext: ODataRequestContext = createODataRequestContext(requestComponent.getRequestDetails(), requestComponent.getHeaders())
        new ChangeSetEntity(
          changeSetId,
          componentRequestContext,
          if (componentRequestContext.getRequest.getMethod == Method.DELETE) null
          else getParsedBatchRequestComponentEntity(componentRequestContext))
      })
      new BatchMethodHandler(oDataRequestContext, dataSourceFactory, changeSetEntities.asJava).handleWrite().asScala.toList
    }

    def getParsedBatchRequestComponentEntity(requestContext: ODataRequestContext): Any = {
      requestContext.getRequest.getHeader("Content-Type") match {
        case ct if ct.contains("application/json") => new ODataJsonParser(requestContext, new ODataParserImpl).getODataEntity
        case ct if ct.contains("application/atom") => new ODataAtomParser(requestContext, new ODataParserImpl).getODataEntity
        case _ => throw new ODataBatchParseException("Content-Type Header needs to be specified for PUT, POST, " +
          "PATCH operations")
      }
    }

    def createODataRequestContext(requestDetails: Map[String,String], batchRequestHeaders: BatchRequestHeaders): ODataRequestContext = {
      val oDataRequest: ODataRequest = createODataRequest(requestDetails, batchRequestHeaders)
      new ODataRequestContext(oDataRequest, createODataUri(oDataRequest.getUri), oDataRequestContext.getEntityDataModel)
    }

    def createODataUri(relativeUrl: String): ODataUri = {
      new ODataUriParser(oDataRequestContext.getEntityDataModel).parseUri(relativeUrl)
    }

    def createODataRequest(requestDetails: Map[String, String], batchRequestHeaders: BatchRequestHeaders): ODataRequest = {
      val oDataRequestBuilder: ODataRequest.Builder = new ODataRequest.Builder()

      // Request type is mandatory
      requestDetails.get("RequestType") match {
        case Some(method) => oDataRequestBuilder.setMethod(Method.valueOf(method))
        case None => throw new ODataBadRequestException("No method specified for batch request")
      }

      // Request uri is mandatory
      val uri = requestDetails.getOrElse("RelativePath", "") +
        requestDetails.getOrElse("RequestEntity", "")
      if (uri.isEmpty) {
        throw new ODataBadRequestException("Uri should be specified for batch request")
      }
      val hostFromRequestHeader = oDataRequestContext.getRequest.getHeader("Host")
      val hostUri = requestDetails.getOrElse("RequestHost",
        batchRequestHeaders.headers.getOrElse("Host",
          if (hostFromRequestHeader.endsWith(".svc")) hostFromRequestHeader else oDataRequestContext.getUri.serviceRoot))
      oDataRequestBuilder.setUri(hostUri + (if (uri.startsWith("/")) uri else "/" + uri))

      // Setting request body
      val requestBody: Option[String] = requestDetails.get("RequestBody")
      if (requestBody.isDefined) {
        oDataRequestBuilder.setBodyText(requestBody.get, "UTF-8")
      }

      // Setting content type
      val contentType: Option[String] = requestDetails.get("Content-Type")
      if (contentType.isDefined) {
        oDataRequestBuilder.setContentType(MediaType.fromString(contentType.get))
        oDataRequestBuilder.setAccept(MediaType.fromString(contentType.get))
      }
      oDataRequestBuilder.setHeaders(mapAsJavaMap(batchRequestHeaders.headers))
      oDataRequestBuilder.build()
    }
    results
  }
}
