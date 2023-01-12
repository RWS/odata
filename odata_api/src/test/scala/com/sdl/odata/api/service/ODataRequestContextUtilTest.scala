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
package com.sdl.odata.api.service

import com.sdl.odata.api.{ODataNotImplementedException, ODataUnsupportedMediaTypeException}
import MediaType.{ATOM_XML, JSON, XML}
import ODataRequest.Method._
import ODataRequestContextUtil._
import com.sdl.odata.test.TestUtils
import TestUtils._
import org.scalatest.FunSuite

/**
 * Unit tests for 'ODataRequestContextUtil'.
 */
class ODataRequestContextUtilTest extends FunSuite {

  val textPlainType = MediaType.fromString("text/plain")

  test("Check that the media type specified in the Request Context is supported") {
    checkSupportedType(createODataRequestContext(List(), None))
    checkSupportedType(createODataRequestContext(List(), Option(ATOM_XML)))
    checkSupportedType(createODataRequestContext(List(), Option(XML)))
    checkSupportedType(createODataRequestContext(List(), Option(JSON)))
    checkSupportedType(createODataRequestContext(List(ATOM_XML), None))
    checkSupportedType(createODataRequestContext(List(XML), None))
    checkSupportedType(createODataRequestContext(List(JSON), None))
    checkSupportedType(createODataRequestContext(List(ATOM_XML, JSON), None))
    checkSupportedType(createODataRequestContext(List(ATOM_XML), Option(JSON)))
  }

  test("Check that the media type specified in the Request Context is not supported") {
    assertUnsupportedMediaTypes(createODataRequestContext(List(), Option(textPlainType)))
    assertUnsupportedMediaTypes(createODataRequestContext(List(textPlainType), None))
    assertUnsupportedMediaTypes(createODataRequestContext(List(textPlainType), Option(textPlainType)))
  }

  def assertUnsupportedMediaTypes(requestContext: ODataRequestContext) {
    intercept[ODataUnsupportedMediaTypeException] {
      checkSupportedType(requestContext)
    }
  }

  test("Check that at least one media type is specified in the Request Context") {
    assert(isMediaTypeSpecified(createODataRequestContext(List(JSON, XML), Option(ATOM_XML))))
    assert(isMediaTypeSpecified(createODataRequestContext(List(), Option(ATOM_XML))))
    assert(isMediaTypeSpecified(createODataRequestContext(List(JSON), None)))
    assert(isMediaTypeSpecified(createODataRequestContext(List(JSON, textPlainType), Option(textPlainType))))
    assert(!isMediaTypeSpecified(createODataRequestContext(List(), None)))
  }

  test("Check that the media type in the Request Context is supported") {
    assert(isMediaTypesSupported(createODataRequestContext(List(JSON, XML), Option(ATOM_XML))))
    assert(isMediaTypesSupported(createODataRequestContext(List(), Option(ATOM_XML))))
    assert(isMediaTypesSupported(createODataRequestContext(List(JSON), None)))
    assert(isMediaTypesSupported(createODataRequestContext(List(JSON, textPlainType), Option(textPlainType))))
    assert(!isMediaTypesSupported(createODataRequestContext(List(), None)))
  }

  test("Check that the media type in the 'Accept' HTTP header is supported") {
    assert(isMediaTypesSupported(createODataRequest(List(XML))))
    assert(isMediaTypesSupported(createODataRequest(List(ATOM_XML, JSON))))
    assert(isMediaTypesSupported(createODataRequest(List(ATOM_XML, textPlainType))))
    assert(!isMediaTypesSupported(createODataRequest(List())))
    assert(!isMediaTypesSupported(createODataRequest(List(textPlainType))))
  }

  test("Check that the media type in the '$format' query parameter is supported") {
    assert(isMediaTypesSupported(createODataUri(Some(ATOM_XML))))
    assert(isMediaTypesSupported(createODataUri(Some(XML))))
    assert(isMediaTypesSupported(createODataUri(Some(JSON))))
    assert(!isMediaTypesSupported(createODataUri(Some(textPlainType))))
    assert(!isMediaTypesSupported(createODataUri(None)))
  }

  test("Check that the media type in the Request Context is supported for a $metadata document") {
    assert(isMediaTypesSupported(createODataRequestContextForMetadata(List(XML), Option(XML))))
    assert(isMediaTypesSupported(createODataRequestContextForMetadata(List(), Option(XML))))
    assert(isMediaTypesSupported(createODataRequestContextForMetadata(List(XML), None)))
    assert(isMediaTypesSupported(createODataRequestContextForMetadata(List(XML, textPlainType), Option(textPlainType))))
    assert(!isMediaTypesSupported(createODataRequestContextForMetadata(List(), None)))
  }

  test("Check that the media type in the '$format' query parameter is supported for a $metadata document") {
    assert(isMetadataMediaTypesSupported(createODataUriForMetadata(Some(XML))))
    assert(!isMetadataMediaTypesSupported(createODataUriForMetadata(Some(textPlainType))))
    assert(!isMetadataMediaTypesSupported(createODataUriForMetadata(None)))
  }

  test("Check that the media type in the Request Context is supported for a Service Document") {
    assert(isMediaTypesSupported(createODataRequestContextForServiceDocument(List(JSON, XML), Option(ATOM_XML))))
    assert(isMediaTypesSupported(createODataRequestContextForServiceDocument(List(), Option(ATOM_XML))))
    assert(isMediaTypesSupported(createODataRequestContextForServiceDocument(List(JSON), None)))
    assert(isMediaTypesSupported(createODataRequestContextForServiceDocument(List(JSON, textPlainType), Option(textPlainType))))
    assert(!isMediaTypesSupported(createODataRequestContextForServiceDocument(List(), None)))
  }

  test("Check that the media type in the '$format' query parameter is supported for a Service Document") {
    assert(isMediaTypesSupported(createODataUriForServiceDocument(Some(ATOM_XML))))
    assert(isMediaTypesSupported(createODataUriForServiceDocument(Some(XML))))
    assert(isMediaTypesSupported(createODataUriForServiceDocument(Some(JSON))))
    assert(!isMediaTypesSupported(createODataUriForServiceDocument(Some(textPlainType))))
    assert(!isMediaTypesSupported(createODataUriForServiceDocument(None)))
  }

  test("Check that the media type in the Request Context is supported for a $entity document request") {
    assert(isMediaTypesSupported(createODataRequestContextForEntityDocument(List(JSON, XML), Option(ATOM_XML))))
    assert(isMediaTypesSupported(createODataRequestContextForEntityDocument(List(), Option(ATOM_XML))))
    assert(isMediaTypesSupported(createODataRequestContextForEntityDocument(List(JSON), None)))
    assert(isMediaTypesSupported(createODataRequestContextForEntityDocument(List(JSON, textPlainType), Option(textPlainType))))
    assert(!isMediaTypesSupported(createODataRequestContextForEntityDocument(List(), None)))
  }

  test("Check that the media type in the '$format' query parameter is supported for a $entity document request") {
    assert(isMediaTypesSupported(createODataUriForEntityDocument(Some(ATOM_XML))))
    assert(isMediaTypesSupported(createODataUriForEntityDocument(Some(XML))))
    assert(isMediaTypesSupported(createODataUriForEntityDocument(Some(JSON))))
    assert(!isMediaTypesSupported(createODataUriForEntityDocument(Some(textPlainType))))
    assert(!isMediaTypesSupported(createODataUriForEntityDocument(None)))
  }

  test("Check that the media type specified in the lists are supported in a batch request.") {
    assert(isMediaTypesSupported(createODataRequestContextForBatch(List(JSON, XML))))
    assert(!isMediaTypesSupported(createODataRequestContextForBatch(List())))
  }

  def assertNotImplemented(requestContext: ODataRequestContext) {
    intercept[ODataNotImplementedException] {
      isMediaTypesSupported(requestContext)
    }
  }

  test("Check function 'isReadOperation'") {
    assert(isReadOperation(createODataRequestContext(GET)))
    assert(!isReadOperation(createODataRequestContext(POST)))
    assert(!isReadOperation(createODataRequestContext(PUT)))
    assert(!isReadOperation(createODataRequestContext(PATCH)))
    assert(!isReadOperation(createODataRequestContext(DELETE)))
  }

  test("Check function 'isWriteOperation'") {
    assert(!isWriteOperation(createODataRequestContext(GET)))
    assert(isWriteOperation(createODataRequestContext(POST)))
    assert(isWriteOperation(createODataRequestContext(PUT)))
    assert(isWriteOperation(createODataRequestContext(PATCH)))
    assert(isWriteOperation(createODataRequestContext(DELETE)))
  }

  test("Check function 'isBatchOperation'") {
    assert(isBatchOperation(createODataRequestContextForBatch(List(JSON, XML))))
    assert(!isBatchOperation(createODataRequestContext(PUT)))
  }
}
