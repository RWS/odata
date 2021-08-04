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

import com.sdl.odata.api.parser._
import com.sdl.odata.api.service.MediaType

import scala.util.parsing.combinator.RegexParsers

trait QueryOptionsParser extends RegexParsers {
  this: ExpressionsParser with NamesAndIdentifiersParser with LiteralsParser with EntityDataModelHelpers =>

  def queryOptions(contextTypeName: String): Parser[List[QueryOption]] =
    rep1sep(queryOption(contextTypeName), "&")

  def queryOption(contextTypeName: String): Parser[QueryOption] =
    systemQueryOption(contextTypeName) | aliasAndValue(contextTypeName) | customQueryOption

  // Query options for unnamed entities: limited subset allowed, must include an 'id' query option
  def entityOptions: Parser[List[QueryOption]] = rep(entityIdOption <~ "&") ~ id ~ rep("&" ~> entityIdOption) ^^ {
    case left ~ id ~ right => (left :+ id) ++ right
  }

  // Subset of query options allowed for unnamed entities
  def entityIdOption: Parser[QueryOption] = format | customQueryOption

  // Query options for named entities: limited subset allowed, must include an 'id' query option
  def entityCastOptions(contextTypeName: String): Parser[List[QueryOption]] =
  rep(entityCastOption(contextTypeName) <~ "&") ~ id ~ rep("&" ~> entityCastOption(contextTypeName)) ^^ {
    case left ~ id ~ right => (left :+ id) ++ right
  }

  // Subset of query options allowed for named entities
  def entityCastOption(contextTypeName: String): Parser[QueryOption] =
  entityIdOption | expand(contextTypeName) | select(contextTypeName)

  def id: Parser[IdOption] = "$id=" ~> """[^&]+""".r ^^ IdOption

  // apply systemQueryoption added
  def systemQueryOption(contextTypeName: String): Parser[SystemQueryOption] =
  expand(contextTypeName) | filter(contextTypeName) | format | id | inlinecount | orderby(contextTypeName) |
    search(contextTypeName) | select(contextTypeName) | skip | skiptoken | top | apply(contextTypeName)

  def expand(contextTypeName: String): Parser[ExpandOption] =
    "$expand=" ~> rep1sep(expandItem(contextTypeName), ",") ^^ ExpandOption

  def expandItem(contextTypeName: String): Parser[ExpandItem] = allRefExpandItem | allExpandItem |
    (pathRefExpandItem(contextTypeName) | pathCountExpandItem(contextTypeName) | pathExpandItem(contextTypeName))
      .withFailureMessage("The URI contains an invalid $expand path")

  def allExpandItem: Parser[AllExpandItem] =
    "*" ~> opt("(" ~> levels <~ ")") ^^ { case options => AllExpandItem(options.toList) }

  def allRefExpandItem: Parser[AllRefExpandItem.type] = "*/$ref" ^^^ AllRefExpandItem


  def pathExpandItem(contextTypeName: String): Parser[PathExpandItem] =
    opt(qualifiedEntityTypeName <~ "/") into pathExpandItemSub(contextTypeName)

  def pathExpandItemSub(contextTypeName: String)(derivedTypeName: Option[String]): Parser[PathExpandItem] =
    expandPathSegment(derivedTypeName.getOrElse(contextTypeName)) into pathExpandItemWithOptions(contextTypeName, derivedTypeName)

  def pathExpandItemWithOptions(contextTypeName: String, derivedTypeName: Option[String])(path: ExpandPathSegment): Parser[PathExpandItem] =
    opt(expandOptions(resolvePathTypeName(contextTypeName, path))) ^^ {
      case options => PathExpandItem(derivedTypeName, path, options.getOrElse(List.empty))
    }


  def pathRefExpandItem(contextTypeName: String): Parser[PathRefExpandItem] =
    opt(qualifiedEntityTypeName <~ "/") into pathRefExpandItemSub(contextTypeName)

  def pathRefExpandItemSub(contextTypeName: String)(derivedTypeName: Option[String]): Parser[PathRefExpandItem] =
    (expandPathSegment(derivedTypeName.getOrElse(contextTypeName)) <~ "/$ref") into pathRefExpandItemWithOptions(contextTypeName, derivedTypeName)

  def pathRefExpandItemWithOptions(contextTypeName: String, derivedTypeName: Option[String])(path: ExpandPathSegment): Parser[PathRefExpandItem] =
    opt(expandRefOptions(resolvePathTypeName(contextTypeName, path))) ^^ {
      case options => PathRefExpandItem(derivedTypeName, path, options.getOrElse(List.empty))
    }


  def pathCountExpandItem(contextTypeName: String): Parser[PathCountExpandItem] =
    opt(qualifiedEntityTypeName <~ "/") into pathCountExpandItemSub(contextTypeName)

  def pathCountExpandItemSub(contextTypeName: String)(derivedTypeName: Option[String]): Parser[PathCountExpandItem] =
    (expandPathSegment(derivedTypeName.getOrElse(contextTypeName)) <~ "/$count") into pathCountExpandItemWithOptions(contextTypeName, derivedTypeName)

  def pathCountExpandItemWithOptions(contextTypeName: String, derivedTypeName: Option[String])(path: ExpandPathSegment): Parser[PathCountExpandItem] =
    opt(expandCountOptions(resolvePathTypeName(contextTypeName, path))) ^^ {
      case options => PathCountExpandItem(derivedTypeName, path, options.getOrElse(List.empty))
    }


  def expandPathSegment(contextTypeName: String): Parser[ExpandPathSegment] =
    complexPropertyExpandPathSegment(contextTypeName) | complexColPropertyExpandPathSegment(contextTypeName) |
      navigationPropertyExpandPathSegment(contextTypeName)

  def complexPropertyExpandPathSegment(contextTypeName: String): Parser[ComplexPropertyExpandPathSegment] =
    (complexProperty(contextTypeName) ~ ("/" ~> opt(qualifiedComplexTypeName <~ "/"))) into {
      case propertyName ~ derivedTypeName =>
        val subPathContextTypeName = derivedTypeName.orElse(getSinglePropertyTypeName(contextTypeName, propertyName)).get
        expandPathSegment(subPathContextTypeName) ^^ {
          case subPath => ComplexPropertyExpandPathSegment(propertyName, derivedTypeName, subPath)
        }
    }

  def complexColPropertyExpandPathSegment(contextTypeName: String): Parser[ComplexPropertyExpandPathSegment] =
    (complexColProperty(contextTypeName) ~ ("/" ~> opt(qualifiedComplexTypeName <~ "/"))) into {
      case propertyName ~ derivedTypeName =>
        val subPathContextTypeName = derivedTypeName.orElse(getPropertyElementTypeName(contextTypeName, propertyName)).get
        expandPathSegment(subPathContextTypeName) ^^ {
          case subPath => ComplexPropertyExpandPathSegment(propertyName, derivedTypeName, subPath)
        }
    }

  def navigationPropertyExpandPathSegment(contextTypeName: String): Parser[NavigationPropertyExpandPathSegment] =
    navigationProperty(contextTypeName) ~ opt("/" ~> qualifiedEntityTypeName) ^^ {
      case propertyName ~ derivedTypeName => NavigationPropertyExpandPathSegment(propertyName, derivedTypeName)
    }


  private def resolvePathTypeName(contextTypeName: String, path: ExpandPathSegment): String = {
    val subPathContextTypeName = path.derivedTypeName
      .orElse(getSinglePropertyTypeName(contextTypeName, path.propertyName))
      .orElse(getPropertyElementTypeName(contextTypeName, path.propertyName))
      .get

    path match {
      case ComplexPropertyExpandPathSegment(_, _, subPath) => resolvePathTypeName(subPathContextTypeName, subPath)
      case NavigationPropertyExpandPathSegment(_, _) => subPathContextTypeName
    }
  }


  def expandOption(contextTypeName: String): Parser[QueryOption] =
    expandRefOption(contextTypeName) | select(contextTypeName) | apply(contextTypeName) |
      expand(contextTypeName) | levels

  def expandOptions(contextTypeName: String): Parser[List[QueryOption]] =
    "(" ~> rep1sep(expandOption(contextTypeName), ";") <~ ")"

  def expandRefOption(contextTypeName: String): Parser[QueryOption] =
    expandCountOption(contextTypeName) | orderby(contextTypeName) | skip | top | inlinecount

  def expandRefOptions(contextTypeName: String): Parser[List[QueryOption]] =
    "(" ~> rep1sep(expandRefOption(contextTypeName), ";") <~ ")"

  def expandCountOption(contextTypeName: String): Parser[QueryOption] =
    filter(contextTypeName) | search(contextTypeName)

  def expandCountOptions(contextTypeName: String): Parser[List[QueryOption]] =
    "(" ~> rep1sep(expandCountOption(contextTypeName), ";") <~ ")"


  def levels: Parser[LevelsQueryOption] =
    "$levels=" ~> ("\\d+".r ^^ { case s => LevelsQueryOption(s.toInt) } | "max" ^^^ LevelsQueryOption(Int.MaxValue))

  def filter(contextTypeName: String): Parser[FilterOption] =
    ("$filter=" ~> boolCommonExpr(contextTypeName) ^^ FilterOption)
      .withFailureMessage("The URI contains an incorrectly specified $filter option")

  // $apply option parsing
  def apply(contextTypeName: String): Parser[ApplyOption] =
  ("$apply=" ~> applyExpr(contextTypeName) ^^ ApplyOption)
    .withFailureMessage("The URI contains an incorrectly specified $apply option")

  def orderby(contextTypeName: String): Parser[OrderByOption] =
    ("$orderby=" ~> rep1sep(orderbyItem(contextTypeName), ",") ^^ OrderByOption)
      .withFailureMessage("The URI contains an incorrectly specified $orderby option")

  def orderbyItem(contextTypeName: String): Parser[OrderByItem] =
    commonExpr(contextTypeName) ~ opt("""\s+""".r ~> ("asc" | "desc")) ^^ {
      case expression ~ Some("asc") => AscendingOrderByItem(expression)
      case expression ~ Some("desc") => DescendingOrderByItem(expression)
      case expression ~ None => AscendingOrderByItem(expression)
    }

  def skip: Parser[SkipOption] = ("$skip=" ~> """\d+""".r ^^ { case s => SkipOption(s.toInt) })
    .withFailureMessage("The URI contains an incorrectly specified $skip option")

  def top: Parser[TopOption] = ("$top=" ~> """\d+""".r ^^ { case s => TopOption(s.toInt) })
    .withFailureMessage("The URI contains an incorrectly specified $top option")

  def format: Parser[FormatOption] = (formatMediaType ^^ FormatOption)
    .withFailureMessage("The URI contains an incorrectly specified $format option")

  def formatMediaType: Parser[MediaType] = "$format=" ~> (formatAtom | formatJson | formatXML | mediaType)

  def formatAtom: Parser[MediaType] = "(?i)atom".r ^^^ MediaType.ATOM_XML

  def formatJson: Parser[MediaType] = "(?i)json".r ^^^ MediaType.JSON

  def formatXML: Parser[MediaType] = "(?i)xml".r ^^^ MediaType.XML

  def mediaType: Parser[MediaType] = mediaTypePart ~ ("/" ~> mediaTypePart) ^^ {
    case mediaType ~ mediaSubType => new MediaType(mediaType, mediaSubType)
  }

  // String consisting of one or more pchar
  def mediaTypePart: Parser[String] =
  """([A-Za-z0-9\-\._~:@\$\&'=!\(\)\*\+,;])+""".r

  def inlinecount: Parser[CountOption] = ("$count=" ~> booleanValue ^^ CountOption)
    .withFailureMessage("The URI contains an incorrectly specified $count option")

  def search(contextTypeName: String): Parser[SearchOption] =
    ("""\$search=\s*""".r ~> searchExpr ^^ SearchOption)
      .withFailureMessage("The URI contains an incorrectly specified $search option")

  def searchExpr: Parser[SearchExpression] = ???

  def searchOrExpr: Parser[OrSearchExpression] = ???

  def searchAndExpr: Parser[AndSearchExpression] = ???

  def searchTerm: Parser[SearchTerm] = opt("""NOT\s+""".r) ~ (searchPhrase | searchWord) ^^ {
    case None ~ value => NormalSearchTerm(value)
    case Some(_) ~ value => NegatedSearchTerm(value)
  }

  def searchPhrase: Parser[String] = ???

  def searchWord: Parser[String] = ???

  def select(contextTypeName: String): Parser[SelectOption] =
    ("$select=" ~> rep1sep(selectItem(contextTypeName), ",") ^^ SelectOption)
      .withFailureMessage("The URI contains an incorrectly specified $select option")

  def selectItem(contextTypeName: String): Parser[SelectItem] = allSelectItem | schemaAllSelectItem |
    pathSelectItem(contextTypeName) | actionSelectItem | functionSelectItem

  def allSelectItem: Parser[AllSelectItem.type] = "*" ^^^ AllSelectItem

  def schemaAllSelectItem: Parser[SchemaAllSelectItem] = namespace <~ "*" ^^ {
    case ns => SchemaAllSelectItem(ns.substring(0, ns.length - 1)) // Remove trailing "." from namespace name
  }

  def pathSelectItem(contextTypeName: String): Parser[PathSelectItem] =
    opt(qualifiedEntityTypeName <~ "/") into pathSelectItemSub(contextTypeName)

  def pathSelectItemSub(contextTypeName: String)(derivedTypeName: Option[String]): Parser[PathSelectItem] =
    selectPathSegment(derivedTypeName.getOrElse(contextTypeName)) ^^ {
      case path => PathSelectItem(derivedTypeName, path)
    }

  def actionSelectItem: Parser[ActionSelectItem] =
    opt(qualifiedEntityTypeName <~ "/") ~ qualifiedActionName ^^ {
      case derivedTypeName ~ actionName => ActionSelectItem(derivedTypeName, actionName)
    }

  def functionSelectItem: Parser[FunctionSelectItem] =
    opt(qualifiedEntityTypeName <~ "/") ~ qualifiedFunctionName ^^ {
      case derivedTypeName ~ ((functionName, paramNames)) => FunctionSelectItem(derivedTypeName, functionName, paramNames)
    }

  def selectPathSegment(contextTypeName: String): Parser[SelectPathSegment] =
    complexPropertySelectPathSegment(contextTypeName) | complexColPropertySelectPathSegment(contextTypeName) |
      primitivePropertySelectPathSegment(contextTypeName) | primitiveColPropertySelectPathSegment(contextTypeName) |
      navigationPropertySelectPathSegment(contextTypeName)

  def complexPropertySelectPathSegment(contextTypeName: String): Parser[ComplexPropertySelectPathSegment] =
    (complexProperty(contextTypeName) ~ opt("/" ~> qualifiedComplexTypeName)) into {
      case propertyName ~ derivedTypeName =>
        val subPathContextTypeName = derivedTypeName.orElse(getSinglePropertyTypeName(contextTypeName, propertyName)).get
        opt(selectPathSegment(subPathContextTypeName)) ^^ {
          case subPath => ComplexPropertySelectPathSegment(propertyName, derivedTypeName, subPath)
        }
    }

  def complexColPropertySelectPathSegment(contextTypeName: String): Parser[ComplexPropertySelectPathSegment] =
    (complexColProperty(contextTypeName) ~ opt("/" ~> qualifiedComplexTypeName)) into {
      case propertyName ~ derivedTypeName =>
        val subPathContextTypeName = derivedTypeName.orElse(getPropertyElementTypeName(contextTypeName, propertyName)).get
        opt(selectPathSegment(subPathContextTypeName)) ^^ {
          case subPath => ComplexPropertySelectPathSegment(propertyName, derivedTypeName, subPath)
        }
    }

  def primitivePropertySelectPathSegment(contextTypeName: String): Parser[TerminalPropertySelectPathSegment] =
    primitiveProperty(contextTypeName) ^^ TerminalPropertySelectPathSegment

  def primitiveColPropertySelectPathSegment(contextTypeName: String): Parser[TerminalPropertySelectPathSegment] =
    primitiveColProperty(contextTypeName) ^^ TerminalPropertySelectPathSegment

  def navigationPropertySelectPathSegment(contextTypeName: String): Parser[TerminalPropertySelectPathSegment] =
    navigationProperty(contextTypeName) ^^ TerminalPropertySelectPathSegment


  def qualifiedActionName: Parser[String] = failure("Actions are not supported")

  def qualifiedFunctionName: Parser[(String, List[String])] = failure("Functions are not supported")

  // String consisting of one or more qchar-NO-AMP
  def skiptoken: Parser[SkipTokenOption] =
  "$skiptoken=" ~> """([A-Za-z0-9\-\._~!\(\)\*\+,;:@/\?\$'=])+""".r ^^ SkipTokenOption

  def aliasAndValue(contextTypeName: String): Parser[AliasAndValueOption] =
    "@" ~> odataIdentifier ~ ("=" ~> commonExpr(contextTypeName)) ^^ {
      case alias ~ value => AliasAndValueOption(alias, value)
    }

  def customQueryOption: Parser[CustomOption] = customName ~ opt("=" ~> customValue) ^^ {
    case name ~ value => CustomOption(name, value)
  }

  // One qchar-no-AMP-EQ-AT-DOLLAR followed by zero or more qchar-no-AMP-EQ
  def customName: Parser[String] =
  """[A-Za-z0-9\-\._~!\(\)\*\+,;:/\?']([A-Za-z0-9\-\._~!\(\)\*\+,;:@/\?\$'])*""".r

  // Zero or more qchar-no-AMP
  def customValue: Parser[String] =
  """([A-Za-z0-9\-\._~!\(\)\*\+,;:@/\?\$'=])*""".r
}
