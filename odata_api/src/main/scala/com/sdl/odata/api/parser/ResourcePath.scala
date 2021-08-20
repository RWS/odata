/*
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
package com.sdl.odata.api.parser

// resourcePath
sealed trait ResourcePath

// Resource path that starts at an entity set
// Resolves to the collection of entities in the entity set
case class EntitySetPath(entitySetName: String, subPath: Option[EntityCollectionPath]) extends ResourcePath with SubPathSegment

// Resource path that starts at a singleton entity
// Resolves to the singleton entity instance
case class SingletonPath(singletonName: String, subPath: Option[EntityPath]) extends ResourcePath with SubPathSegment

// Call to an imported operation (an operation is an action or a function)
// Resolves to whatever the operation returns
sealed trait OperationImportCall extends ResourcePath

// Call to an imported action
case class ActionImportCall(actionName: String) extends OperationImportCall

// Call to an imported function
case class FunctionImportCall(functionName: String, args: Option[Map[String, FunctionParam]],
                              subPath: Option[PathSegment]) extends OperationImportCall with SubPathSegment

sealed trait FunctionParam
case class AliasFunctionParam(alias: String) extends FunctionParam
case class LiteralFunctionParam(value: Literal) extends FunctionParam

// Crossjoin resource path
// See OData Version 4.0 Part 2: URL Conventions paragraph 4.11
case class CrossJoinPath(entitySetNames: List[String]) extends ResourcePath

// All resource path
// See OData Version 4.0 Part 2: URL Conventions paragraph 4.12
case object AllPath extends ResourcePath


// Path segment - allows navigation into (collections of) entities, complex objects and primitive values
sealed trait PathSegment

trait SubPathSegment {
  def subPath: Option[PathSegment]
}

// A path into a collection of entities
// Possible subpath types: KeyPredicatePath, CountPath, BoundOperationCallPath, RefPath
case class EntityCollectionPath(derivedTypeName: Option[String], subPath: Option[PathSegment]) extends PathSegment with SubPathSegment

// A key predicate path into a collection of entities
// Resolves to a single entity of the collection (which matches the specified key)
case class KeyPredicatePath(keyPredicate: KeyPredicate, subPath: Option[EntityPath]) extends PathSegment with SubPathSegment

sealed trait KeyPredicate
case class SimpleKeyPredicate(value: Literal) extends KeyPredicate
case class CompoundKeyPredicate(values: Map[String, Literal]) extends KeyPredicate

// A path into a single entity
// Possible subpath types: PropertyPath, BoundOperationCallPath, RefPath, ValuePath
case class EntityPath(derivedTypeName: Option[String], subPath: Option[PathSegment]) extends PathSegment with SubPathSegment

// A path into a single complex object
// Possible subpath types: PropertyPath, BoundOperationCallPath
case class ComplexPath(derivedTypeName: Option[String], subPath: Option[PathSegment]) extends PathSegment with SubPathSegment

// A path from a single entity or complex object to a property of that entity or complex object
// Resolves to the type of the property
case class PropertyPath(propertyName: String, subPath: Option[PathSegment]) extends PathSegment with SubPathSegment

// The '/$count' path segment
case object CountPath extends PathSegment

// The '/$ref' path segment
case object RefPath extends PathSegment

// The '/$value' path segment
case object ValuePath extends PathSegment

// Call to a bound operation (an operation is an action or a function)
// Resolves to whatever the operation returns
sealed trait BoundOperationCallPath extends PathSegment

// Call to a bound action
case class BoundActionCallPath(actionName: String) extends BoundOperationCallPath

// Call to a bound function
case class BoundFunctionCallPath(functionName: String, args: Option[Map[String, FunctionParam]],
                                 subPath: Option[PathSegment]) extends BoundOperationCallPath with SubPathSegment
