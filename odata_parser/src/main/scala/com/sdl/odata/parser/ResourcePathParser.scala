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
package com.sdl.odata.parser

import com.sdl.odata.api.parser._

import scala.util.parsing.combinator.RegexParsers

trait ResourcePathParser extends RegexParsers {
  this: NamesAndIdentifiersParser with LiteralsParser with EntityDataModelHelpers =>

  def resourcePath: Parser[ResourcePath] =
    entitySetResourcePath | singletonEntityResourcePath | operationImportCall | crossjoin | all

  // Resource path that starts at an entity set
  def entitySetResourcePath: Parser[EntitySetPath] = entitySetName into {
    entitySetName =>
      val entityTypeName = getEntitySetTypeName(entitySetName).get
      (opt(collectionNavigation(entityTypeName)) ^^ { case subPath => EntitySetPath(entitySetName, subPath) })
        .withFailureMessage(s"The URI contains an invalid path into the entity set: $entitySetName")
  }

  // Resource path that starts at a singleton entity
  def singletonEntityResourcePath: Parser[SingletonPath] = singletonEntity into {
    singletonName =>
      val entityTypeName = getSingletonTypeName(singletonName).get
      (opt(singleNavigation(entityTypeName)) ^^ { case subPath => SingletonPath(singletonName, subPath) })
        .withFailureMessage(s"The URI contains an invalid path into the singleton: $entitySetName")
  }

  // A path into a collection of entities
  def collectionNavigation(contextTypeName: String): Parser[EntityCollectionPath] =
    opt("/" ~> qualifiedEntityTypeName.withFailureMessage("The URI contains an invalid path into a collection of entities")) into {
      derivedTypeNameOpt =>
        val entityTypeName = derivedTypeNameOpt.getOrElse(contextTypeName)

        // The filter here is to make the parser fail when it matches empty input
        (opt(collectionNavPath(entityTypeName)) filter { case path => derivedTypeNameOpt.isDefined || path.isDefined }) ^^ {
          case subPath => EntityCollectionPath(derivedTypeNameOpt, subPath)
        }
    }

  // Path used in collectionNavigation
  def collectionNavPath(contextTypeName: String): Parser[PathSegment] =
    keyPredicatePathSegment(contextTypeName) | collectionPath(contextTypeName) | ref

  // A key predicate path into a collection of entities
  def keyPredicatePathSegment(contextTypeName: String): Parser[KeyPredicatePath] =
    keyPredicate(contextTypeName) ~ opt(singleNavigation(contextTypeName)) ^^ {
      case keyPredicate ~ subPath => KeyPredicatePath(keyPredicate, subPath)
    }

  def keyPredicate(contextTypeName: String): Parser[KeyPredicate] = simpleKey | compoundKey(contextTypeName)

  def simpleKey: Parser[SimpleKeyPredicate] = "(" ~> primitiveLiteral <~ ")" ^^ SimpleKeyPredicate

  def compoundKey(contextTypeName: String): Parser[CompoundKeyPredicate] =
    "(" ~> rep1sep(keyValuePair(contextTypeName), ",") <~ ")" ^^ {
      case values => CompoundKeyPredicate(values.toMap)
    }

  def keyValuePair(contextTypeName: String): Parser[(String, Literal)] =
    (primitiveKeyProperty(contextTypeName) | keyPropertyAlias(contextTypeName)) ~ ("=" ~> primitiveLiteral) ^^ {
      case key ~ value => (key, value)
    }

  // Note: Check that alias is a valid key property alias for the relevant entity type
  def keyPropertyAlias(contextTypeName: String): Parser[String] = odataIdentifier

  // A path into a single entity
  def singleNavigation(contextTypeName: String): Parser[EntityPath] =
    opt("/" ~> qualifiedEntityTypeName.withFailureMessage("The URI contains an invalid path into an entity")) into {
      derivedTypeNameOpt =>
        val entityTypeName = derivedTypeNameOpt.getOrElse(contextTypeName)

        // The filter here is to make the parser fail when it matches empty input
        (opt(singleNavPath(entityTypeName)) filter { case path => derivedTypeNameOpt.isDefined || path.isDefined }) ^^ {
          case subPath => EntityPath(derivedTypeNameOpt, subPath)
        }
    }

  // Path used in singleNavigation
  def singleNavPath(contextTypeName: String): Parser[PathSegment] =
    ("/" ~> propertyPath(contextTypeName)) | ("/" ~> boundOperation(contextTypeName)) | ref | value

  // A path from a single entity or complex object to a property of that entity or complex object
  def propertyPath(contextTypeName: String): Parser[PropertyPath] =
    (entityNavPropertyPath(contextTypeName) | entityColNavPropertyPath(contextTypeName) |
    complexPropertyPath(contextTypeName) | complexColPropertyPath(contextTypeName) |
    primitivePropertyPath(contextTypeName) | primitiveColPropertyPath(contextTypeName) |
    streamPropertyPath(contextTypeName))
      .withFailureMessage("The URI contains an invalid path to a property of an entity or complex object")

  // A path to a navigation property; the type of the navigation property is a singular entity type (not a collection)
  def entityNavPropertyPath(contextTypeName: String): Parser[PropertyPath] =
    entityNavigationProperty(contextTypeName) into {
      propertyName =>
        val propertyTypeName = getSinglePropertyTypeName(contextTypeName, propertyName).get
        opt(singleNavigation(propertyTypeName)) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path to a navigation property, the type of the navigation property is a collection of an entity type
  def entityColNavPropertyPath(contextTypeName: String): Parser[PropertyPath] =
    entityColNavigationProperty(contextTypeName) into {
      propertyName =>
        val propertyElementTypeName = getPropertyElementTypeName(contextTypeName, propertyName).get
        opt(collectionNavigation(propertyElementTypeName)) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path to a property; the type of the property is a singular complex type (not a collection)
  def complexPropertyPath(contextTypeName: String): Parser[PropertyPath] =
    complexProperty(contextTypeName) into {
      propertyName =>
        val propertyTypeName = getSinglePropertyTypeName(contextTypeName, propertyName).get
        opt(complexPath(propertyTypeName)) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path to a property; the type of the property is a collection of a complex type
  def complexColPropertyPath(contextTypeName: String): Parser[PropertyPath] =
    complexColProperty(contextTypeName) into {
      propertyName =>
        val propertyElementTypeName = getPropertyElementTypeName(contextTypeName, propertyName).get
        opt(collectionPath(propertyElementTypeName)) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path to a property; the type of the property is a singular primitive type (not a collection)
  def primitivePropertyPath(contextTypeName: String): Parser[PropertyPath] =
    primitiveProperty(contextTypeName) into {
      propertyName =>
        val propertyTypeName = getSinglePropertyTypeName(contextTypeName, propertyName).get
        opt(singlePath(propertyTypeName)) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path to a property; the type of the property is a collection of a primitive type
  def primitiveColPropertyPath(contextTypeName: String): Parser[PropertyPath] =
    primitiveColProperty(contextTypeName) into {
      propertyName =>
        val propertyElementTypeName = getPropertyElementTypeName(contextTypeName, propertyName).get
        opt(collectionPath(propertyElementTypeName)) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path to a property; the property is a stream property
  def streamPropertyPath(contextTypeName: String): Parser[PropertyPath] =
    streamProperty(contextTypeName) into {
      propertyName =>
        // Note: What is the property type in case of a stream property?
        opt(boundOperation("TODO.TODO")) ^^ { case subPath => PropertyPath(propertyName, subPath) }
    }

  // A path into a collection of complex objects or primitive values
  def collectionPath(contextTypeName: String): Parser[PathSegment] = count | "/" ~> boundOperation(contextTypeName)

  // A path into a single primitive value
  def singlePath(contextTypeName: String): Parser[PathSegment] = value | boundOperation(contextTypeName)

  // A path into a single complex object
  def complexPath(contextTypeName: String): Parser[ComplexPath] =
    opt("/" ~> qualifiedComplexTypeName) into {
      derivedTypeNameOpt =>
        val complexTypeName = derivedTypeNameOpt.getOrElse(contextTypeName)

        (("/" ~> propertyPath(complexTypeName)) | ("/" ~> boundOperation(complexTypeName))) ^^ {
          case subPath => ComplexPath(derivedTypeNameOpt, Some(subPath))
        }
    }

  def count: Parser[CountPath.type] = "/$count" ^^^ CountPath

  def ref: Parser[RefPath.type] = "/$ref" ^^^ RefPath

  def value: Parser[ValuePath.type] = "/$value" ^^^ ValuePath

  // Call to an imported operation (an operation is an action or a function)
  def operationImportCall: Parser[OperationImportCall] = actionImportCall | functionImportCall

  // Call to an imported action
  def actionImportCall: Parser[ActionImportCall] = actionImport ^^ ActionImportCall

  // Call to an imported function
  def functionImportCall: Parser[FunctionImportCall] = entityFunctionImportCall | entityColFunctionImportCall |
    complexFunctionImportCall | complexColFunctionImportCall | primitiveFunctionImportCall

  def generalFunctionImportCall(pathSegmentParser: String => Parser[PathSegment]): Parser[FunctionImportCall] = functionImport into {
    functionImportName =>
      opt(functionParameters) ~ opt(pathSegmentParser(getFunctionImportReturnType(functionImportName).get)) ^^ {
        case args ~ subPath => FunctionImportCall(functionImportName, args, subPath)
      }
  }

  // Call to an imported function that returns a single entity
  def entityFunctionImportCall: Parser[FunctionImportCall] = generalFunctionImportCall(singleNavigation)

  // Call to an imported function that returns a collection of entities
  def entityColFunctionImportCall: Parser[FunctionImportCall] = generalFunctionImportCall(collectionNavigation)

  // Call to an imported function that returns a single complex object
  def complexFunctionImportCall: Parser[FunctionImportCall] = generalFunctionImportCall(complexPath)

  // Call to an imported function that returns a collection of complex objects
  // Call to an imported function that returns a collection of primitive values
  def complexColFunctionImportCall: Parser[FunctionImportCall] = generalFunctionImportCall(collectionPath)

  // Call to an imported function that returns a single primitive value
  def primitiveFunctionImportCall: Parser[FunctionImportCall] = generalFunctionImportCall(singlePath)

  // Call to a bound operation (an operation is an action or a function)
  def boundOperation(contextTypeName: String): Parser[BoundOperationCallPath] =
    boundActionCall(contextTypeName) | boundFunctionCall(contextTypeName)

  // Call to a bound action
  def boundActionCall(contextTypeName: String): Parser[BoundActionCallPath] = namespace ~ action ^^ {
    case ns ~ actionName => BoundActionCallPath(ns + actionName)
  }

  // Call to a bound function
  def boundFunctionCall(contextTypeName: String): Parser[BoundFunctionCallPath] = boundEntityFuncCall(contextTypeName) |
    boundEntityColFuncCall(contextTypeName) | boundComplexFuncCall(contextTypeName) |
    boundComplexColFuncCall(contextTypeName) | boundPrimitiveFuncCall(contextTypeName)

  def generalBoundFunctionCall(pathSegmentParser: String => Parser[PathSegment]): Parser[BoundFunctionCallPath] = namespace into {
    namespaceName =>
      function into {
        functionName =>
          opt(functionParameters) ~ opt(pathSegmentParser(getFunctionReturnType(functionName).get)) ^^ {
            case args ~ subPath => BoundFunctionCallPath(namespaceName + functionName, args, subPath)
          }
      }
  }

  // Call to a bound function that returns a single entity
  def boundEntityFuncCall(contextTypeName: String): Parser[BoundFunctionCallPath] = generalBoundFunctionCall(singleNavigation)

  // Call to a bound function that returns a collection of entities
  def boundEntityColFuncCall(contextTypeName: String): Parser[BoundFunctionCallPath] = generalBoundFunctionCall(collectionNavigation)

  // Call to a bound function that returns a single complex object
  def boundComplexFuncCall(contextTypeName: String): Parser[BoundFunctionCallPath] = generalBoundFunctionCall(complexPath)

  // Call to a bound function that returns a collection of complex objects
  // Call to a bound function that returns a collection of primitive values
  def boundComplexColFuncCall(contextTypeName: String): Parser[BoundFunctionCallPath] = generalBoundFunctionCall(collectionPath)

  // Call to a bound function that returns a single primitive value
  def boundPrimitiveFuncCall(contextTypeName: String): Parser[BoundFunctionCallPath] = generalBoundFunctionCall(singlePath)

  def functionParameters: Parser[Map[String, FunctionParam]] =
    "(" ~> repsep(functionParameter, ",") <~ ")" ^^ { case params => params.toMap }

  def functionParameter: Parser[(String, FunctionParam)] =
    odataIdentifier ~ ("=" ~> (aliasFunctionParam | literalFunctionParam)) ^^ { case name ~ param => (name, param) }

  def aliasFunctionParam: Parser[AliasFunctionParam] = "@" ~> odataIdentifier ^^ AliasFunctionParam

  def literalFunctionParam: Parser[LiteralFunctionParam] = primitiveLiteral ^^ LiteralFunctionParam

  // Crossjoin resource path
  def crossjoin: Parser[CrossJoinPath] =
    "$crossjoin(" ~> rep1sep(entitySetName, ",") <~ ")" ^^ CrossJoinPath

  // All resource path
  def all: Parser[AllPath.type] = "$all" ^^^ AllPath



  // Determine the type that a resource path resolves to
  def resolveResourcePathTypeName(resourcePath: ResourcePath): String = resourcePath match {
    case EntitySetPath(entitySetName, subPath) =>
      resolvePathSegmentTypeName(getEntitySetTypeName(entitySetName).get, subPath)

    case SingletonPath(singletonName, subPath) =>
      resolvePathSegmentTypeName(getSingletonTypeName(singletonName).get, subPath)

    case ActionImportCall(actionName) =>
      // Note: Determine the type that the action resolves to
      "UNKNOWN.UNKNOWN"

    case FunctionImportCall(functionName, args, subPath) =>
      // Note: Determine the type that the function resolves to
      resolvePathSegmentTypeName("UNKNOWN.UNKNOWN", subPath)

    case CrossJoinPath(entitySetNames) =>
      // Note: This is hard, because this doesn't resolve to a type name. Instead, query options must address types by
      // starting with one of the entity set names mentioned in the crossjoin. Like this:
      //
      //    http://host/service/$crossjoin(Products,Sales)?$filter=Products/ID eq Sales/ProductID
      //
      // See OData Version 4.0 Part 2: URL Conventions paragraph 4.11
      // NOTE: This syntax is not in the OData ABNF syntax! That seems to be an error in the ABNF specification.
      "UNKNOWN.UNKNOWN"

    case AllPath =>
      // Note: Similar problem as with CrossJoinPath.
      "UNKNOWN.UNKNOWN"
  }

  def resolvePathSegmentTypeName(contextTypeName: String, pathSegment: Option[PathSegment]): String =
    pathSegment match {
      case Some(EntityCollectionPath(derivedTypeName, subPath)) =>
        resolvePathSegmentTypeName(derivedTypeName.getOrElse(contextTypeName), subPath)

      case Some(KeyPredicatePath(_, subPath)) =>
        resolvePathSegmentTypeName(contextTypeName, subPath)

      case Some(EntityPath(derivedTypeName, subPath)) =>
        resolvePathSegmentTypeName(derivedTypeName.getOrElse(contextTypeName), subPath)

      case Some(ComplexPath(derivedTypeName, subPath)) =>
        resolvePathSegmentTypeName(derivedTypeName.getOrElse(contextTypeName), subPath)

      case Some(PropertyPath(propertyName, subPath)) =>
        val subPathContextTypeName = getSinglePropertyTypeName(contextTypeName, propertyName)
          .orElse(getPropertyElementTypeName(contextTypeName, propertyName)).get
        resolvePathSegmentTypeName(subPathContextTypeName, subPath)

      case Some(CountPath) => contextTypeName
      case Some(RefPath) => contextTypeName
      case Some(ValuePath) => contextTypeName

      case Some(BoundActionCallPath(actionName)) =>
        // Note: Determine the type that the action resolves to
        "UNKNOWN.UNKNOWN"

      case Some(BoundFunctionCallPath(functionName, args, subPath)) =>
        // Note: Determine the type that the function resolves to
        resolvePathSegmentTypeName("UNKNOWN.UNKNOWN", subPath)

      case None => contextTypeName
    }
}
