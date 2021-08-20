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
package com.sdl.odata.processor

import com.sdl.odata.util.edm.EntityDataModelUtil
import com.sdl.odata.api.edm.ODataEdmException
import com.sdl.odata.api.edm.model._
import com.sdl.odata.api.parser._
import com.sdl.odata.api.processor.datasource.ODataDataSourceException
import com.sdl.odata.api.processor.query._
import com.sdl.odata.api.service.ODataRequestContext
import com.sdl.odata.api.{ODataBadRequestException, ODataNotImplementedException}

class QueryModelBuilder(entityDataModel: EntityDataModel) {

  def build(requestContext: ODataRequestContext): ODataQuery = requestContext.getUri match {
    case ODataUri(_, resourcePathUri: ResourcePathUri) =>
      buildFromResourcePathUri(resourcePathUri)

    case _ =>
      throw new ODataDataSourceException("The URI does not represent a valid query: " + requestContext.getRequest.getUri)
  }

  private def buildFromResourcePathUri(resourcePathUri: ResourcePathUri): ODataQuery = {
    // Build operation from resource path
    val operation = resourcePathUri match {
      case ResourcePathUri(EntitySetPath(entitySetName, subPath), _) =>
        buildFromPathSegment(SelectOperation(entitySetName), subPath)

      case ResourcePathUri(SingletonPath(singletonName, subPath), _) =>
        buildFromPathSegment(SelectOperation(singletonName), subPath)

      case ResourcePathUri(resourcePath, _) =>
        throw new ODataNotImplementedException("This type of resource path is not supported for queries: " + resourcePath)
    }

    // Apply query options
    ODataQuery(applyOptions(resourcePathUri.options, operation))
  }

  // NOTE: Derived type names are currently ignored. How this should be implemented depends on how class hierarchies
  // are mapped to database tables.

  private def buildFromPathSegment(context: QueryOperation, pathSegment: Option[PathSegment]): QueryOperation = pathSegment match {
    case Some(EntityCollectionPath(derivedTypeName, subPath)) => buildFromPathSegment(context, subPath)
    case Some(EntityPath(derivedTypeName, subPath)) => buildFromPathSegment(context, subPath)
    case Some(ComplexPath(derivedTypeName, subPath)) => buildFromPathSegment(context, subPath)

    case Some(KeyPredicatePath(keyPredicate, subPath)) =>
      buildFromPathSegment(buildFromKeyPredicate(context, keyPredicate), subPath)

    case Some(PropertyPath(propertyName, subPath)) =>
      // If the property is a navigation property, then build a JoinOperation, otherwise a SelectPropertiesOperation
      val property = getTargetType(context).getStructuralProperty(propertyName)
      if (property.isInstanceOf[NavigationProperty]) {
        val propertyTypeName = if (property.isCollection) property.getElementTypeName else property.getTypeName
        val entityName = EntityDataModelUtil.getEntityNameByEntityTypeName(entityDataModel, propertyTypeName)
        buildFromPathSegment(JoinOperation(context, SelectOperation(entityName), propertyName, JoinSelectRight), subPath)
      } else {
        buildFromPathSegment(context.select(propertyName), subPath)
      }

    case Some(CountPath) =>
      CountOperation(context, trueFalse = true)

    case Some(ValuePath) => context

    case Some(_) =>
      throw new ODataNotImplementedException("This type of path segment is not supported for queries: " + pathSegment.get)

    case None => context
  }

  private def buildFromKeyPredicate(source: QueryOperation, keyPredicate: KeyPredicate): QueryOperation = keyPredicate match {
    case SimpleKeyPredicate(value) => buildFromSimpleKeyPredicate(source, value)
    case CompoundKeyPredicate(values) => buildFromCompoundKeyPredicate(source, values)
  }

  private def buildFromSimpleKeyPredicate(source: QueryOperation, value: Literal): QueryOperation = {
    val entityType = getTargetType(source)

    // Get the key; there must be exactly one key field
    val key = entityType.asInstanceOf[EntityType].getKey.getPropertyRefs
    if (key.size() != 1) {
      throw new ODataBadRequestException(s"The entity type '$entityType' has a compound key. All key fields must " +
        "be specified in the key predicate.")
    }

    // NOTE: We assume here that the path consists of just the name of the key field
    val keyFieldName = key.get(0).getPath

    val keyMap = Map(keyFieldName -> wrap(ODataUriUtil.getLiteralValue(value)))
    SelectByKeyOperation(source, keyMap)
  }

  private def buildFromCompoundKeyPredicate(source: QueryOperation, values: Map[String, Literal]): QueryOperation = {

    val keyMap = values map { case (name, value) => (name, wrap(ODataUriUtil.getLiteralValue(value))) }
    SelectByKeyOperation(source, keyMap)
  }

  //  Ugly method to box values (make sure the value is an AnyRef)
  private def wrap(value: Any): AnyRef = value match {
    case null => null
    case true => java.lang.Boolean.TRUE
    case false => java.lang.Boolean.FALSE
    case _ => value.asInstanceOf[AnyRef]
  }

  private def getTargetType(source: QueryOperation): EntityType = {
    val sourceName = source.entitySetName

    // Get the name of the entity type of the entity set or singleton
    val typeName = Option(entityDataModel.getEntityContainer.getEntitySet(sourceName)).map(_.getTypeName)
      .orElse(Option(entityDataModel.getEntityContainer.getSingleton(sourceName)).map(_.getTypeName))
    if (typeName.isEmpty) {
      throw new ODataEdmException("Cannot determine type name for entity set or singleton: " + sourceName)
    }

    // Get the entity type itself
    val entityType = entityDataModel.getType(typeName.get)
    if (entityType == null) {
      throw new ODataEdmException("Type not found in entity data model: " + typeName.get)
    }

    // Check that it is indeed an entity type
    if (entityType.getMetaType != MetaType.ENTITY) {
      throw new ODataEdmException("Type is not an entity type: " + entityType)
    }

    entityType.asInstanceOf[EntityType]
  }

  private def applyOptions(options: List[QueryOption], operation: QueryOperation): QueryOperation =
    options.foldLeft(operation)(applyOption)

  private def applyOption(source: QueryOperation, option: QueryOption): QueryOperation = option match {
    case FilterOption(expression) => applyFilterOption(source, expression)
    // ApplyOption
    case ApplyOption(applyMethod) => applyApplyOption(source, applyMethod)
    case TopOption(count) => LimitOperation(source, count)
    case SkipOption(count) => SkipOperation(source, count)
    case CountOption(trueFalse) => CountOperation(source, trueFalse)
    case ExpandOption(items) => applyExpandOption(source, items)
    case OrderByOption(items) => applyOrderByOption(source, items)

    // SelectOption
    case SelectOption(items) => applySelectOption(source, items)

    case FormatOption(_) => source
    case AliasAndValueOption(_, _) => source
    case CustomOption(_, _) => source

    case _ =>
      throw new ODataNotImplementedException("This type of option is not supported for queries: " + option)
  }
  // SelectOption handling
  private def applySelectOption(source: QueryOperation, items: List[SelectItem]): QueryOperation = {
    def getSelectPath(segment: SelectPathSegment): String = segment match {
      case ComplexPropertySelectPathSegment(propertyName, _, subPath) => propertyName + "." + getSelectPath(subPath.get)
      case TerminalPropertySelectPathSegment(propertyName) => propertyName
    }

    val selectField = items flatMap {
      case selectItem: PathSelectItem => List(getSelectPath(selectItem.path))
      case _ => Nil
    }
    source.select(selectField :_*)
  }

  private def applyFilterOption(source: QueryOperation, expression: BooleanExpr): QueryOperation = {
    def getFilterPropertyPath(expr: Expression): String = getPropertyPath(expr) match {
      case Some(propertyPath) => propertyPath
      case None =>
        throw new ODataNotImplementedException("Unsupported expression type for 'filter': " + expr)
    }

    def createCriteriaValue(expr: Expression): CriteriaValue = expr match {
      case LiteralExpr(literal) => LiteralCriteriaValue(ODataUriUtil.getLiteralValue(literal))

      case EntityPathExpr(_, Some(subPath)) => PropertyCriteriaValue(getFilterPropertyPath(subPath))
      case ComplexPathExpr(_, Some(subPath)) => PropertyCriteriaValue(getFilterPropertyPath(subPath))
      case PropertyPathExpr(_, _) => PropertyCriteriaValue(getFilterPropertyPath(expr))

      case AddExpr(left, right) => createCriteriaValue(left).add(createCriteriaValue(right))
      case SubExpr(left, right) => createCriteriaValue(left).sub(createCriteriaValue(right))
      case MulExpr(left, right) => createCriteriaValue(left).mul(createCriteriaValue(right))
      case DivExpr(left, right) => createCriteriaValue(left).div(createCriteriaValue(right))
      case ModExpr(left, right) => createCriteriaValue(left).mod(createCriteriaValue(right))

      case _ =>
        throw new ODataNotImplementedException("Unsupported expression type for 'filter': " + expr)
    }

    def createCriteria(expr: Expression): Criteria = expr match {
      case EqExpr(left, right) => createCriteriaValue(left).eq(createCriteriaValue(right))
      case NeExpr(left, right) => createCriteriaValue(left).ne(createCriteriaValue(right))
      case LtExpr(left, right) => createCriteriaValue(left).lt(createCriteriaValue(right))
      case LeExpr(left, right) => createCriteriaValue(left).le(createCriteriaValue(right))
      case GtExpr(left, right) => createCriteriaValue(left).gt(createCriteriaValue(right))
      case GeExpr(left, right) => createCriteriaValue(left).ge(createCriteriaValue(right))

      case AndExpr(left, right) => createCriteria(left).and(createCriteria(right))
      case OrExpr(left, right) => createCriteria(left).or(createCriteria(right))
      case BooleanMethodCallExpr(methodName, args) => createMethodCriteria(methodName, args)

      case _ =>
        throw new ODataNotImplementedException("Unsupported expression type for 'filter': " + expr)
    }
    
    def createMethodCriteria(methodName: String, args: List[Expression]): Criteria = methodName match {
      case "startswith" => evaluateStartsWithMethod(args)
      case "endswith" => evaluateEndsWithMethod(args)
      case "contains" => evaluateContainsMethod(args)
      case "geo.intersects" => evaluateGeoIntersectsMethod(args)
    }
    
    def evaluateEndsWithMethod(args: List[Expression]):Criteria = {
      createCriteriaValue(args(0)).endswith(createCriteriaValue(args(1)))
    }

    def evaluateContainsMethod(args: List[Expression]):Criteria = {
    		createCriteriaValue(args(0)).contains(createCriteriaValue(args(1)))
    }
    
    def evaluateStartsWithMethod(args: List[Expression]):Criteria = {
    		createCriteriaValue(args(0)).startswith(createCriteriaValue(args(1)))
    }
    
    def evaluateGeoIntersectsMethod(args: List[Expression]):Criteria = {
    		createCriteriaValue(args(0)).geointersects(createCriteriaValue(args(1)))
    }
    
    CriteriaFilterOperation(source, createCriteria(expression))
  }

  private def applyExpandOption(source: QueryOperation, items: List[ExpandItem]): QueryOperation = {

    def getExpandPath(segment: ExpandPathSegment): String = segment match {
      case ComplexPropertyExpandPathSegment(propertyName, _, subPath) => propertyName + "." + getExpandPath(subPath)
      case NavigationPropertyExpandPathSegment(propertyName, _) => propertyName
    }

    val expandFields = items flatMap {
      case expandPath: ExpandPath => List(getExpandPath(expandPath.path))
      case _ => Nil
    }

    // NOTE: Expand options (per expand item) are not (yet) supported and are ignored for now

    ExpandOperation(source, expandFields)
  }

  // applyApplyOption to handle ApplyOption
  private def applyApplyOption(source: QueryOperation, applyExp: ApplyExpr): QueryOperation = {
    
    def getApplyProperties(expr: ApplyPropertyExpr): List[String] = expr.args flatMap {
        case propertyPath: Expression => List(getPropertyPath(propertyPath) match {
          case Some(propertyPath) => propertyPath
          case None =>
            throw new ODataNotImplementedException("Unsupported expression type for 'apply': " + expr)
        })
        case _ => Nil
    }
    
    def getApplyFunction(expr: ApplyFunctionExpr): ApplyFunction = ApplyFunction(expr.methodName, expr.args)
    
    ApplyOperation(source, applyExp.methodName, getApplyProperties(applyExp.args.properties), getApplyFunction(applyExp.args.function))
  }

  private def applyOrderByOption(source: QueryOperation, items: List[OrderByItem]): QueryOperation = {

    def getOrderByPropertyPath(expr: Expression): String = getPropertyPath(expr) match {
      case Some(propertyPath) => propertyPath
      case None =>
        throw new ODataNotImplementedException("Unsupported expression type for 'orderby': " + expr)
    }

    val orderByFields = items map {
      case AscendingOrderByItem(expr) => OrderByProperty.asc(getOrderByPropertyPath(expr))
      case DescendingOrderByItem(expr) => OrderByProperty.desc(getOrderByPropertyPath(expr))
    }

    OrderByOperation(source, orderByFields)
  }

  private def getPropertyPath(expr: Expression): Option[String] = expr match {
    case EntityPathExpr(_, Some(subPath)) => getPropertyPath(subPath)
    case ComplexPathExpr(_, Some(subPath)) => getPropertyPath(subPath)

    case PropertyPathExpr(propertyName, Some(subPath)) =>
      getPropertyPath(subPath) match {
        case Some(subName) => Some(propertyName + "." + subName)
        case None => Some(propertyName)
      }

    case PropertyPathExpr(propertyName, None) => Some(propertyName)

    case _ => None
  }
}
