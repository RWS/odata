/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
import org.junit.Assert.assertEquals
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
      "GET http://127.0.0.1:8082/discovery-service/odata.svc/Environment  " + newLine +
      "Host: http://127.0.0.1:8082/discovery-service/odata.svc" + newLine +
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
    assert(indvRequestComponent.getRequestDetails().get("RequestHost").get =="http://127.0.0.1:8082")
  }

  test("Batch request with space after request URL2") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET Environment HTTP/1.1   " + newLine +
      "Host: http://localhost:8082/discovery-service/odata.svc" + newLine +
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

    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "http://localhost:8082/discovery-service/odata.svc")
  }

  test("Batch request with space after request multipart/mixed;") {
    val testBatchRequestBody = "--batch_36522ad7-fc75-4b56-8c71-56071383e77b" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding:binary" + newLine +
      newLine +
      "GET Environment HTTP/1.1   " + newLine +
      "Host: http://localhost:8082/discovery-service/odata.svc" + newLine +
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

    assert(indvRequestComponent.getHeaders().headers.get("Host").get == "http://localhost:8082/discovery-service/odata.svc")
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

  test("CRQ-1019: Batch request with trailing new line whitespaces and trailing request new lines with whitespaces") {
    val whiteSpacesBatchSource = "--batch_7aa6777a-f7f2-4a45-89ee-a3b72464f51d\n" +
      "Content-Type: multipart/mixed; boundary=changeset_05118c88-8f0b-43da-be4d-c326e0133670\n " +
      "\n" +
      "--changeset_05118c88-8f0b-43da-be4d-c326e0133670\n" +
      "Content-Type: application/http\n" +
      "Content-Transfer-Encoding: binary\n" +
      "Content-ID: 1\n " +
      "\n" +
      "POST http://localhost:8082/odata.svc/WebApplications HTTP/1.1\n" +
      "OData-Version: 4.0\n" +
      "OData-MaxVersion: 4.0\n" +
      "Content-Type: application/json;odata.metadata=minimal\n" +
      "Accept: application/json;odata.metadata=minimal\n" +
      "Accept-Charset: UTF-8\n" +
      "User-Agent: Microsoft ADO.NET Data Services\n " +
      "\n" +
      "{\"@odata.type\":\"#Example.Application.Platform.Entity\",\"id\":\"Website2_RootWebApp\",\"ContextURL\":\"/\",\"BaseURLs@odata.type\":\"#Collection(Example.Application.Platform.Entity)\",\"BaseURLs\":[{\"@odata.type\":\"#Example.Application.Platform.Entity\",\"Protocol\":\"http\",\"Host\":\"localhost\",\"Port\":\"8080\"}],\"ExtensionProperties@odata.type\":\"#Collection(Example.Application.Platform.Entity)\",\"ExtensionProperties\":[],\"WebCapability@odata.bind\":\"http://localhost:8082/odata.svc/WebCapabilities('DefaultWeb')\"}\n \n--changeset_05118c88-8f0b-43da-be4d-c326e0133670--\n--batch_7aa6777a-f7f2-4a45-89ee-a3b72464f51d--\n " +
      "\n "
    val noWhiteSpacesBatchSource = "--batch_7aa6777a-f7f2-4a45-89ee-a3b72464f51d\n" +
      "Content-Type: multipart/mixed; boundary=changeset_05118c88-8f0b-43da-be4d-c326e0133670\n " +
      "\n" +
      "--changeset_05118c88-8f0b-43da-be4d-c326e0133670\n" +
      "Content-Type: application/http\n" +
      "Content-Transfer-Encoding: binary\n" +
      "Content-ID: 1\n " +
      "\n" +
      "POST http://localhost:8082/odata.svc/WebApplications HTTP/1.1\n" +
      "OData-Version: 4.0\n" +
      "OData-MaxVersion: 4.0\n" +
      "Content-Type: application/json;odata.metadata=minimal\n" +
      "Accept: application/json;odata.metadata=minimal\n" +
      "Accept-Charset: UTF-8\n" +
      "User-Agent: Microsoft ADO.NET Data Services\n " +
      "\n" +
      "{\"@odata.type\":\"#Example.Application.Platform.Entity\",\"id\":\"Website2_RootWebApp\",\"ContextURL\":\"/\",\"BaseURLs@odata.type\":\"#Collection(Example.Application.Platform.Entity)\",\"BaseURLs\":[{\"@odata.type\":\"#Example.Application.Platform.Entity\",\"Protocol\":\"http\",\"Host\":\"localhost\",\"Port\":\"8080\"}],\"ExtensionProperties@odata.type\":\"#Collection(Example.Application.Platform.Entity)\",\"ExtensionProperties\":[],\"WebCapability@odata.bind\":\"http://localhost:8082/odata.svc/WebCapabilities('DefaultWeb')\"}\n \n--changeset_05118c88-8f0b-43da-be4d-c326e0133670--\n--batch_7aa6777a-f7f2-4a45-89ee-a3b72464f51d--\n" +
      "\n"

    val whiteSpacesResult = batchRequestParser.trimRedundantTrailingSpaces(whiteSpacesBatchSource)
    val noWhiteSpacesResult = batchRequestParser.trimRedundantTrailingSpaces(noWhiteSpacesBatchSource)

    assert(whiteSpacesResult != null)
    assert(noWhiteSpacesResult != null)
    assertEquals(whiteSpacesResult, noWhiteSpacesResult)
  }

  test("Batch request with full urls") {
    val fileContents = "--batch_1f5bbc13-ac60-458e-988f-18c4a8c09cae" + newLine +
      "Content-Type: multipart/mixed; boundary=changeset_926a7f6a-5307-4ce7-91ad-397ff2f83ff5" + newLine + newLine +
      "--changeset_926a7f6a-5307-4ce7-91ad-397ff2f83ff5" + newLine +
      "Content-Type: application/http" + newLine +
      "Content-Transfer-Encoding: binary" + newLine +
      "Content-ID: 666" + newLine + newLine +
      "POST https://secure-host/service.root/applications HTTP/1.1" + newLine +
      "OData-Version: 4.0" + newLine +
      "OData-MaxVersion: 4.0" + newLine +
      "Content-Type: application/json;odata.metadata=minimal" + newLine +
      "Accept: application/json;odata.metadata=minimal" + newLine +
      "User-Agent: Microsoft ADO.NET Data Services" + newLine + newLine +
      "{ \"some\" : \"content\" }" + newLine + "" + newLine +
      "--changeset_926a7f6a-5307-4ce7-91ad-397ff2f83ff5--" + newLine +
      "--batch_1f5bbc13-ac60-458e-988f-18c4a8c09cae--"

    implicit val parsedContent = batchRequestParser.parseBatch(fileContents)

    assert(parsedContent != null)
    assert(parsedContent.requestComponents != null)
    assert(parsedContent.requestComponents.size == 1)

    assert(parsedContent.requestComponents(0).isInstanceOf[ChangeSetRequestComponent])


    val changesetRequestComponent = parsedContent.requestComponents(0).asInstanceOf[ChangeSetRequestComponent]

    assert(changesetRequestComponent.getHeaders() != null)
    assert(changesetRequestComponent.getHeaders().headers.isEmpty)

    assert(changesetRequestComponent.changesetRequests != null)

    assert(changesetRequestComponent.changesetRequests.size == 1)

    val changeSetOperation1 = changesetRequestComponent.changesetRequests(0)

    assert(changeSetOperation1.requestComponentHeaders != null)
    assert(changeSetOperation1.requestComponentHeaders.headers.size == 7)
    assert(changeSetOperation1.requestComponentHeaders.headerType == IndividualRequestHeader)

    val changeSetOperation1Headers = changeSetOperation1.requestComponentHeaders.headers
    assert(changeSetOperation1Headers.contains("Content-ID") && changeSetOperation1Headers.get("Content-ID").get.equals("666"))

    assert(changeSetOperation1.requestDetails != null)
    assert(changeSetOperation1.requestDetails.get("RequestType").get == "POST")
    assert(changeSetOperation1.requestDetails.get("RequestEntity").get == "applications")
    assert(changeSetOperation1.requestDetails.get("RelativePath").get == "/service.root/")
    assert(changeSetOperation1.requestDetails.get("RequestHost").get == "https://secure-host")
    assert(changeSetOperation1.requestDetails.get("RequestBody").get == "{ \"some\" : \"content\" }")
  }
}
