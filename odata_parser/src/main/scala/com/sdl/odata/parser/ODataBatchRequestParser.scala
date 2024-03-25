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
package com.sdl.odata.parser

import com.sdl.odata.api.parser.ODataBatchParseException

import scala.util.parsing.combinator.RegexParsers

/**
 * ODataBatchRequestParser.
 * Batch Request Structure expected
 * Batch Main Header
 *    - List of Request Contents (1 or more) - Each request content can be individual requests or a change set
 *       - Individual Requests
 *          - Headers
 *          - Request details
 *       - ChangeSets
 *            - Headers
 *            - List<Individual Requests>
 */
class ODataBatchRequestParser extends RegexParsers {
  // Note that the current implementation does not support the odata.continue-on-error preference
  private val lineSeparator = sys.props("line.separator")

  override def skipWhitespace = false

  var contentIds: Set[Int] = Set[Int]()
  var batchId: String = ""
  val ContentIdHeader = "Content-ID"
  val ContentTransferEncodingHeader = "Content-Transfer-Encoding"
  val ContentTransferEncodingHeaderValue = "binary"

  // Main parser class for ODataBatchRequest
  def parseBatch(input: String): ODataBatchRequestContent = parseAll(parseBatchRequest, trimRedundantTrailingSpaces(input)) match {
    case Success(result, _) => result
    case NoSuccess(msg, _) => throw new ODataBatchParseException(msg)
  }

  // Trim redundant trailing spaces and trailing request line separators
  def trimRedundantTrailingSpaces(input: String): String = {
    input.split(lineSeparator).map(_.trim).reverse.dropWhile(_.isEmpty).reverse.mkString(lineSeparator.toString)
  }

  def parseBatchRequest: Parser[ODataBatchRequestContent] = parseRequestContent ^^ {
    case requestContent => ODataBatchRequestContent(requestContent)
  }

  // Step1: Parse the request content which can contain Individual requests for change sets
  def parseRequestContent: Parser[List[ODataRequestComponent]] = rep(parseQueryOrChangeSet) <~ "--" + batchId + "--".r ^^ {
    res => {
      val requestComponents = res

      if (requestComponents.isEmpty) throw new ODataBatchParseException("Batch request is empty.")
      requestComponents
    }
  }

  // Step2: check if the request content is an individual query or changeset and parse accordingly
  def parseQueryOrChangeSet: Parser[ODataRequestComponent] = parserBatchId ~> chooseRequest

  def parserBatchId =  "--.+_.+\r*\n".r ^^ {
    case parsedBatchId => {
      batchId = parsedBatchId.replaceFirst("--","").trim
    }

  }
  //application -> Query Request , multipart -> changeSet Request
  def chooseRequest: Parser[ODataRequestComponent] =
    ("Content-Type:\\s*application/.+\r*\n".r ~> parseIndividualBatchRequest) |
      ("Content-Type:\\s*multipart/mixed;\\s*boundary=".r ~> parseChangeSetRequestComponent)

  def parseIndividualBatchRequest: Parser[BatchRequestComponent] = parseRequestComponent ^^ {
    case requestComponent => {
      if (!requestComponent.getRequestDetails().get("RequestType").get.equals("GET")) throw new ODataBatchParseException("Only GET is supported in Individual requests of batch.")
      requestComponent
    }
  }

  // Step2a: If the content is individual request, extract data to BatchRequestComponent
  def parseRequestComponent: Parser[BatchRequestComponent] = parseIndividualRequestTopHeaders ~ getRequestURIComponents ~ parseQueryRequestHeaders into {
    case headerMap ~ requestContent ~ queryRequestHeaders =>
      (if (queryRequestHeaders.isEmpty) parseQueryBody else lineSeparator ~> parseQueryBody) ^^ {
        case requestBody => BatchRequestComponent(BatchRequestHeaders(headerMap ++ queryRequestHeaders, IndividualRequestHeader), requestContent ++ requestBody)
      }
  }

  // Step2b: If the content is a change set, extract data to ChangeSetRequestContent
  def parseChangeSetRequestComponent: Parser[ChangeSetRequestComponent] = parseChangeSetId ~ parseChangeSetHeader ~ parseChangeSetComponents <~ ("--.+_.+--" + lineSeparator).r ^^ {
    case  changesetId ~ header ~ res => ChangeSetRequestComponent(BatchRequestHeaders(header, ChangeSetRequestHeader), res, changesetId)
  }

  def parseChangeSetId: Parser[String] = ".+\r*\n".r ^^ { case changesetId => changesetId.trim}

  // Parse and validate top level headers of Individual Request
  def parseIndividualRequestTopHeaders: Parser[Map[String, String]] = parseHeaders <~ lineSeparator ^^ {
    case headerMap => {
      if (headerMap.isEmpty || !headerMap.contains(ContentTransferEncodingHeader)) {
        throw new ODataBatchParseException("An individual request of a batch request must contain Content-Transfer-Encoding header")
      }

      if (!headerMap.get(ContentTransferEncodingHeader).get.equals(ContentTransferEncodingHeaderValue))
        throw new ODataBatchParseException("Each operation of a batch request must contain Content-Transfer-Encoding with value binary")

      headerMap
    }
  }

  //Parsing Individual request component
  // Formats that needs to be supported
  // 1. Absolute URI with schema, host, port, and absolute resource path.
  //      Ex: GET https://host:1234/path/service/People(1) HTTP/1.1
  // 2. Absolute resource path and separate Host header.
  //      Ex:   GET /path/service/People(1) HTTP/1.1
  //            Host: myserver.mydomain.org:1234
  // 3. Resource path relative to the batch request URI.
  //      Ex:   GET People(1) HTTP/1.1
  def getRequestURIComponents: Parser[Map[String, String]] = ("GET " | "POST " | "PATCH " | "PUT " | "DELETE ") ~ getRequestURI ^^ {
    case requestType ~ relativeURL => Map("RequestType" -> requestType.trim) ++ relativeURL
  }

  // TODO: Validations
  // Must not include authentication or authorization related HTTP headers and Expect, From, Max-Forwards, Range, or TE headers
  def parseQueryRequestHeaders: Parser[Map[String, String]] = parseHeaders

  def parseQueryBody: Parser[Map[String, String]] = getEntityData ^^ {
    case entityData => Map("RequestBody" -> entityData.trim)
  }

  def getEntityData: Parser[String] = ("<?xml" ~> parseAtom) | ("{" ~> parseJson) | (lineSeparator.withFailureMessage("Request body must contain an empty line or entity data.") ~> "")

  def parseAtom: Parser[String] = """(?s)((.+?)(?=--|$))""".r ^^ { case data => "<?xml" + data}

  def parseJson: Parser[String] = """(?s)((.+?)(?=--|$))""".r ^^ { case data => "{" + data}

  // Parse and validate top level headers of Change set Request
  def parseChangeSetHeader: Parser[Map[String, String]] = parseHeaders

  // Parse recursively Individual requests inside a change set
  def parseChangeSetComponents: Parser[List[BatchRequestComponent]] = rep(parseChangeSetComponent) ^^ {
    case res => res.toList
  }

  def parseChangeSetComponent: Parser[BatchRequestComponent] = ("--changeset_.+[^-]" + lineSeparator).r ~> parseRequestComponent ^^ {
    case requestComponent => {
      if (requestComponent.getRequestDetails().get("RequestType").get.equals("GET")) throw new ODataBatchParseException("ChangeSets must not contain GET requests.")
      val changeSetRequestComponentHeaders = requestComponent.getHeaders().headers

      if (!changeSetRequestComponentHeaders.contains(ContentIdHeader)) throw new ODataBatchParseException("Each request within a change set MUST specify a Content-ID header")

      val contentID = changeSetRequestComponentHeaders.get(ContentIdHeader).get.toInt
      if (contentIds.contains(contentID)) throw new ODataBatchParseException("Value of Content-ID header within a change set must be unique")
      contentIds += contentID
      requestComponent
    }
  }

def getRequestURI: Parser[Map[String, String]] = opt("^(http|https):\\/\\/[^\\/]*[:\\d{0,5}]?".r) ~ opt("\\/.+\\/(?=[a-zA-Z])".r) ~ opt("\\$.d{0,5}\\/".r) ~
    ("[^\\s]+".r <~ opt(" HTTP/\\d\\.\\d".r)) <~ lineSeparator ^^ {
  case hostComponent ~ relativePath ~ contentId ~ reqUri => {
      var components = Map("RequestEntity" -> reqUri)
      relativePath match {
        case Some(relPath) => components += ("RelativePath" -> relPath)
        case None =>
      }
      hostComponent match {
        case Some(host) => components += ("RequestHost" -> host)
        case None =>
      }

      contentId match {
        case Some(id) => components += ("ContentId" -> id)
        case None =>
      }

      components
    }

  }

  def parseChangeSetRequestComponentHeader: Parser[Map[String, String]] = parseHeaders <~ lineSeparator

  // Generic header parsing, parsed as name-value pairs
  def parseHeaders: Parser[Map[String, String]] = repsep(requestHeaderNameValuePair, lineSeparator) <~ lineSeparator ^^ {
    res => res.map(s => (s._1, s._2)).toMap
  }

  def requestHeaderNameValuePair: Parser[(String, String)] = (("""(?s)[-\w.]*|\n""".r <~ ":") ~
    (opt("\\s*".r) ~> """[\w\:\+/,_; \[\].\-\"\'\?=]*""".r)) ^^ {
    case name ~ value => (name, value)
  }

}
