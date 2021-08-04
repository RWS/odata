/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.service

import com.sdl.odata.api.ODataUnsupportedMediaTypeException
import com.sdl.odata.api.parser._
import ODataUriUtil.getFormatOption
import MediaType.{ATOM_XML, JSON, XML}
import ODataRequest.Method.{DELETE, GET, PATCH, POST, PUT}

/**
 * Utility functions to extract useful information from the `OData Request Context`.
 */
object ODataRequestContextUtil {

  private val supportedMediaTypes = List(ATOM_XML, XML, JSON)
  private val supportedMetadataMediaTypes = List(XML)

  /**
   * Check in the 'Accept' HTTP header and '$format' query parameter if the specified type(s) are supported.
   *
   * @param requestContext The request context containing the 'Accept' HTTP header and '$format' query parameter.
   * @throws ODataUnsupportedMediaTypeException if the specified type(s) are not supported
   */
  def checkSupportedType(requestContext: ODataRequestContext) {
    if (isMediaTypeSpecified(requestContext) && !isMediaTypesSupported(requestContext)) {
      throw new ODataUnsupportedMediaTypeException("Unsupported media type")
    }
  }

  /**
   * Check if at least one media type is specified either in the 'Accept' HTTP header or '$format' query parameter.
   *
   * @param requestContext The request context containing the 'Accept' HTTP header and '$format' query parameter.
   * @return 'true' if it is specified, 'false' otherwise.
   */
  def isMediaTypeSpecified(requestContext: ODataRequestContext): Boolean = {
    requestContext.getRequest.getAccept.size() > 0 || getFormatOption(requestContext.getUri).isDefined
  }

  /**
   * Check if the media type specified either in the 'Accept' HTTP header or in the '$format' query parameter is
   * supported.
   *
   * @param context The request context containing the 'Accept' HTTP header and the '$format' query parameter.
   * @return 'true' if it is supported, 'false' otherwise.
   */
  def isMediaTypesSupported(context: ODataRequestContext): Boolean = {
    context.getUri match {
      case ODataUri(_, MetadataUri(_, _)) =>
        isMetadataMediaTypesSupported(context.getRequest) || isMetadataMediaTypesSupported(context.getUri)
      case ODataUri(_, ServiceRootUri(_)) =>
        isMediaTypesSupported(context.getRequest) || isMediaTypesSupported(context.getUri)
      case ODataUri(_, EntityUri(_, _)) =>
        isMediaTypesSupported(context.getRequest) || isMediaTypesSupported(context.getUri)
      case ODataUri(_, ResourcePathUri(_, _)) =>
        isMediaTypesSupported(context.getRequest) || isMediaTypesSupported(context.getUri)
      case ODataUri(_, BatchUri) =>
        isMediaTypesSupported(context.getRequest) || isMediaTypesSupported(context.getUri)
      case _ =>
        throw new UnsupportedOperationException(s"No implementation for $context.getUri")
    }
  }

  /**
   * Check if the media type specified in the 'Accept' HTTP header is supported.
   *
   * @param request The request containing the 'Accept' HTTP header.
   * @return 'true' if it is supported, 'false' otherwise.
   */
  def isMediaTypesSupported(request: ODataRequest): Boolean = {
    import scala.collection.JavaConverters._
    request.getAccept.asScala.exists(mediaType => supportedMediaTypes.exists(_.matches(mediaType)))
  }

  /**
   * Check if the media type specified in the '$format' query parameter is supported.
   *
   * @param oDataUri The OData URI containing the '$format' query parameter.
   * @return 'true' if it is supported, 'false' otherwise.
   */
  def isMediaTypesSupported(oDataUri: ODataUri): Boolean = getFormatOption(oDataUri) match {
    case Some(formatOpt) => supportedMediaTypes.contains(formatOpt.mediaType)
    case _ => false
  }

  /**
   * Check if the media type specified in the 'Accept' HTTP header is supported for the $metadata document.
   *
   * @param request The request containing the 'Accept' HTTP header.
   * @return 'true' if it is supported, 'false' otherwise.
   */
  def isMetadataMediaTypesSupported(request: ODataRequest): Boolean = {
    import scala.collection.JavaConverters._
    request.getAccept.asScala.exists(mediaType => supportedMetadataMediaTypes.exists(_.matches(mediaType)))
  }

  /**
   * Check if the media type specified in the '$format' query parameter is supported for the $metadata document.
   *
   * @param oDataUri The OData URI containing the '$format' query parameter.
   * @return 'true' if it is supported, 'false' otherwise.
   */
  def isMetadataMediaTypesSupported(oDataUri: ODataUri): Boolean = getFormatOption(oDataUri) match {
    case Some(formatOpt) => supportedMetadataMediaTypes.contains(formatOpt.mediaType)
    case _ => false
  }

  /**
   * Check whether the OData request wrapped in the given OData request context is about a 'read operation'.
   *
   * @param context The given OData request context.
   * @return 'true' if it is a 'read operation', 'false' otherwise.
   */
  def isReadOperation(context: ODataRequestContext) =
    context.getRequest.getMethod match {
      case GET => true
      case _ => false
    }

  /**
   * Check whether the OData request wrapped in the given OData request context is about a 'write operation'.
   *
   * @param context The given OData request context.
   * @return 'true' if it is a 'write operation', 'false' otherwise.
   */
  def isWriteOperation(context: ODataRequestContext) =
    context.getRequest.getMethod match {
      case POST => true
      case PATCH => true
      case PUT => true
      case DELETE => true
      case _ => false
    }

  /**
   * Check whether the OData request wrapped in the given OData request context is about a 'batch operation'.
   *
   * @param context The given OData request context.
   * @return 'true' if it is a 'batch operation', 'false' otherwise.
   */
  def isBatchOperation(context: ODataRequestContext) : Boolean = context.getUri.relativeUri match {
    case _: BatchUri.type => true
    case _ => false

    }
}
