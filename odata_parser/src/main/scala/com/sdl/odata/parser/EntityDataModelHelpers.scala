/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.edm.model._
import scala.collection.JavaConverters._

trait EntityDataModelHelpers {

  def entityDataModel: EntityDataModel

  def getType(typeName: String): Option[Type] = Option(entityDataModel.getType(typeName))

  def getPrimitiveType(typeName: String): Option[PrimitiveType] =
    getType(typeName).filter(_.getMetaType == MetaType.PRIMITIVE).map(_.asInstanceOf[PrimitiveType])

  def isPrimitiveType(typeName: String): Boolean =
    getPrimitiveType(typeName).isDefined

  def getStructuredType(typeName: String): Option[StructuredType] =
    getType(typeName).filter(_.isInstanceOf[StructuredType]).map(_.asInstanceOf[StructuredType])

  def getEntityType(typeName: String): Option[EntityType] =
    getType(typeName).filter(_.getMetaType == MetaType.ENTITY).map(_.asInstanceOf[EntityType])

  def isEntityType(typeName: String): Boolean = getEntityType(typeName).isDefined

  def getComplexType(typeName: String): Option[ComplexType] =
    getType(typeName).filter(_.getMetaType == MetaType.COMPLEX).map(_.asInstanceOf[ComplexType])

  def isComplexType(typeName: String): Boolean = getComplexType(typeName).isDefined

  def getEnumType(typeName: String): Option[EnumType] =
    getType(typeName).filter(_.getMetaType == MetaType.ENUM).map(_.asInstanceOf[EnumType])

  def isEnumType(typeName: String): Boolean = getEnumType(typeName).isDefined

  def getTypeDefinition(typeName: String): Option[TypeDefinition] =
    getType(typeName).filter(_.getMetaType == MetaType.TYPE_DEFINITION).map(_.asInstanceOf[TypeDefinition])

  def isTypeDefinition(typeName: String): Boolean = getTypeDefinition(typeName).isDefined

  def getEntitySet(entitySetName: String): Option[EntitySet] =
    Option(entityDataModel.getEntityContainer.getEntitySet(entitySetName))

  def isEntitySet(entitySetName: String): Boolean = getEntitySet(entitySetName).isDefined

  def getSingleton(SingletonName: String): Option[Singleton] =
    Option(entityDataModel.getEntityContainer.getSingleton(SingletonName))

  def isSingleton(SingletonName: String): Boolean = getSingleton(SingletonName).isDefined

  // Note: Needs to take possible base type of containing type into account; if not found in the type itself, look in the base type
  def getStructuralProperty(contextTypeName: String, propertyName: String): Option[StructuralProperty] =
    getStructuredType(contextTypeName).flatMap(t => Option(t.getStructuralProperty(propertyName)))

  def isSinglePropertyOfType(property: StructuralProperty, predicate: String => Boolean): Boolean =
    !property.isCollection && predicate(property.getTypeName)

  def isCollectionPropertyOfType(property: StructuralProperty, predicate: String => Boolean): Boolean =
    property.isCollection && predicate(property.getElementTypeName)

  // Checks if the property refers to a single primitive value
  def isPrimitiveSingleProperty(property: StructuralProperty): Boolean =
    isSinglePropertyOfType(property, isPrimitiveType)

  // Checks if the property refers to a collection of primitive values
  def isPrimitiveCollectionProperty(property: StructuralProperty): Boolean =
    isCollectionPropertyOfType(property, isPrimitiveType)

  def isComplexSingleProperty(property: StructuralProperty): Boolean =
    isSinglePropertyOfType(property, isComplexType)

  def isComplexCollectionProperty(property: StructuralProperty): Boolean =
    isCollectionPropertyOfType(property, isComplexType)

  // Check if the property is a navigation property that refers to a single entity or a collection of entities
  def isEntityNavigationProperty(property: StructuralProperty): Boolean =
    isEntitySingleNavigationProperty(property) || isEntityCollectionNavigationProperty(property)

  // Check if the property is a navigation property that refers to a single entity
  def isEntitySingleNavigationProperty(property: StructuralProperty): Boolean =
    property.isInstanceOf[NavigationProperty] && isSinglePropertyOfType(property, isEntityType)

  // Check if the property is a navigation property that refers to a collection of entities
  def isEntityCollectionNavigationProperty(property: StructuralProperty): Boolean =
    property.isInstanceOf[NavigationProperty] && isCollectionPropertyOfType(property, isEntityType)


  def checkPropertyWith(predicate: StructuralProperty => Boolean)(contextTypeName: String)(propertyName: String): Boolean =
    getStructuralProperty(contextTypeName, propertyName).exists(predicate)

  def isPrimitiveSinglePropertyOf: String => String => Boolean =
    checkPropertyWith(isPrimitiveSingleProperty)

  def isPrimitiveKeyPropertyOf(contextTypeName: String)(propertyName: String): Boolean = {
    import scala.collection.JavaConverters._
    val containingType = getEntityType(contextTypeName)

    def isKeyProperty(property: StructuralProperty): Boolean = containingType exists {
      entityType => entityType.getKey.getPropertyRefs.asScala.map(_.getPath).toList.contains(property.getName)
    }

    containingType.flatMap(t => Option(t.getStructuralProperty(propertyName)))
      .filter(isPrimitiveSingleProperty)
      .exists(isKeyProperty)
  }

  def isPrimitiveCollectionPropertyOf: String => String => Boolean =
    checkPropertyWith(isPrimitiveCollectionProperty)

  def isComplexSinglePropertyOf: String => String => Boolean =
    checkPropertyWith(isComplexSingleProperty)

  def isComplexCollectionPropertyOf: String => String => Boolean =
    checkPropertyWith(isComplexCollectionProperty)

  // Note: Stream properties are not (yet) supported; this always returns false
  def isStreamPropertyOf: String => String => Boolean =
    (contextTypeName: String) => (propertyName: String) => false

  def isEntityNavigationPropertyOf: String => String => Boolean =
    checkPropertyWith(isEntityNavigationProperty)

  def isEntitySingleNavigationPropertyOf: String => String => Boolean =
    checkPropertyWith(isEntitySingleNavigationProperty)

  def isEntityCollectionNavigationPropertyOf: String => String => Boolean =
    checkPropertyWith(isEntityCollectionNavigationProperty)


  def getEntitySetTypeName(entitySetName: String): Option[String] =
    getEntitySet(entitySetName).flatMap(e => Option(e.getTypeName))

  def getSingletonTypeName(singletonName: String): Option[String] =
    getSingleton(singletonName).flatMap(s => Option(s.getTypeName))

  /**
   * Gets the type name of a non-collection property. Returns `None` if the property is a collection.
   *
   * @param contextTypeName The name of the type that contains the property.
   * @param propertyName The name of the property.
   * @return `Some` containing the name of the type of the property, or `None` if the specified names are not valid
   *        or the property is a collection.
   */
  def getSinglePropertyTypeName(contextTypeName: String, propertyName: String): Option[String] =
    getStructuralProperty(contextTypeName, propertyName).filter(!_.isCollection).flatMap(p => Option(p.getTypeName))

  /**
   * Gets the type name of the elements of a collection property. Returns `None` if the property is not a collection.
   *
   * @param contextTypeName The name of the type that contains the property.
   * @param propertyName The name of the property.
   * @return `Some` containing the name of the type of the elements of the collection property, or `None` if the
   *        specified names are not valid or the property is not a collection.
   */
  def getPropertyElementTypeName(contextTypeName: String, propertyName: String): Option[String] =
    getStructuralProperty(contextTypeName, propertyName).flatMap(p => Option(p.getElementTypeName))

  def isAction(actionName: String): Boolean =
    getAction(actionName).isDefined

  def getAction(actionName: String): Option[Action] =
    entityDataModel.getSchemas.asScala.find(_.getAction(actionName) != null).map(_.getAction(actionName))

  def isActionImport(actionImportName: String): Boolean =
    getActionImport(actionImportName).isDefined

  def getActionImport(actionImportName: String): Option[ActionImport] =
    Option(entityDataModel.getEntityContainer.getActionImport(actionImportName))

  def isFunction(functionName: String): Boolean =
    getFunction(functionName).isDefined

  def getFunction(functionName: String): Option[Function] =
    entityDataModel.getSchemas.asScala.find(_.getFunction(functionName) != null).map(_.getFunction(functionName))

  def getFunctionReturnType(functionName: String): Option[String] =
    getFunction(functionName).map(_.getReturnType)

  def isFunctionImport(functionImportName: String): Boolean =
    getFunctionImport(functionImportName).isDefined

  def getFunctionImport(functionImportName: String): Option[FunctionImport] =
    Option(entityDataModel.getEntityContainer.getFunctionImport(functionImportName))

  def getFunctionImportReturnType(functionImportName: String): Option[String] =
    getFunctionImport(functionImportName).map(_.getFunction.getReturnType)
}
