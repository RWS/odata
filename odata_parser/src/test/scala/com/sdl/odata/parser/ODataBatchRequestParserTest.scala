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
package com.sdl.odata.parser

import com.sdl.odata.api.parser.ODataBatchParseException
import org.scalatest.FunSuite

import scala.io.Source

/**
 * Tests for ODataBatchRequestParser.
 */
class ODataBatchRequestParserTest extends FunSuite {

  val batchRequestParser = new ODataBatchRequestParser
  val newLine = sys.props("line.separator")
  
  test("Batch request with only individual request") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
      newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"
    
    implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])
    
    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]
    
    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))
    
    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getRequestDetails().get("RequestEntity").get == "Customers('ALFKI')")

    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "localhost")
    
  }
  
  test("Batch request with individual requests and change sets") {
    val fileContents = Source.fromURL(getClass.getResource("/BatchRequestSample1.txt")).mkString
    implicit val parsedContent = batchRequestParser.parseBatch(fileContents)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 4)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])
    assert(parsedContent.requestComponents(1).isInstanceOf[ChangeSetRequestComponent])
    assert(parsedContent.requestComponents(2).isInstanceOf[BatchRequestComponent])

    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getHeaders().headerType == IndividualRequestHeader)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getHeaders().headers.contains("Host"))
    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "host")

    val changesetRequestComponent = parsedContent.requestComponents(1).asInstanceOf[ChangeSetRequestComponent]

    assert(changesetRequestComponent.getHeaders() != null)
    assert(changesetRequestComponent.getHeaders().headers.size == 0)
    assert(changesetRequestComponent.getHeaders().headerType == ChangeSetRequestHeader)

    assert(changesetRequestComponent.changesetRequests != null)

    assert(changesetRequestComponent.changesetRequests.size == 2)

    val changeSetOperation1 = changesetRequestComponent.changesetRequests(0)

    assert(changeSetOperation1.requestComponentHeaders != null)
    assert(changeSetOperation1.requestComponentHeaders.headers.size == 5)
    assert(changeSetOperation1.requestComponentHeaders.headerType == IndividualRequestHeader)

    val changeSetOperation1Headers = changeSetOperation1.requestComponentHeaders.headers

    assert(changeSetOperation1Headers.contains("Content-Length") && changeSetOperation1Headers.get("Content-Length").get.equals("123123"))
    assert(changeSetOperation1Headers.contains("Content-ID") && changeSetOperation1Headers.get("Content-ID").get.equals("1"))

    assert(changeSetOperation1.requestDetails != null)
    assert(changeSetOperation1.requestDetails.get("RequestType").get == "POST")
    assert(changeSetOperation1.requestDetails.get("RequestEntity").get == "Customers")
    assert(changeSetOperation1.requestDetails.get("RelativePath").get == "/service/")
    assert(changeSetOperation1.requestDetails.get("RequestBody").get == "<?xml AtomPub representation of a new Customer>")

    val changeSetOperation2 = changesetRequestComponent.changesetRequests(1)

    assert(changeSetOperation2.requestComponentHeaders != null)
    assert(changeSetOperation2.requestComponentHeaders.headers.size == 6)
    assert(changeSetOperation2.requestComponentHeaders.headerType == IndividualRequestHeader)

    val changeSetOperation2Headers = changeSetOperation2.requestComponentHeaders.headers

    assert(changeSetOperation2Headers.contains("Content-Length") && changeSetOperation2Headers.get("Content-Length").get.equals("12334"))
    assert(changeSetOperation2Headers.contains("Content-ID") && changeSetOperation2Headers.get("Content-ID").get.equals("2"))

    assert(changeSetOperation2.requestDetails != null)
    assert(changeSetOperation2.requestDetails.get("RequestType").get == "PATCH")
    assert(changeSetOperation2.requestDetails.get("RequestEntity").get == "Orders")
    assert(changeSetOperation2.requestDetails.get("ContentId").get == "$1/")
    assert(changeSetOperation2.requestDetails.get("RequestBody").get == "{JSON representation of Customer ALFKI}")

    val indvRequestComponent2 = parsedContent.requestComponents(2).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent2.getHeaders() != null)
    assert(indvRequestComponent2.getHeaders().headerType == IndividualRequestHeader)
    assert(indvRequestComponent2.getRequestDetails() != null)
    assert(indvRequestComponent2.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent2.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent2.getRequestDetails().get("RequestEntity").get == "Products(1)")
    assert(indvRequestComponent2.getRequestDetails().get("RelativePath").get == "/service/")

    assert(indvRequestComponent2.getHeaders().headers.size == 2)
  }

  test("Batch request without line separator between individual request headers and request URI") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      "GET /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
      newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"

    val exception = intercept[ODataBatchParseException] {
      implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)
    }

    assert(exception != null)
  }

  test("Batch request without line separator between GET request URI and end of request component") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
       newLine +
      "GET /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"
    val exception = intercept[ODataBatchParseException] {
      implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)
    }

    assert(exception != null)
  }

  test("Batch request with only one line separator between GET request URI and end of request component") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
       newLine +
      "GET /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
       newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"
    val exception = intercept[ODataBatchParseException] {
      implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)
    }

    assert(exception != null)
    assert(exception.getMessage == "Request body must contain an empty line or entity data.")
  }

  test("Batch request with more than two line separator between GET request URI and end of request component") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
       newLine +
      "GET /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
       newLine +
       newLine +
       newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"
    val exception = intercept[ODataBatchParseException] {
      implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)
    }

    assert(exception != null)
  }

  test("Batch individual request with POST (Not supported in current implementation") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
       newLine +
      "POST /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
       newLine +
       newLine +
      "{some data]" +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"
    val exception = intercept[ODataBatchParseException] {
      implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)
    }

    assert(exception != null)
    assert(exception.getMessage.startsWith("Only GET is supported in Individual requests of batch."))
  }

  test("Batch request with space after request URL") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET http://cdwstest.ams.dev:7071/discovery-service/discovery.svc/Environment  " + newLine +
      "Host: http://cdwstest.ams.dev:7071/discovery-service/discovery.svc" + newLine +
      newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"

    implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])

    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getRequestDetails().get("RequestEntity").get == "Environment")
    assert(indvRequestComponent.getRequestDetails().get("RequestHost").get =="http://cdwstest.ams.dev:7071")
  }

  test("Batch request with space after request URL2") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET Environment HTTP/1.1   " + newLine +
      "Host: http://cdwstest.ams.dev:7071/discovery-service/discovery.svc" + newLine +
      newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"

    implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])

    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getRequestDetails().get("RequestEntity").get == "Environment")

    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "http://cdwstest.ams.dev:7071/discovery-service/discovery.svc")
  }

  test("Batch request with space after request multipart/mixed;") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET Environment HTTP/1.1   " + newLine +
      "Host: http://cdwstest.ams.dev:7071/discovery-service/discovery.svc" + newLine +
      newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"

    implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])

    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getRequestDetails().get("RequestEntity").get == "Environment")

    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "http://cdwstest.ams.dev:7071/discovery-service/discovery.svc")
  }

  test("Input whitespace trimming") {
    val requestBodyWithTrailingWhitespaces = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b   " + newLine +
      "headers and batch content   " + newLine +
      "   " + newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--   "
    val requestBodyWithoutTrailingWhitespaces = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "headers and batch content" + newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"

    assert(batchRequestParser.trimRedundantTrailingSpaces(requestBodyWithTrailingWhitespaces) ==
      requestBodyWithoutTrailingWhitespaces)
  }

  test("Batch request with no host defined with resource path only") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET Environment HTTP/1.1" + newLine +
      newLine +
      newLine +
      "--batch_36522ad7-fc75-4b56-8c71-56071383e77b--"

    val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])

    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getRequestDetails().get("RequestEntity").get == "Environment")
  }

  test("Batch request with different batch prefix") {
    val testBatchRequestBody = "--b_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET /service/Customers('ALFKI')" + newLine +
      "Host: localhost" + newLine +
      newLine +
      newLine +
      "--b_36522ad7-fc75-4b56-8c71-56071383e77b--"

    implicit val parsedContent = batchRequestParser.parseBatch(testBatchRequestBody)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[BatchRequestComponent])

    val indvRequestComponent = parsedContent.requestComponents(0).asInstanceOf[BatchRequestComponent]

    assert(indvRequestComponent.getHeaders() != null)
    assert(indvRequestComponent.getRequestDetails() != null)
    assert(indvRequestComponent.getRequestDetails().contains("RequestType"))

    assert(indvRequestComponent.getRequestDetails().get("RequestType").get == "GET")
    assert(indvRequestComponent.getRequestDetails().get("RequestEntity").get == "Customers('ALFKI')")

    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "localhost")

  }
}
