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
package com.sdl.odata.test

import com.sdl.odata.api.parser._
import com.sdl.odata.api.service.ODataRequest.Method
import com.sdl.odata.api.service.ODataRequest.Method.{GET, POST}
import com.sdl.odata.api.service.{MediaType, ODataRequest, ODataRequestContext}

/**
 * Object with useful tests functions.
 */
object TestUtils {

  val serviceRoot = "http://localhost:8080/odata.svc"

  /**
   * Create an OData Request Context specifying only the HTTP method.
   *
   * @param method The given HTTP method.
   * @return The created OData Request Context.
   */
  def createODataRequestContext(method: Method): ODataRequestContext = {
    new ODataRequestContext(createODataRequest(method), createODataUri(None), null)
  }

  /**
   * Create an OData Request Context specifying only the media types to include in the 'Accept' HTTP header and
   * '$format' query parameter.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData Request Context.
   */
  def createODataRequestContext(acceptMediaTypes: List[MediaType], formatMediaType: Option[MediaType]): ODataRequestContext = {
    new ODataRequestContext(createODataRequest(acceptMediaTypes), createODataUri(formatMediaType), null)
  }

  /**
   * Create an OData Request Context specifying only the media types to include in the 'Accept' HTTP header and
   * '$format' query parameter for a $metadata document request.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData Request Context.
   */
  def createODataRequestContextForMetadata(acceptMediaTypes: List[MediaType], formatMediaType: Option[MediaType]): ODataRequestContext = {
    new ODataRequestContext(createODataRequest(acceptMediaTypes), createODataUriForMetadata(formatMediaType), null)
  }

  /**
   * Create an OData Request Context specifying only the media types to include in the 'Accept' HTTP header and
   * '$format' query parameter for a Service Document request.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData Request Context.
   */
  def createODataRequestContextForServiceDocument(acceptMediaTypes: List[MediaType], formatMediaType: Option[MediaType]): ODataRequestContext = {
    new ODataRequestContext(createODataRequest(acceptMediaTypes), createODataUriForServiceDocument(formatMediaType), null)
  }

  /**
   * Create an OData Request Context specifying only the media types to include in the 'Accept' HTTP header and
   * '$format' query parameter for a $entity document request.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData Request Context.
   */
  def createODataRequestContextForEntityDocument(acceptMediaTypes: List[MediaType], formatMediaType: Option[MediaType]): ODataRequestContext = {
    new ODataRequestContext(createODataRequest(acceptMediaTypes), createODataUriForEntityDocument(formatMediaType), null)
  }

  /**
   * Create an OData Batch Request Context specifying only the media types to include in the 'Accept' HTTP header for a Batch
   * request.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @return The created OData Batch Request Context.
   */
  def createODataRequestContextForBatch(acceptMediaTypes: List[MediaType]): ODataRequestContext = {
    new ODataRequestContext(createODataBatchRequest(acceptMediaTypes), createODataUriForBatch(), null)
  }

  /**
   * Create an OData Request specifying only the media types to include in the 'Accept' HTTP header.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @return The created OData Request.
   */
  def createODataRequest(acceptMediaTypes: List[MediaType]): ODataRequest = {
    new ODataRequest.Builder()
      .setBodyText("Text", "UTF-8")
      .setMethod(GET)
      .setUri(serviceRoot)
      .setAccept(acceptMediaTypes: _*)
      .build()
  }

  /**
   * Create an OData Batch Request specifying only the media types to include in the 'Accept' HTTP header.
   *
   * @param acceptMediaTypes The media types to include in the 'Accept' HTTP header.
   * @return The created OData Batch Request.
   */
  def createODataBatchRequest(acceptMediaTypes: List[MediaType]): ODataRequest = {
    new ODataRequest.Builder()
      .setBodyText("Text", "UTF-8")
      .setMethod(POST)
      .setUri(serviceRoot)
      .setAccept(acceptMediaTypes: _*)
      .build()
  }

  /**
   * Create an OData Request specifying only the HTTP method.
   *
   * @param method The given HTTP method.
   * @return The created OData Request.
   */
  def createODataRequest(method: Method): ODataRequest = {
    new ODataRequest.Builder()
      .setBodyText("Text", "UTF-8")
      .setUri(serviceRoot)
      .setMethod(method)
      .build
  }

  /**
   * Create an OData URI specifying only the media type to include in the '$format' query parameter.
   *
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData URI.
   */
  def createODataUri(formatMediaType: Option[MediaType]): ODataUri = {
    val formatOptionList = formatMediaType match {
      case Some(format) => List(FormatOption(format))
      case None => List()
    }
    ODataUri(serviceRoot, ResourcePathUri(EntitySetPath("EntitySet", None), formatOptionList))
  }

  /**
   * Create an OData URI specifying only the media type to include in the '$format' query parameter for a $metadata
   * document request.
   *
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData URI.
   */
  def createODataUriForMetadata(formatMediaType: Option[MediaType]): ODataUri = {
    ODataUri(serviceRoot, MetadataUri(formatMediaType, None))
  }

  /**
   * Create an OData URI specifying only the media type to include in the '$format' query parameter for a Service
   * Document request.
   *
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData URI.
   */
  def createODataUriForServiceDocument(formatMediaType: Option[MediaType]): ODataUri = {
    ODataUri(serviceRoot, ServiceRootUri(formatMediaType))
  }

  /**
   * Create an OData URI specifying only the media type to include in the '$format' query parameter for a $entity
   * document request.
   *
   * @param formatMediaType Option containing the media type to include in the '$format' query parameter.
   * @return The created OData URI.
   */
  def createODataUriForEntityDocument(formatMediaType: Option[MediaType]): ODataUri = {
    formatMediaType match {
      case Some(format) => ODataUri(serviceRoot, EntityUri(None, List(FormatOption(formatMediaType.get))))
      case None => ODataUri(serviceRoot, EntityUri(None, List()))
    }
  }

  /**
   * Create an OData URI for a Batch request.
   *
   * @return The created OData URI.
   */
  def createODataUriForBatch(): ODataUri = {
    ODataUri(serviceRoot, BatchUri)
  }
}
