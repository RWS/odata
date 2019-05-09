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
package com.sdl.odata.api.parser

import com.sdl.odata.api.ODataNotImplementedException
import com.sdl.odata.api.edm.model._
import com.sdl.odata.util.PrimitiveUtil
import com.sdl.odata.util.edm.EntityDataModelUtil
import com.sdl.odata.util.edm.EntityDataModelUtil._

/**
 * Target type of an URI: the type of entity that will be the result of a query for an URI.
 *
 * @param typeName The name of the type.
 * @param isCollection `true` if a collection is expected, `false` if a single value is expected.
 * @param propertyName The name of a property that's being selected from an entity.
 */
case class TargetType(typeName: String, isCollection: Boolean, propertyName: Option[String] = None)

/**
 * Information about an entity that is involved in a link.
 *
 * @param entityType The name of the entity type.
 * @param navigationProperty The navigation property that contains the link.
 * @param entityKey The key of an instance of the entity type, that indicates in which entity the link will be created
 *                  or deleted.
 */
case class FromEntity(entityType: EntityType, navigationProperty: NavigationProperty, entityKey: Map[String, AnyRef])

/**
 * Utility functions to extract useful information from the `ODataUri`.
 */
object ODataUriUtil {

  /**
   * Get the list of query options from an `ODataUri`.
   *
   * @param odataUri The `ODataUri`.
   * @return The list of query options, or an empty list if none are found.
   */
  def getQueryOptions(odataUri: ODataUri): List[QueryOption] = odataUri match {
    case ODataUri(_, opts: QueryOptions) => opts.options
    case ODataUri(_, ServiceRootUri(Some(mediaType))) => List(new FormatOption(mediaType))
    case ODataUri(_, MetadataUri(Some(mediaType), _)) => List(new FormatOption(mediaType))
    case _ => List.empty
  }

  /**
   * Get the '$format' query option of an `ODataUri` if it is present.
   *
   * @param odataUri The `ODataUri`.
   * @return `Some(FormatOption)` containing the format option or `None` if the URI does not have a format option.
   */
  def getFormatOption(odataUri: ODataUri): Option[FormatOption] =
    getQueryOptions(odataUri).filter(_.isInstanceOf[FormatOption]).map(_.asInstanceOf[FormatOption]).headOption

  /**
   * Get the '$id' query option of an `ODataUri` if it is present.
   *
   * @param odataUri The `ODataUri`.
   * @return `Some(IdOption)` containing the id option or `None` if the URI does not have an id option.
   */
  def getIdOption(odataUri: ODataUri): Option[IdOption] =
    getQueryOptions(odataUri).filter(_.isInstanceOf[IdOption]).map(_.asInstanceOf[IdOption]).headOption

  /**
   * Get the expand options from an `ODataUri`.
   *
   * @param odataUri The `ODataUri`.
   * @return The list of expand options, or an empty list if none are found.
   */
  def getExpandOptions(odataUri: ODataUri): List[ExpandOption] =
    getQueryOptions(odataUri).filter(_.isInstanceOf[ExpandOption]).map(_.asInstanceOf[ExpandOption])

  /**
   * Get the expand items from an `ODataUri`.
   *
   * @param odataUri The `ODataUri`.
   * @return The list of expand items, or an empty list if none are found.
   */
  def getExpandItems(odataUri: ODataUri): List[ExpandItem] =
    getExpandOptions(odataUri: ODataUri).flatMap(_.items)

  /**
   * Gets the "simple" expand property names. This gets the names of the navigation properties in all expand items
   * in all expand options in the URI, but only for those in which the path in the expand item consists of only
   * the navigation property name. Expand items where the path is more sophisticated are skipped.
   *
   * @param odataUri The `ODataUri`.
   * @return The list of "simple" expand property names.
   */
  def getSimpleExpandPropertyNames(odataUri: ODataUri): List[String] =
    getExpandItems(odataUri) flatMap {
      case expandItem: PathExpandItem =>
        expandItem.path match {
          case segment: NavigationPropertyExpandPathSegment => List(segment.propertyName)

          case _ => List.empty // Skip items where the path is not a single NavigationPropertyExpandPathSegment
        }

      case _ => List.empty // Skip items that are not a PathExpandItem
    }

  /**
   * The method extracts an Entity set name from a given ODataUri
   *
   * @param odataUri The `ODataUri`
   * @return Entity set name
   */
  def getEntitySetName(odataUri: ODataUri): Option[String] = {
    def handleResourcePath(resourcePath: ResourcePath): Option[String] = resourcePath match {
      case EntitySetPath(entitySetName, None) => Some(entitySetName)
      case EntitySetPath(_, Some(entityCollectionPath)) => handlePathSegment(entityCollectionPath)
      case _ => None
    }

    def handlePathSegment(pathSegment: PathSegment): Option[String] = pathSegment match {
      case EntityCollectionPath(_, Some(keyPredicatePath)) => handlePathSegment(keyPredicatePath)
      case KeyPredicatePath(_, Some(entityPath)) => handlePathSegment(entityPath)
      case EntityPath(_, Some(propertyPath)) => handlePathSegment(propertyPath)
      case PropertyPath(entitySetName, _) => Some(entitySetName)
      case _ => None
    }

    odataUri match {
      case ODataUri(_, ResourcePathUri(resourcePath, _)) => handleResourcePath(resourcePath)
      case _ => None
    }
  }

  /**
   * If the given `ODataUri` refers to a singleton, then this method returns a `Some` containing the name of the
   * singleton, otherwise it returns `None`.
   *
   * @param odataUri The `ODataUri`
   * @return An option containing the name of the singleton or `None`.
   */
  def getSingletonName(odataUri: ODataUri): Option[String] = odataUri match {
    case ODataUri(_, ResourcePathUri(SingletonPath(singletonName, _), _)) => Some(singletonName)
    case _ => None
  }

  /**
   * The method builds Entity set URL based on given ODataUri
   *
   * @param oDataUri The given OData URI.
   * @return The built entity set URL
   */
  def getEntitySetId(oDataUri: ODataUri): Option[String] = {
    def handleKeyPredicatePath(typeName: String, keyPredicate: KeyPredicate, subPath: Option[PathSegment], acc: String) = keyPredicate match {
      case SimpleKeyPredicate(literal) => getContextFromSubPath(typeName, subPath, acc, Some(formatLiteral(literal)))
      case CompoundKeyPredicate(literals) => getContextFromSubPath(typeName, subPath, acc, Some(compoundKeyToString(literals)))
    }

    def handleEntityPath(typeName: String, derivedTypeOption: Option[String], pathSegment: Option[PathSegment], acc: String, keyPredicateValue: Option[String]) = {
      val tuple = (derivedTypeOption, keyPredicateValue, pathSegment)
      tuple match {
        case (None, None, subPath) => getContextFromSubPath(typeName, subPath, acc, None)
        case (None, Some(value), subPath) => getContextFromSubPath(typeName, subPath, s"$acc($value)", None)
        case (Some(derivedType), Some(value), None) => getContextFromSubPath(derivedType, None, s"$acc/$derivedType($value)", keyPredicateValue)
        case (Some(derivedType), None, subPath) => getContextFromSubPath(derivedType, subPath, s"$acc/$derivedType", None)
        case (Some(derivedType), Some(value), subPath) => getContextFromSubPath(derivedType, subPath, s"$acc($value)/$derivedType", None)
      }
    }

    def getContextFromSubPath(typeName: String, pathSegment: Option[PathSegment], acc: String, keyPredicateValue: Option[String]): Option[String] = {
      val tuple = (pathSegment, keyPredicateValue)
      tuple match {
        case (Some(PropertyPath(propertyName, subPath)), _) => getContextFromSubPath(typeName, subPath, s"$acc/$propertyName", keyPredicateValue)
        case (Some(ComplexPath(derivedType, subPath)), _) => handleEntityPath(typeName, derivedType, subPath, acc, keyPredicateValue)
        case (Some(EntityPath(derivedType, subPath)), Some(value)) => handleEntityPath(typeName, derivedType, subPath, s"$acc($value)", None)
        case (Some(KeyPredicatePath(keyPredicate, subPath)), _) => handleKeyPredicatePath(typeName, keyPredicate, subPath, acc)
        case (None, Some(value)) => Some(s"$acc($value)")
        case (_, _) => Some(acc)
      }
    }

    def handleSingletonPath(idUri: String, subPath: Option[EntityPath], singleton: String) = subPath match {
      case None => Some(s"$idUri$singleton")
      case Some(_) => getContextFromSubPath(singleton, subPath, s"$idUri$singleton", None)
    }

    def handleEntitySetPath(entitySetName: String, subPath: Option[EntityCollectionPath], acc: String) = subPath match {
      case Some(EntityCollectionPath(None, None)) => Some(s"$acc$entitySetName")
      case Some(EntityCollectionPath(Some(derivedType), None)) => Some(s"$acc$entitySetName/$derivedType")
      case Some(EntityCollectionPath(derivedTypeOption, pathSegment)) => derivedTypeOption match {
        case None => getContextFromSubPath(entitySetName, pathSegment, s"$acc$entitySetName", None)
        case Some(derivedType) => getContextFromSubPath(derivedType, pathSegment, s"$acc$entitySetName/$derivedType", None)
      }
      case None => Some(s"$acc$entitySetName")
    }

    oDataUri match {
      case ODataUri(serviceRoot, relativeUri) =>
        val idUri = s"$serviceRoot/"
        relativeUri match {
          case ResourcePathUri(EntitySetPath(entitySetName, subPath), _) => handleEntitySetPath(entitySetName, subPath, s"$idUri")
          case ResourcePathUri(SingletonPath(singleton, subPath), _) => handleSingletonPath(idUri, subPath, singleton)
          case _ => None
        }
      case _ => None
    }
  }

  def hasCountOption(uri: ODataUri): Boolean = {
    getQueryOptions(uri).exists {
      case CountOption(true) => true
      case _ => false
    }
  }

  /**
   * Builds the 'Context URL' from a given OData URI.
   * The context will be generated for entity set, entity, simple property and complex property (including derived types)
   *
   * @param odataUri The given OData URI.
   * @return The built 'Context URL'.
   */
  def getContextUrl(odataUri: ODataUri): Option[String] = {
    getContextUrl(odataUri, false)
  }

  /**
   * Builds the 'Context URL' from a given OData URI for a write operation.
   * The context will be generated for entity set, entity, simple property and complex property (including derived types)
   *
   * @param odataUri The given OData URI.
   * @return The built 'Context URL'.
   */
  def getContextUrlWriteOperation(odataUri: ODataUri): Option[String] = {
    getContextUrl(odataUri, true)
  }

  private def getContextUrl(odataUri: ODataUri, isWriteOperation: Boolean): Option[String] = {
    val ENTITY = "$entity"
    val METADATA = "$metadata"

    def handleKeyPredicatePath(typeName: String, keyPredicate: KeyPredicate, subPath: Option[PathSegment], acc: String) = keyPredicate match {
      case SimpleKeyPredicate(literal) => getContextFromSubPath(typeName, subPath, s"$acc$typeName", Some(formatLiteral(literal)))
      case CompoundKeyPredicate(literals) => getContextFromSubPath(typeName, subPath, s"$acc$typeName", Some(compoundKeyToString(literals)))
    }

    def handleEntityPath(typeName: String, derivedTypeOption: Option[String], pathSegment: Option[PathSegment], acc: String, keyPredicateValue: Option[String]) = {
      val tuple = (derivedTypeOption, keyPredicateValue, pathSegment)
      tuple match {
        case (None, None, subPath) => getContextFromSubPath(typeName, subPath, acc, None)
        case (None, Some(value), subPath) => getContextFromSubPath(typeName, subPath, s"$acc($value)", None)
        case (Some(derivedType), Some(value), None) => getContextFromSubPath(derivedType, None, s"$acc/$derivedType/$ENTITY", keyPredicateValue)
        case (Some(derivedType), None, subPath) => getContextFromSubPath(derivedType, subPath, s"$acc/$derivedType", None)
        case (Some(derivedType), Some(value), subPath) => getContextFromSubPath(derivedType, subPath, s"$acc($value)/$derivedType", None)
      }
    }

    def getContextFromSubPath(typeName: String, pathSegment: Option[PathSegment], acc: String, keyPredicateValue: Option[String]): Option[String] = pathSegment match {
      case Some(PropertyPath(propertyName, Some(EntityCollectionPath(derivedType, subPath)))) => getContextFromSubPath(propertyName, subPath, s"$acc/", keyPredicateValue)
      case Some(PropertyPath(propertyName, subPath)) => getContextFromSubPath(propertyName, subPath, s"$acc/$propertyName", keyPredicateValue)
      case Some(ComplexPath(derivedType, subPath)) => handleEntityPath(typeName, derivedType, subPath, acc, keyPredicateValue)
      case Some(EntityPath(derivedType, subPath)) => handleEntityPath(typeName, derivedType, subPath, acc, keyPredicateValue)
      case Some(KeyPredicatePath(_, None)) => getContextFromSubPath(typeName, None, s"$acc$typeName/$ENTITY", keyPredicateValue)
      case Some(KeyPredicatePath(keyPredicate, subPath)) => handleKeyPredicatePath(typeName, keyPredicate, subPath, acc)
      case Some(CountPath) => Some(acc)
      case Some(ValuePath) => Some(acc)
      case None => Some(acc)
    }

    def handleSingletonPath(metaDataUri: String, subPath: Option[EntityPath], singleton: String) = subPath match {
      case None => Some(s"$metaDataUri#$singleton")
      case Some(_) => getContextFromSubPath(singleton, subPath, s"$metaDataUri#$singleton", None)
    }

    def handleEntitySetPath(entitySetName: String, subPath: Option[EntityCollectionPath], acc: String) = subPath match {
      case Some(EntityCollectionPath(None, None)) => Some(s"$acc$entitySetName")
      case Some(EntityCollectionPath(Some(derivedType), None)) => Some(s"$acc$entitySetName/$derivedType")
      case Some(EntityCollectionPath(derivedTypeOption, pathSegment)) => derivedTypeOption match {
        case None => getContextFromSubPath(entitySetName, pathSegment, acc, None)
        case Some(derivedType) => getContextFromSubPath(derivedType, pathSegment, s"$acc$entitySetName/", None)
      }
      case None => Some(s"$acc$entitySetName")
    }

    (odataUri, isWriteOperation) match {
      case (ODataUri(serviceRoot, relativeUri), true) => relativeUri match {
        case ResourcePathUri(EntitySetPath(entitySetName, subPath), _) =>
          handleEntitySetPath(entitySetName, subPath, s"$serviceRoot/$METADATA#") map { value =>
            if (!value.endsWith(ENTITY)) value + "/" + ENTITY else value
          }
        case ResourcePathUri(SingletonPath(singleton, subPath), _) =>
          handleSingletonPath(s"$serviceRoot/$METADATA", subPath, singleton) map { value =>
            if (!value.endsWith(ENTITY)) value + "/" + ENTITY else value
          }
        case _ => None
      }
      case (ODataUri(serviceRoot, relativeUri), false) =>
        val metaDataUri = s"$serviceRoot/$METADATA"
        relativeUri match {
          case ServiceRootUri(format) => Some(metaDataUri)
          case ResourcePathUri(EntitySetPath(entitySetName, subPath), _) => handleEntitySetPath(entitySetName, subPath, s"$metaDataUri#")
          case ResourcePathUri(SingletonPath(singleton, subPath), _) => handleSingletonPath(metaDataUri, subPath, singleton)
          case _ => None
        }
      case _ => None
    }
  }

  /**
   * Resolve the target type for the given OData URI and Entity Data Model.
   *
   * @param odataUri The given OData URI.
   * @param entityDataModel The given Entity Data Model.
   * @return The resolved target type.
   */
  def resolveTargetType(odataUri: ODataUri, entityDataModel: EntityDataModel): Option[TargetType] = {
    def resolveTargetTypeUnnamedEntityUri(options: List[QueryOption]): Option[TargetType] = {
      // Note: Determine the target type from the '$id' option which must be in the list of options
      None
    }

    def resolveTargetTypeNamedEntityUri(derivedTypeName: String, options: List[QueryOption]): Option[TargetType] = {
      // Note: Is this always correct? There might be a '$select' option which changes the target type.
      Some(TargetType(derivedTypeName, false))
    }

    // Note: The options are not yet taken into account; they can influence the target type (for example '$select')

    def resolveTargetTypeResourcePath(resourcePath: ResourcePath, options: List[QueryOption]): Option[TargetType] = {
      def resolve(contextType: TargetType, segment: Option[PathSegment]): Option[TargetType] = segment match {
        case Some(EntityCollectionPath(derivedTypeName, subPath)) =>
          // A path to a collection of entities
          val contextTypeName = derivedTypeName.getOrElse(contextType.typeName)
          resolve(TargetType(contextTypeName, true), subPath)

        case Some(KeyPredicatePath(_, subPath)) =>
          // A path to a single entity out of a collection
          resolve(TargetType(contextType.typeName, false), subPath)

        case Some(EntityPath(derivedTypeName, subPath)) =>
          // A path to a single entity
          val contextTypeName = derivedTypeName.getOrElse(contextType.typeName)
          resolve(TargetType(contextTypeName, false), subPath)

        case Some(ComplexPath(derivedTypeName, subPath)) =>
          // A path to a single complex object
          val contextTypeName = derivedTypeName.getOrElse(contextType.typeName)
          resolve(TargetType(contextTypeName, false), subPath)

        case Some(PropertyPath(propertyName, subPath)) =>
          // A path to a property of a structured type (entity or complex object)
          val property = getAndCheckStructuredType(entityDataModel, contextType.typeName).getStructuralProperty(propertyName)
          if (property.isCollection) {
            resolve(TargetType(property.getElementTypeName, true, Some(propertyName)), subPath)
          } else {
            resolve(TargetType(property.getTypeName, false, Some(propertyName)), subPath)
          }

        case Some(CountPath) =>
          Some(TargetType(PrimitiveType.INT64.getFullyQualifiedName, false, Some("$count")))

        case Some(RefPath) => Some(contextType)
        case Some(ValuePath) => Some(contextType)

        case Some(BoundActionCallPath(actionName)) => {
          val action = getAndCheckAction(entityDataModel, actionName)
          val returnType = action.getReturnType
          val entitySet = entityDataModel.getEntityContainer.getEntitySet(returnType)
          resolve(TargetType(if (entitySet == null) returnType else entitySet.getTypeName,
            isCollection(entityDataModel, returnType)), None)
        }
        case Some(BoundFunctionCallPath(functionName, args, subPath)) =>
          val function = getAndCheckFunction(entityDataModel, functionName)
          val returnType = function.getReturnType
          val entitySet = entityDataModel.getEntityContainer.getEntitySet(returnType)
          resolve(TargetType(if (entitySet == null) returnType else entitySet.getTypeName,
            isCollection(entityDataModel, returnType)), subPath)

        case None => Some(contextType)
      }

      resourcePath match {
        case EntitySetPath(entitySetName, subPath) =>
          val contextTypeName = getAndCheckEntitySet(entityDataModel, entitySetName).getTypeName
          resolve(TargetType(contextTypeName, true), subPath)

        case SingletonPath(singletonName, subPath) =>
          val contextTypeName = getAndCheckSingleton(entityDataModel, singletonName).getTypeName
          resolve(TargetType(contextTypeName, false), subPath)

        case ActionImportCall(actionName) =>
          val actionImport = getAndCheckActionImport(entityDataModel, actionName)
          val returnType = actionImport.getAction.getReturnType
          val entitySet = entityDataModel.getEntityContainer.getEntitySet(returnType)

          resolve(TargetType(if (entitySet == null) returnType else entitySet.getTypeName,
            isCollection(entityDataModel, returnType)), None)

        case FunctionImportCall(functionName, _, subPath) =>
          val functionImport: FunctionImport = getAndCheckFunctionImport(entityDataModel, functionName)
          var returnType = functionImport.getFunction.getReturnType
          val entitySet = entityDataModel.getEntityContainer.getEntitySet(returnType)
          val matcher = COLLECTION_PATTERN.matcher(returnType)
          val collection = matcher.matches()
          if (collection) {
            returnType = matcher.group(1)
          }

          resolve(TargetType(
            if (entitySet == null) returnType else entitySet.getTypeName,
            if (collection) collection else isCollection(entityDataModel, returnType)), subPath)

        case CrossJoinPath(_) => None
        case AllPath => None
      }
    }

    odataUri match {
      case ODataUri(_, ServiceRootUri(_)) => None
      case ODataUri(_, MetadataUri(_, context)) => None
      case ODataUri(_, BatchUri) => None

      case ODataUri(_, EntityUri(None, options)) =>
        resolveTargetTypeUnnamedEntityUri(options)

      case ODataUri(_, EntityUri(Some(derivedTypeName), options)) =>
        resolveTargetTypeNamedEntityUri(derivedTypeName, options)

      case ODataUri(_, ResourcePathUri(resourcePath, options)) =>
        resolveTargetTypeResourcePath(resourcePath, options)

      case _ => None
    }
  }

  def getOperationReturnType(odataUri: ODataUri, entityDataModel: EntityDataModel): String = {
    if (ODataUriUtil.getActionCallName(odataUri).isDefined) {
      val fullyQualifiedActionName: String = ODataUriUtil.getActionCallName(odataUri).get
      val action: Action = EntityDataModelUtil.getAndCheckAction(entityDataModel, fullyQualifiedActionName)
      action.getReturnType
    }
    else if (ODataUriUtil.getFunctionCallName(odataUri).isDefined) {
      val fullyQualifiedFunctionName: String = ODataUriUtil.getFunctionCallName(odataUri).get
      val function: Function = EntityDataModelUtil.getAndCheckFunction(entityDataModel, fullyQualifiedFunctionName)
      function.getReturnType
    }
    else if (ODataUriUtil.getActionImportCallName(odataUri).isDefined) {
      val actionImport: ActionImport = entityDataModel.getEntityContainer.getActionImport(ODataUriUtil.getActionImportCallName(odataUri).get)
      actionImport.getAction.getReturnType
    }
    else if (ODataUriUtil.getFunctionImportCallName(odataUri).isDefined) {
      val functionImport: FunctionImport = entityDataModel.getEntityContainer.getFunctionImport(ODataUriUtil.getFunctionImportCallName(odataUri).get)
      functionImport.getFunction.getReturnType
    } else ""
  }

  /**
   * Determines if an URI ends with a ".../$count" path.
   *
   * @param odataUri The OData URI.
   * @return `true` if the URI ends with a ".../$count" path, `false` otherwise.
   */
  def isCountPathUri(odataUri: ODataUri): Boolean = endsWithPathSegment(odataUri, CountPath)

  /**
   * Determines if an URI ends with a ".../$ref" path.
   *
   * @param odataUri The OData URI.
   * @return `true` if the URI ends with a ".../$ref" path, `false` otherwise.
   */
  def isRefPathUri(odataUri: ODataUri): Boolean = endsWithPathSegment(odataUri, RefPath)

  /**
   * Determines if an URI ends with a ".../$value" path.
   *
   * @param odataUri The OData URI.
   * @return `true` if the URI ends with a ".../$value" path, `false` otherwise.
   */
  def isValuePathUri(odataUri: ODataUri): Boolean = endsWithPathSegment(odataUri, ValuePath)

  private def endsWithPathSegment(odataUri: ODataUri, endingSegment: PathSegment): Boolean = {
    def endsWithPathSegment(pathSegment: PathSegment): Boolean = pathSegment match {
      case parent: PathSegment with SubPathSegment =>
        if (parent.subPath.isDefined) endsWithPathSegment(parent.subPath.get) else false
      case _ => pathSegment == endingSegment
    }

    odataUri match {
      case ODataUri(_, ResourcePathUri(resourcePath: SubPathSegment, _)) =>
        if (resourcePath.subPath.isDefined) endsWithPathSegment(resourcePath.subPath.get) else false
      case _ => false
    }
  }

  def isFunctionCallUri(oDataUri: ODataUri): Boolean = getFunctionCallName(oDataUri).isDefined ||
    getFunctionImportCallName(oDataUri).isDefined

  def getFunctionCallName(oDataUri: ODataUri): Option[String] = {
    def endsWithPathSegment(pathSegment: PathSegment): Option[String] = pathSegment match {
      case BoundFunctionCallPath(functionName, _, _) => Some(functionName)
      case parent: PathSegment with SubPathSegment =>
        if (parent.subPath.isDefined) endsWithPathSegment(parent.subPath.get) else None
      case _ => None
    }

    oDataUri match {
      case ODataUri(_, ResourcePathUri(resourcePath: SubPathSegment, _)) =>
        if (resourcePath.subPath.isDefined) endsWithPathSegment(resourcePath.subPath.get) else None
      case _ => None
    }
  }

  def getFunctionImportCallName(oDataUri: ODataUri): Option[String] = {
    oDataUri match {
      case ODataUri(_, ResourcePathUri(FunctionImportCall(functionImportName, _, _), _)) => Some(functionImportName)
      case _ => None
    }
  }

  def getFunctionCallParameters(oDataUri: ODataUri): Option[Map[String, String]] = {
    def endsWithPathSegment(pathSegment: PathSegment): Option[Map[String, String]] = pathSegment match {
      case BoundFunctionCallPath(_, Some(args), _) => getParametersFromLiteralFunctions(args)
      case parent: PathSegment with SubPathSegment =>
        if (parent.subPath.isDefined) endsWithPathSegment(parent.subPath.get) else None
      case _ => None
    }

    oDataUri match {
      case ODataUri(_, ResourcePathUri(resourcePath: SubPathSegment, _)) =>
        if (resourcePath.subPath.isDefined) endsWithPathSegment(resourcePath.subPath.get) else None
      case _ => None
    }
  }

  def getFunctionImportCallParameters(oDataUri: ODataUri): Option[Map[String, String]] = {
    oDataUri match {
      case ODataUri(_, ResourcePathUri(FunctionImportCall(_, Some(args), _), _)) =>
        getParametersFromLiteralFunctions(args)
      case _ => None
    }
  }

  private def getParametersFromLiteralFunctions(parameters: Map[String, FunctionParam]): Option[Map[String, String]] = {
    Some(parameters.mapValues(_ match {
      case LiteralFunctionParam(value) => value match {
        case NullLiteral => null
        case default => getLiteralValue(default).toString
      }
      // TODO how alias values should be handled?
      case AliasFunctionParam(value) => value
    }))
  }

  /**
   * Determines if an URI is an action or action import call.
   *
   * @param odataUri The OData URI.
   * @return `true` if the URI is an action or action import call.
   */
  def isActionCallUri(odataUri: ODataUri): Boolean = getActionCallName(odataUri).isDefined || getActionImportCallName(odataUri).isDefined

  /**
   * Function returns the name of action if the given url is an action call.
   *
   * @param odataUri The OData URI.
   * @return The name of action if the given url is an action call.
   */
  def getActionCallName(odataUri: ODataUri): Option[String] = {
    def endsWithPathSegment(pathSegment: PathSegment): Option[String] = pathSegment match {
      case BoundActionCallPath(actionName) => Some(actionName)
      case parent: PathSegment with SubPathSegment =>
        if (parent.subPath.isDefined) endsWithPathSegment(parent.subPath.get) else None
      case _ => None
    }

    odataUri match {
      case ODataUri(_, ResourcePathUri(resourcePath: SubPathSegment, _)) =>
        if (resourcePath.subPath.isDefined) endsWithPathSegment(resourcePath.subPath.get) else None
      case _ => None
    }
  }

  /**
   * Function returns the name of action import if the given url is an action import call.
   *
   * @param odataUri The OData URI.
   * @return The name of action import if the given url is an action import call.
   */
  def getActionImportCallName(odataUri: ODataUri): Option[String] = {
    odataUri match {
      case ODataUri(_, ResourcePathUri(ActionImportCall(actionImportName), _)) => Some(actionImportName)
      case _ => None
    }
  }

  /**
   * Function returns the entity name bound to a bound operation
   *
   * @param odataUri The OData Uri
   * @return the name of bound entity name
   */
  def getBoundEntityName(odataUri: ODataUri): Option[String] = odataUri.relativeUri match {
    case ResourcePathUri(EntitySetPath(entityName, _), _) => Some(entityName)
    case ResourcePathUri(SingletonPath(singletonName, _), _) => Some(singletonName)
    case _ => None
  }

  /**
   * For a ".../$ref" URI, determine the `FromEntity` - the entity in which a link will be created or deleted.
   *
   * @param odataUri The OData URI.
   * @param entityDataModel The entity data model
   * @return `Some(FromEntity)` if the URI has the expected format and refers to a navigation property of an entity or
   *         singleton, `None` if this information could not be determined from the URI.
   */
  def getFromEntity(odataUri: ODataUri, entityDataModel: EntityDataModel): Option[FromEntity] = odataUri match {
    case ODataUri(_, ResourcePathUri(EntitySetPath(entitySetName,
    Some(EntityCollectionPath(_, Some(KeyPredicatePath(keyPredicate,
    Some(EntityPath(_, Some(PropertyPath(propertyName, _))))))))), _)) =>
      val entityType = getAndCheckEntityType(entityDataModel, getAndCheckEntitySet(entityDataModel, entitySetName).getTypeName)
      Some(FromEntity(entityType, entityType.getStructuralProperty(propertyName).asInstanceOf[NavigationProperty],
        getEntityKeyMap(odataUri, entityDataModel)))

    case ODataUri(_, ResourcePathUri(SingletonPath(singletonName,
    Some(EntityPath(_, Some(PropertyPath(propertyName, _))))), _)) =>
      val entityType = getAndCheckEntityType(entityDataModel, getAndCheckSingleton(entityDataModel, singletonName).getTypeName)
      Some(FromEntity(entityType, entityType.getStructuralProperty(propertyName).asInstanceOf[NavigationProperty],
        Map()))

    case _ => None
  }

  /**
   * Extract an entity with the keys populated based on the given OData URI and Entity Data Model.
   *
   * @param odataUri The given OData URI.
   * @param entityDataModel The Entity Data Model.
   * @return The entity with the keys populated.
   */
  def extractEntityWithKeys(odataUri: ODataUri, entityDataModel: EntityDataModel): Option[Any] = {
    resolveTargetType(odataUri, entityDataModel) match {
      case Some(TargetType(typeName, false, _)) =>
        val entityType = getAndCheckEntityType(entityDataModel, typeName)
        val entity = entityType.getJavaType.newInstance()

        if (!isSingletonEntity(entityDataModel, entity)) {
          val entityKeyMap = getEntityKeyMap(odataUri, entityDataModel)
          if (!hasSameNumberOfFields(entityKeyMap, entityType))
            None
          else {
            for ((key, value) <- entityKeyMap) {
              EntityDataModelUtil.setPropertyValue(entityType.getStructuralProperty(key), entity, value)
            }
          }
        }
        Some(entity)

      case _ => None
    }
  }

  def hasSameNumberOfFields(keyMap: Map[String, Any], entityType: EntityType): Boolean =
    keyMap.size == entityType.getKey.getPropertyRefs.size()

  /**
   * Get a map containing the entity key value(s) for the entity type specified in the OData URI.
   *
   * @param odataUri The specified OData URI.
   * @param entityDataModel The entity data model.
   * @return The map containing the entity key value(s).
   */
  def getEntityKeyMap(odataUri: ODataUri, entityDataModel: EntityDataModel): Map[String, AnyRef] = odataUri match {
    case ODataUri(_, ResourcePathUri(resourcePath, _)) => getEntityKeyMap(resourcePath, entityDataModel)

    case _ => throw new ODataUriParseException(
      "The URI does not have the expected format: http://.../odata.svc/EntitySetName(...)")
  }

  /**
   * Get a map containing the entity key value(s) from a resource path.
   *
   * @param resourcePath The resource path.
   * @param entityDataModel The entity data model.
   * @return The map containing the entity key value(s).
   */
  def getEntityKeyMap(resourcePath: ResourcePath, entityDataModel: EntityDataModel): Map[String, AnyRef] =
    resourcePath match {
      case EntitySetPath(entitySetName,
      Some(EntityCollectionPath(_, Some(KeyPredicatePath(SimpleKeyPredicate(value), _))))) =>
        val entityType = getAndCheckEntityType(entityDataModel, getAndCheckEntitySet(entityDataModel, entitySetName).getTypeName)
        Map(getEntityKey(entityType) -> getLiteralValue(value))

      case EntitySetPath(_,
      Some(EntityCollectionPath(_, Some(KeyPredicatePath(CompoundKeyPredicate(keyMap), _))))) =>
        keyMap map { case (key, value) => (key, getLiteralValue(value)) }

      case _ => throw new ODataUriParseException(
        "The resource path does not have the expected format: EntitySetName(...)")
    }

  def getLiteralValue(literal: Literal): AnyRef = literal match {
    case NullLiteral => null
    case TrueLiteral => java.lang.Boolean.TRUE
    case FalseLiteral => java.lang.Boolean.FALSE
    case NumberLiteral(value) => value
    case StringLiteral(value) => value
    case EnumLiteral(_, values) => values
    case LocalDateLiteral(date) => date
    case LocalTimeLiteral(time) => time
    case DateTimeLiteral(dateTime) => dateTime
    case PeriodLiteral(period) => period
    case GuidLiteral(guid) => guid
    case BinaryLiteral(bytes) => bytes
    case _ =>
      throw new ODataNotImplementedException("Unsupported literal: " + literal)
  }

  /**
   * Converts given literal to string.
   * All possible literal not covered but easily can extend by adding another pattern matching case
   *
   * @param literal represents Literal
   * @return converted string.
   */
  def formatLiteral(literal: Literal): String = literal match {
    case NullLiteral => "null"
    case TrueLiteral => "true"
    case FalseLiteral => "false"
    case NumberLiteral(value) => value.toString()
    case StringLiteral(value) => "'" + value.replaceAll("'", "''") + "'"
    case LocalDateLiteral(date) => date.toString
    case LocalTimeLiteral(time) => time.toString
    case DateTimeLiteral(dateTime) => dateTime.toString
    case PeriodLiteral(value) => s"duration'$value'"
    case GuidLiteral(value) => value.toString
    case _ => throw new ODataNotImplementedException("Unsupported literal: " + literal)
  }

  /**
   * This converts compound key to string
   *
   * @param literals map which represent compound key
   * @return converted string
   */
  def compoundKeyToString(literals: Map[String, Literal]): String =
    literals.foldRight(List(): List[String])((pair, acc) => (pair._1 + "=" + formatLiteral(pair._2)) :: acc).mkString(",")

  def getEntityKey(entityType: EntityType): String = {
    val keys = entityType.getKey.getPropertyRefs
    if (keys.size() == 1)
      entityType.getStructuralProperty(keys.get(0).getPath).getJavaField.getName
    else
      throw new ODataUriParseException(
        s"The entity type $entityType has a compound key, all key fields must be specified in the URI")
  }

  /**
   * Convert a given Scala list into a Java list.
   *
   * @param list The given Scala list.
   * @return The converted Java list.
   */
  def asJavaList[T](list: List[T]): java.util.List[T] = {
    import scala.collection.JavaConverters._
    list.asJava
  }

  /**
   * Convert a given Java list into a Scala list.
   *
   * @param list The given Java list.
   * @return The converted Scala list.
   */
  def asScalaList[T](list: java.util.List[T]): List[T] = {
    import scala.collection.JavaConverters._
    list.asScala.toList
  }

  /**
   * Convert a given Scala map into a Java map.
   *
   * @param map The given Scala map.
   * @tparam K The key type.
   * @tparam V The value type.
   * @return THe converted Java map
   */
  def asJavaMap[K, V](map: Map[K, V]): java.util.Map[K, V] = {
    import scala.collection.JavaConverters._
    map.asJava
  }

  /**
   * Convert a given Java map into a Scala map.
   *
   * @param map The given Java map.
   * @return The converted Scala map.
   */
  def asScalaMap[K, V](map: java.util.Map[K, V]): Map[K, V] = {
    import scala.collection.JavaConverters._
    map.asScala.toMap
  }
}
