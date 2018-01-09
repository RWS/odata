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

import scala.util.parsing.combinator.RegexParsers

trait ExpressionsParser extends RegexParsers {
  this: ResourcePathParser with QueryOptionsParser with NamesAndIdentifiersParser with LiteralsParser with EntityDataModelHelpers =>

  def commonExpr(contextTypeName: String): Parser[Expression] = commonExprPart1(contextTypeName) into {
    left =>
      opt(additiveExpr(contextTypeName, left)) ^^ {
        case Some(additiveExpr) => additiveExpr
        case None => left
      }
  }

  def commonExprPart1(contextTypeName: String): Parser[Expression] = commonExprPart2(contextTypeName) into {
    left =>
      opt(multiplicativeExpr(contextTypeName, left)) ^^ {
        case Some(multiplicativeExpr) => multiplicativeExpr
        case None => left
      }
  }

  def commonExprPart2(contextTypeName: String): Parser[Expression] = literalExpr | paramAliasExpr | jsonDataExpr |
    rootExpr | functionExpr(contextTypeName) | negateExpr(contextTypeName) | methodCallExpr(contextTypeName) |
    parenExpr(contextTypeName) | castExpr(contextTypeName) | firstMemberExpr(contextTypeName)

  def literalExpr: Parser[LiteralExpr] = primitiveLiteral ^^ LiteralExpr

  def paramAliasExpr: Parser[ParameterAliasExpr] = "@" ~> odataIdentifier ^^ ParameterAliasExpr

  def jsonDataExpr: Parser[JsonDataExpr] = failure("JSON data not yet supported")

  def boolCommonExpr(contextTypeName: String): Parser[BooleanExpr] = boolCommonExprPart1(contextTypeName) into {
    left =>
      opt(orExpr(contextTypeName, left)) ^^ {
        case Some(orExpr) => orExpr
        case None => left
      }
  }

  def boolCommonExprPart1(contextTypeName: String): Parser[BooleanExpr] = boolCommonExprPart2(contextTypeName) into {
    left =>
      opt(andExpr(contextTypeName, left)) ^^ {
        case Some(andExpr) => andExpr
        case None => left
      }
  }

  def boolCommonExprPart2(contextTypeName: String): Parser[BooleanExpr] =
    isofExpr(contextTypeName) | boolMethodCallExpr(contextTypeName) | notExpr(contextTypeName) |
    (commonExpr(contextTypeName) into comparisonExpr(contextTypeName)) | boolParenExpr(contextTypeName) | firstMemberExpr(contextTypeName)

  def rootExpr: Parser[RootExpr] = "$root/" ~> (entitySetRootExpr | singletonRootExpr)

  def entitySetRootExpr: Parser[EntitySetRootExpr] = entitySetName into {
    entitySetName =>
      val entityTypeName = getEntitySetTypeName(entitySetName).get
      keyPredicate(entityTypeName) ~ opt(singleNavigationExpr(entityTypeName)) ^^ {
        case keyPredicate ~ subPath => EntitySetRootExpr(entitySetName, keyPredicate, subPath)
      }
    }

  def singletonRootExpr: Parser[SingletonRootExpr] = singletonEntity into {
    singletonName =>
      val entityTypeName = getSingletonTypeName(singletonName).get
      opt(singleNavigationExpr(entityTypeName)) ^^ {
        case subPath => SingletonRootExpr(singletonName, subPath)
      }
    }

  def firstMemberExpr(contextTypeName: String): Parser[BooleanPathExpr] = memberExpr(contextTypeName) | inscopeVariableExpr

  def memberExpr(contextTypeName: String): Parser[EntityPathExpr] =
    opt(qualifiedEntityTypeName <~ "/") into {
      derivedTypeNameOpt =>
        val entityTypeName = derivedTypeNameOpt.getOrElse(contextTypeName)
        (propertyPathExpr(entityTypeName) | boundFunctionExpr(entityTypeName)) ^^ {
          case subPath => EntityPathExpr(derivedTypeNameOpt, Some(subPath))
        }
    }

  def propertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    entityNavPropertyPathExpr(contextTypeName) | entityColNavPropertyPathExpr(contextTypeName) |
    complexPropertyPathExpr(contextTypeName) | complexColPropertyPathExpr(contextTypeName) |
    primitivePropertyPathExpr(contextTypeName) | primitiveColPropertyPathExpr(contextTypeName) |
    streamPropertyPathExpr(contextTypeName)

  def entityNavPropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    entityNavigationProperty(contextTypeName) into {
      propertyName =>
        val propertyTypeName = getSinglePropertyTypeName(contextTypeName, propertyName).get
        opt(singleNavigationExpr(propertyTypeName)) ^^ { case subPath => PropertyPathExpr(propertyName, subPath) }
    }

  def entityColNavPropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    entityColNavigationProperty(contextTypeName) into {
      propertyName =>
        val propertyElementTypeName = getPropertyElementTypeName(contextTypeName, propertyName).get
        opt(collectionNavigationExpr(propertyElementTypeName)) ^^ { case subPath => PropertyPathExpr(propertyName, subPath) }
    }

  def complexPropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    complexProperty(contextTypeName) into {
      propertyName =>
        val propertyTypeName = getSinglePropertyTypeName(contextTypeName, propertyName).get
        opt(complexPathExpr(propertyTypeName)) ^^ { case subPath => PropertyPathExpr(propertyName, subPath) }
    }

  def complexColPropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    complexColProperty(contextTypeName) into {
      propertyName =>
        val propertyElementTypeName = getPropertyElementTypeName(contextTypeName, propertyName).get
        opt(collectionPathExpr(propertyElementTypeName)) ^^ { case subPath => PropertyPathExpr(propertyName, subPath) }
    }

  def primitivePropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    primitiveProperty(contextTypeName) into {
      propertyName =>
        val propertyTypeName = getSinglePropertyTypeName(contextTypeName, propertyName).get
        opt(singlePathExpr(propertyTypeName)) ^^ { case subPath => PropertyPathExpr(propertyName, subPath) }
    }

  def primitiveColPropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    primitiveColProperty(contextTypeName) into {
      propertyName =>
        val propertyElementTypeName = getPropertyElementTypeName(contextTypeName, propertyName).get
        opt(collectionPathExpr(propertyElementTypeName)) ^^ { case subPath => PropertyPathExpr(propertyName, subPath) }
    }

  def streamPropertyPathExpr(contextTypeName: String): Parser[PropertyPathExpr] =
    streamProperty(contextTypeName) ^^ { case propertyName => PropertyPathExpr(propertyName, None) }

  def inscopeVariableExpr: Parser[BooleanPathExpr] = implicitVariableExpr  | lambdaVariableExpr

  // Note: What type name to pass to singleNavigationExpr? Need to know to what type '$it' refers.
  def implicitVariableExpr: Parser[ImplicitVariableExpr] =
    "$it" ~> opt(singleNavigationExpr("TODO.TODO")) ^^ { case subPath => ImplicitVariableExpr(subPath) }

  // TODO: see notes
  // Note: What type name to pass to singleNavigationExpr? Need to know to what type the lambda variable refers.
  // Note: Somehow check that the identifier is the name of a lambda variable.
  def lambdaVariableExpr: Parser[LambdaVariableExpr] =
    odataIdentifier ~ opt(singleNavigationExpr("Edm.String")) ^^ {
      case variableName ~ subPath => LambdaVariableExpr(variableName, subPath)
    }

  def collectionNavigationExpr(contextTypeName: String): Parser[EntityCollectionPathExpr] =
    opt("/" ~> qualifiedEntityTypeName) into {
      derivedTypeNameOpt =>
        val entityTypeName = derivedTypeNameOpt.getOrElse(contextTypeName)
        (keyPredicatePathExpr(entityTypeName) | collectionPathExpr(entityTypeName)) ^^ {
          case subPath => EntityCollectionPathExpr(derivedTypeNameOpt, Some(subPath))
        }
    }

  def keyPredicatePathExpr(contextTypeName: String): Parser[KeyPredicatePathExpr] =
    keyPredicate(contextTypeName) ~ opt(singleNavigationExpr(contextTypeName)) ^^ {
      case keyPredicate ~ subPath => KeyPredicatePathExpr(keyPredicate, subPath)
    }

  def singleNavigationExpr(contextTypeName: String): Parser[EntityPathExpr] =
    "/" ~> memberExpr(contextTypeName)

  def collectionPathExpr(contextTypeName: String): Parser[PathExpr] =
    countPathExpr | ("/" ~> (boundFunctionExpr(contextTypeName) | anyExpr(contextTypeName) | allExpr(contextTypeName)))

  def countPathExpr: Parser[CountPathExpr.type] = "/$count" ^^^ CountPathExpr

  def complexPathExpr(contextTypeName: String): Parser[ComplexPathExpr] =
    "/" ~> opt(qualifiedComplexTypeName <~ "/") into {
      derivedTypeNameOpt =>
        val entityTypeName = derivedTypeNameOpt.getOrElse(contextTypeName)
        (propertyPathExpr(entityTypeName) | boundFunctionExpr(entityTypeName)) ^^ {
          case subPath => ComplexPathExpr(derivedTypeNameOpt, Some(subPath))
        }
    }

  def singlePathExpr(contextTypeName: String): Parser[PathExpr] =
    "/" ~> boundFunctionExpr(contextTypeName)

  def boundFunctionExpr(contextTypeName: String): Parser[BoundFunctionCallPathExpr] =
    boundEntityFunctionExpr(contextTypeName) | boundEntityColFunctionExpr(contextTypeName) |
    boundComplexFunctionExpr(contextTypeName) | boundComplexColFunctionExpr(contextTypeName) |
    boundPrimitiveFunctionExpr(contextTypeName)

  def generalBoundFunctionExpr(contextTypeName: String, pathExprParser: String => Parser[PathExpr]): Parser[BoundFunctionCallPathExpr] = namespace into {
    namespaceName =>
      function into {
        functionName =>
          functionExprParameters(contextTypeName) ~ opt(pathExprParser(getFunctionReturnType(functionName).get)) ^^ {
            case args ~ subPath => BoundFunctionCallPathExpr(namespaceName + functionName, args, subPath)
          }
      }
  }

  // Expression with a bound function that returns a single entity
  def boundEntityFunctionExpr(contextTypeName: String): Parser[BoundFunctionCallPathExpr] = generalBoundFunctionExpr(contextTypeName, singleNavigationExpr)

  // Expression with a bound function that returns a collection of entities
  def boundEntityColFunctionExpr(contextTypeName: String): Parser[BoundFunctionCallPathExpr] = generalBoundFunctionExpr(contextTypeName, collectionNavigationExpr)

  // Expression with a bound function that returns a single complex object
  def boundComplexFunctionExpr(contextTypeName: String): Parser[BoundFunctionCallPathExpr] = generalBoundFunctionExpr(contextTypeName, complexPathExpr)

  // Expression with a bound function that returns a collection of complex objects
  // Expression with a bound function that returns a collection of primitive values
  def boundComplexColFunctionExpr(contextTypeName: String): Parser[BoundFunctionCallPathExpr] = generalBoundFunctionExpr(contextTypeName, collectionPathExpr)

  // Expression with a bound function that returns a single primitive value
  def boundPrimitiveFunctionExpr(contextTypeName: String): Parser[BoundFunctionCallPathExpr] = generalBoundFunctionExpr(contextTypeName, singlePathExpr)

  def functionExpr(contextTypeName: String): Parser[FunctionCallExpr] =
    entityFunctionExpr(contextTypeName) | entityColFunctionExpr(contextTypeName) |
    complexFunctionExpr(contextTypeName) | complexColFunctionExpr(contextTypeName) |
    primitiveFunctionExpr(contextTypeName)

  def generalFunctionExpr(contextTypeName: String, pathExprParser: String => Parser[PathExpr]): Parser[FunctionCallExpr] = namespace into {
    namespaceName =>
      function into {
        functionName =>
          functionExprParameters(contextTypeName) ~ opt(pathExprParser(getFunctionReturnType(functionName).get)) ^^ {
            case args ~ subPath => FunctionCallExpr(namespaceName + functionName, args, subPath)
          }
      }
  }

  def entityFunctionExpr(contextTypeName: String): Parser[FunctionCallExpr] = generalFunctionExpr(contextTypeName, singleNavigationExpr)

  def entityColFunctionExpr(contextTypeName: String): Parser[FunctionCallExpr] = generalFunctionExpr(contextTypeName, collectionNavigationExpr)

  def complexFunctionExpr(contextTypeName: String): Parser[FunctionCallExpr] = generalFunctionExpr(contextTypeName, complexPathExpr)

  def complexColFunctionExpr(contextTypeName: String): Parser[FunctionCallExpr] = generalFunctionExpr(contextTypeName, collectionPathExpr)

  def primitiveFunctionExpr(contextTypeName: String): Parser[FunctionCallExpr] = generalFunctionExpr(contextTypeName, singlePathExpr)

  def functionExprParameters(contextTypeName: String): Parser[Map[String, FunctionExprParam]] =
    "(" ~> rep1sep(functionExprParameter(contextTypeName), ",") <~ ")" ^^ { case params => params.toMap }

  def functionExprParameter(contextTypeName: String): Parser[(String, FunctionExprParam)] =
    odataIdentifier ~ ("=" ~> (aliasFunctionExprParam | expressionFunctionExprParam(contextTypeName))) ^^ {
      case name ~ param => (name, param)
    }

  def aliasFunctionExprParam: Parser[AliasFunctionExprParam] = "@" ~> odataIdentifier ^^ AliasFunctionExprParam

  def expressionFunctionExprParam(contextTypeName: String): Parser[ExpressionFunctionExprParam] =
    commonExpr(contextTypeName) ^^ ExpressionFunctionExprParam

  def anyExpr(contextTypeName: String): Parser[AnyPathExpr] =
    """any\(\s*""".r ~> opt(lambdaVariableNameAndPredicate(contextTypeName)) <~ """\s*\)""".r ^^ AnyPathExpr

  def allExpr(contextTypeName: String): Parser[AllPathExpr] =
    """all\(\s*""".r ~> lambdaVariableNameAndPredicate(contextTypeName) <~ """\s*\)""".r ^^ AllPathExpr

  def lambdaVariableNameAndPredicate(contextTypeName: String): Parser[LambdaVariableAndPredicate] =
    odataIdentifier ~ ("""\s*:\s*""".r ~> boolCommonExpr(contextTypeName)) ^^ {
      case variableName ~ predicate => LambdaVariableAndPredicate(variableName, predicate)
    }

  def methodCallExpr(contextTypeName: String): Parser[MethodCallExpr] =
    methodName ~ methodCallArgs(contextTypeName) ^^ { case methodName ~ args => MethodCallExpr(methodName, args) }

  def methodName: Parser[String] = "length" | "indexof" | "substring" | "tolower" | "toupper" | "trim" | "concat" |
    "year" | "month" | "day" | "hour" | "minute" | "second" | "fractionalseconds" | "totalseconds" | "date" | "time" |
    "totaloffsetminutes" | "mindatetime" | "maxdatetime" | "now" | "round" | "floor" | "ceiling" |"geo.distance" |
    "geo.length" withFailureMessage "Expected a method name"

  def applyExpr(contextTypeName: String): Parser[ApplyExpr] =
  (applyMethodName <~ """\s*\(""".r) ~ ("""\(\s*""".r ~> applyMethodCallArgs(contextTypeName) <~ """\s*\)""".r) ^^ { case methodName ~ args =>
  ApplyExpr(methodName, args) }

  def applyFunctionCallExpr(contextTypeName: String): Parser[ApplyFunctionExpr] =
  		applyFunctionName ~ applyfunctionParams ^^ { case methodName ~ args => ApplyFunctionExpr(methodName, args) }

  def applyfunctionParams: Parser[String] = """\(\s*""".r ~> """[\$\w\s*]+""".r <~ """\s*\)""".r withFailureMessage "Expected words"

  def applyMethodName: Parser[String] = "groupby" withFailureMessage "Expected a method name"

  def applyFunctionName: Parser[String] = "aggregate" withFailureMessage "Expected a method name"
  
  def applyMethodCallArgs(contextTypeName: String): Parser[ApplyMethodCallExpr] =
  (repsep(memberExpr(contextTypeName), """\s*,\s*""".r) <~ """\s*\)\s*""".r) ~ (""",\s*""".r ~>applyFunctionCallExpr(contextTypeName)) ^^ {
  case properties ~ function => ApplyMethodCallExpr(ApplyPropertyExpr(properties), function);
	}

  def methodCallArgs(contextTypeName: String): Parser[List[Expression]] =
    """\(\s*""".r ~> repsep(commonExpr(contextTypeName), """\s*,\s*""".r) <~ """\s*\)""".r withFailureMessage "Invalid method call arguments"

  def boolMethodCallExpr(contextTypeName: String): Parser[BooleanMethodCallExpr] =
    boolMethodName ~ ("""\(\s*""".r ~> repsep(commonExpr(contextTypeName), """\s*,\s*""".r) <~ """\s*\)""".r) ^^ {
      case methodName ~ args => BooleanMethodCallExpr(methodName, args)
    }

  def boolMethodName: Parser[String] = "contains" | "startswith" | "endswith" | "geo.intersects"

  def boolParenExpr(contextTypeName: String): Parser[BooleanExpr] =
    """\(\s*""".r ~> boolCommonExpr(contextTypeName) <~ """\s*\)""".r

  def parenExpr(contextTypeName: String): Parser[Expression] =
    """\(\s*""".r ~> commonExpr(contextTypeName) <~ """\s*\)""".r

  def orExpr(contextTypeName: String, left: BooleanExpr): Parser[OrExpr] =
    """\s+or\s+""".r ~> boolCommonExpr(contextTypeName) ^^ { case right => OrExpr(left, right) }

  def andExpr(contextTypeName: String, left: BooleanExpr): Parser[AndExpr] =
    """\s+and\s+""".r ~> boolCommonExprPart1(contextTypeName) ^^ { case right => AndExpr(left, right) }

  def comparisonExpr(contextTypeName: String)(left: Expression): Parser[ComparisonExpr] =
    eqExpr(contextTypeName, left) | neExpr(contextTypeName, left) |
    ltExpr(contextTypeName, left) | leExpr(contextTypeName, left) |
    gtExpr(contextTypeName, left) | geExpr(contextTypeName, left) |
    hasExpr(contextTypeName, left)

  def eqExpr(contextTypeName: String, left: Expression): Parser[EqExpr] =
    """\s+eq\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => EqExpr(left, right) }

  def neExpr(contextTypeName: String, left: Expression): Parser[NeExpr] =
    """\s+ne\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => NeExpr(left, right) }

  def ltExpr(contextTypeName: String, left: Expression): Parser[LtExpr] =
    """\s+lt\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => LtExpr(left, right) }

  def leExpr(contextTypeName: String, left: Expression): Parser[LeExpr] =
    """\s+le\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => LeExpr(left, right) }

  def gtExpr(contextTypeName: String, left: Expression): Parser[GtExpr] =
    """\s+gt\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => GtExpr(left, right) }

  def geExpr(contextTypeName: String, left: Expression): Parser[GeExpr] =
    """\s+ge\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => GeExpr(left, right) }

  def hasExpr(contextTypeName: String, left: Expression): Parser[HasExpr] =
    """\s+has\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => HasExpr(left, right) }

  def additiveExpr(contextTypeName: String, left: Expression): Parser[ArithmeticExpr] =
    addExpr(contextTypeName, left) | subExpr(contextTypeName, left)

  def addExpr(contextTypeName: String, left: Expression): Parser[AddExpr] =
    """\s+add\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => AddExpr(left, right) }

  def subExpr(contextTypeName: String, left: Expression): Parser[SubExpr] =
    """\s+sub\s+""".r ~> commonExpr(contextTypeName) ^^ { case right => SubExpr(left, right) }

  def multiplicativeExpr(contextTypeName: String, left: Expression): Parser[ArithmeticExpr] =
    mulExpr(contextTypeName, left) | divExpr(contextTypeName, left) | modExpr(contextTypeName, left)

  def mulExpr(contextTypeName: String, left: Expression): Parser[MulExpr] =
    """\s+mul\s+""".r ~> commonExprPart1(contextTypeName) ^^ { case right => MulExpr(left, right) }

  def divExpr(contextTypeName: String, left: Expression): Parser[DivExpr] =
    """\s+div\s+""".r ~> commonExprPart1(contextTypeName) ^^ { case right => DivExpr(left, right) }

  def modExpr(contextTypeName: String, left: Expression): Parser[ModExpr] =
    """\s+mod\s+""".r ~> commonExprPart1(contextTypeName) ^^ { case right => ModExpr(left, right) }

  def negateExpr(contextTypeName: String): Parser[NegateExpr] =
    """-\s*""".r ~> commonExprPart2(contextTypeName) ^^ NegateExpr

  def notExpr(contextTypeName: String): Parser[NotExpr] =
    """not\s+""".r ~> boolCommonExprPart2(contextTypeName) ^^ NotExpr

  def isofExpr(contextTypeName: String): Parser[IsOfExpr] =
    """isof\(\s*""".r ~> opt(commonExpr(contextTypeName) <~ """\s*,\s*""".r) ~ qualifiedTypeName <~ """\s*\)""".r ^^ {
      case expression ~ typeName => IsOfExpr(expression, typeName)
    }

  def castExpr(contextTypeName: String): Parser[CastExpr] =
    """cast\(\s*""".r ~> opt(commonExpr(contextTypeName) <~ """\s*,\s*""".r) ~ qualifiedTypeName <~ """\s*\)""".r ^^ {
      case expression ~ typeName => CastExpr(expression, typeName)
    }
}
