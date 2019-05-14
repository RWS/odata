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

import scala.util.parsing.combinator.RegexParsers
import com.sdl.odata.api.edm.model.EntityDataModel
import com.sdl.odata.api.parser._
import java.net.URLDecoder

import org.springframework.stereotype.Controller

class ODataUriParser(val entityDataModel: EntityDataModel, val basepath: String = """(?i)^.*?\.svc""") extends RegexParsers
  with ResourcePathParser
  with QueryOptionsParser
  with ContextFragmentParser
  with ExpressionsParser
  with NamesAndIdentifiersParser
  with LiteralsParser
  with EntityDataModelHelpers {

  def this(entityDataModel: EntityDataModel) = this(entityDataModel, """(?i)^.*?\.svc""");

  def parseUri(input: String): ODataUri = parseAll(odataUri, URLDecoder.decode(input, "UTF-8")) match {
    case Success(result, _) => result
    case NoSuccess(msg, _) => throw new ODataUriParseException(msg)
  }

  def parseResourcePath(input: String): ResourcePath = parseAll(resourcePath, URLDecoder.decode(input, "UTF-8")) match {
    case Success(result, _) => result
    case NoSuccess(msg, _) => throw new ODataUriParseException(msg)
  }

  override val skipWhitespace = false

  def odataUri: Parser[ODataUri] = odataUriRelativeUri | odataUriServiceRoot

  def odataUriServiceRoot: Parser[ODataUri] = serviceRoot ~ opt("?" ~> formatMediaType) ^^ {
    case serviceRoot ~ format => ODataUri(serviceRoot, ServiceRootUri(format))
  }

  def odataUriRelativeUri: Parser[ODataUri] = serviceRoot ~ odataRelativeUri ^^ {
    case serviceRoot ~ relativeUri => ODataUri(serviceRoot, relativeUri)
  }

  // Everything up to ".svc" (case-insensitive) is considered to be part of the service root
  def serviceRoot: Parser[String] = basepath.r <~ opt("/")

  def odataRelativeUri: Parser[RelativeUri] = batchUri | entityUri | metadataUri | resourcePathUri

  def batchUri: Parser[BatchUri.type] = "$batch" ^^^ BatchUri

  def entityUri: Parser[EntityUri] = unnamedEntityUri | namedEntityUri

  def unnamedEntityUri: Parser[EntityUri] = "$entity?" ~> entityOptions ^^ {
    case options => EntityUri(None, options)
  }

  def namedEntityUri: Parser[EntityUri] = ("$entity/" ~> qualifiedEntityTypeName) into {
    derivedTypeName =>
      ("?" ~> entityCastOptions(derivedTypeName)) ^^ {
        case options => EntityUri(Some(derivedTypeName), options)
      }
  }

  def metadataUri: Parser[MetadataUri] = ("$metadata" ~ opt("/")) ~> opt("?" ~> formatMediaType) ~ opt(context) ^^ {
    case format ~ context => MetadataUri(format, context)
  }

  def resourcePathUri: Parser[ResourcePathUri] = resourcePath into {
    resourcePath =>
      opt("?" ~> queryOptions(resolveResourcePathTypeName(resourcePath))) ^^ {
        case options => ResourcePathUri(resourcePath, options.getOrElse(List.empty))
      }
  }
}
