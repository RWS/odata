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
package com.sdl.odata.util.edm;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.FunctionImport;
import com.sdl.odata.api.edm.model.Key;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.PropertyRef;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.Singleton;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Utility class for entity data model related methods.
 */
public final class EntityDataModelUtil {
    private static final Logger LOG = LoggerFactory.getLogger(EntityDataModelUtil.class);

    /**
     * Collection pattern.
     */
    public static final Pattern COLLECTION_PATTERN = Pattern.compile("Collection\\((.+)\\)");

    private EntityDataModelUtil() {
    }

    public static Type getAndCheckType(EntityDataModel entityDataModel, String typeName) {
        Type type = entityDataModel.getType(typeName);
        if (type == null) {
            throw new ODataSystemException("Type not found in the entity data model: " + typeName);
        }
        return type;
    }

    /**
     * Gets the OData type for a Java type and throws an exception if there is no OData type for the Java type.
     *
     * @param entityDataModel The entity data model.
     * @param javaType        The Java type.
     * @return The OData type for the Java type.
     * @throws ODataSystemException If there is no OData type for the specified Java type.
     */
    public static Type getAndCheckType(EntityDataModel entityDataModel, Class<?> javaType) {
        Type type = entityDataModel.getType(javaType);
        if (type == null) {
            throw new ODataSystemException("No type found in the entity data model for Java type: "
                    + javaType.getName());
        }
        return type;
    }

    /**
     * Returns {@code true} if the OData type is a primitive type, {@code false} otherwise.
     *
     * @param type The OData type.
     * @return {@code true} if the OData type is a primitive type, {@code false} otherwise.
     */
    public static boolean isPrimitiveType(Type type) {
        return type.getMetaType() == MetaType.PRIMITIVE;
    }

    /**
     * Checks if the specified OData type is a primitive type and throws an exception if it is not.
     *
     * @param type The OData type.
     * @return The OData type.
     * @throws ODataSystemException If the OData type is not a primitive type.
     */
    public static PrimitiveType checkIsPrimitiveType(Type type) {
        if (!isPrimitiveType(type)) {
            throw new ODataSystemException("A primitive type is required, but '" + type.getFullyQualifiedName() +
                    "' is not a primitive type: " + type.getMetaType());
        }
        return (PrimitiveType) type;
    }

    /**
     * Gets the OData type with a specified name and checks if the OData type is a primitive type; throws an exception
     * if the OData type is not a primitive type.
     *
     * @param entityDataModel The entity data model.
     * @param typeName        The type name.
     * @return The OData primitive type with the specified name.
     * @throws ODataSystemException If there is no OData type with the specified name or if the OData type is not
     *                              a primitive type.
     */
    public static PrimitiveType getAndCheckPrimitiveType(EntityDataModel entityDataModel, String typeName) {
        return checkIsPrimitiveType(getAndCheckType(entityDataModel, typeName));
    }

    /**
     * Returns {@code true} if the OData type is a structured type, {@code false} otherwise.
     *
     * @param type The OData type.
     * @return {@code true} if the OData type is a structured type, {@code false} otherwise.
     */
    public static boolean isStructuredType(Type type) {
        MetaType metaType = type.getMetaType();
        return metaType == MetaType.ENTITY || metaType == MetaType.COMPLEX;
    }

    /**
     * Checks if the specified OData type is a structured type and throws an exception if it is not.
     *
     * @param type The OData type.
     * @return The OData type.
     * @throws ODataSystemException If the OData type is not a structured type.
     */
    public static StructuredType checkIsStructuredType(Type type) {
        if (!isStructuredType(type)) {
            throw new ODataSystemException("A structured type is required, but '" + type.getFullyQualifiedName() +
                    "' is not a structured type: " + type.getMetaType());
        }
        return (StructuredType) type;
    }

    /**
     * Gets the OData type with a specified name and checks if the OData type is a structured type; throws an exception
     * if the OData type is not a structured type.
     *
     * @param entityDataModel The entity data model.
     * @param typeName        The type name.
     * @return The OData structured type with the specified name.
     * @throws ODataSystemException If there is no OData type with the specified name or if the OData type is not
     *                              a structured type.
     */
    public static StructuredType getAndCheckStructuredType(EntityDataModel entityDataModel, String typeName) {
        return checkIsStructuredType(getAndCheckType(entityDataModel, typeName));
    }

    /**
     * Gets the OData type for a Java type and checks if the OData type is a structured type; throws an exception if the
     * OData type is not a structured type.
     *
     * @param entityDataModel The entity data model.
     * @param javaType        The Java type.
     * @return The OData structured type for the Java type.
     * @throws ODataSystemException If there is no OData type for the specified Java type or if the OData type is not
     *                              a structured type.
     */
    public static StructuredType getAndCheckStructuredType(EntityDataModel entityDataModel, Class<?> javaType) {
        return checkIsStructuredType(getAndCheckType(entityDataModel, javaType));
    }

    /**
     * Returns {@code true} if the OData type is an entity type, {@code false} otherwise.
     *
     * @param type The OData type.
     * @return {@code true} if the OData type is an entity type, {@code false} otherwise.
     */
    public static boolean isEntityType(Type type) {
        return type.getMetaType() == MetaType.ENTITY;
    }

    /**
     * Checks if the specified OData type is an entity type and throws an exception if it is not.
     *
     * @param type The OData type.
     * @return The OData type.
     * @throws ODataSystemException If the OData type is not an entity type.
     */
    public static EntityType checkIsEntityType(Type type) {
        if (!isEntityType(type)) {
            throw new ODataSystemException("An entity type is required, but '" + type.getFullyQualifiedName() +
                    "' is not an entity type: " + type.getMetaType());
        }
        return (EntityType) type;
    }

    /**
     * Gets the OData type with a specified name and checks if the OData type is an entity type; throws an exception
     * if the OData type is not an entity type.
     *
     * @param entityDataModel The entity data model.
     * @param typeName        The type name.
     * @return The OData entity type with the specified name.
     * @throws ODataSystemException If there is no OData type with the specified name or if the OData type is not
     *                              an entity type.
     */
    public static EntityType getAndCheckEntityType(EntityDataModel entityDataModel, String typeName) {
        return checkIsEntityType(getAndCheckType(entityDataModel, typeName));
    }

    /**
     * Gets the OData type for a Java type and checks if the OData type is an entity type; throws an exception if the
     * OData type is not an entity type.
     *
     * @param entityDataModel The entity data model.
     * @param javaType        The Java type.
     * @return The OData entity type for the Java type.
     * @throws ODataSystemException If there is no OData type for the specified Java type or if the OData type is not
     *                              an entity type.
     */
    public static EntityType getAndCheckEntityType(EntityDataModel entityDataModel, Class<?> javaType) {
        return checkIsEntityType(getAndCheckType(entityDataModel, javaType));
    }

    /**
     * Returns {@code true} if the OData type is a complex type, {@code false} otherwise.
     *
     * @param type The OData type.
     * @return {@code true} if the OData type is a complex type, {@code false} otherwise.
     */
    public static boolean isComplexType(Type type) {
        return type.getMetaType() == MetaType.COMPLEX;
    }

    /**
     * Checks if the specified OData type is a complex type and throws an exception if it is not.
     *
     * @param type The OData type.
     * @return The OData type.
     * @throws ODataSystemException If the OData type is not a complex type.
     */
    public static ComplexType checkIsComplexType(Type type) {
        if (!isComplexType(type)) {
            throw new ODataSystemException("A complex type is required, but '" + type.getFullyQualifiedName() +
                    "' is not a complex type: " + type.getMetaType());
        }
        return (ComplexType) type;
    }

    /**
     * Gets the OData type for a Java type and checks if the OData type is a complex type; throws an exception if the
     * OData type is not a complex type.
     *
     * @param entityDataModel The entity data model.
     * @param javaType        The Java type.
     * @return The OData complex type for the Java type.
     * @throws ODataSystemException If there is no OData type for the specified Java type or if the OData type is not
     *                              a complex type.
     */
    public static ComplexType getAndCheckComplexType(EntityDataModel entityDataModel, Class<?> javaType) {
        return checkIsComplexType(getAndCheckType(entityDataModel, javaType));
    }

    /**
     * Gets the OData type name of the property; if the property is a collection, gets the OData type name of the
     * elements of the collection.
     *
     * @param property The property.
     * @return The OData type name of the property; if the property is a collection, the OData type name of the elements
     * of the collection.
     */
    public static String getPropertyTypeName(StructuralProperty property) {
        return property.isCollection() ? property.getElementTypeName() : property.getTypeName();
    }

    /**
     * Gets the OData type of the property; if the property is a collection, gets the OData type of the elements of the
     * collection.
     *
     * @param entityDataModel The entity data model
     * @param property        The property.
     * @return The OData type of the property; if the property is a collection, the OData type of the elements of the
     * collection.
     */
    public static Type getPropertyType(EntityDataModel entityDataModel, StructuralProperty property) {
        return getAndCheckType(entityDataModel, getPropertyTypeName(property));
    }

    /**
     * Gets the value of a property.
     *
     * @param property The property.
     * @param object   The object to get the value from (typically an OData entity).
     * @return The value of the property.
     */
    public static Object getPropertyValue(StructuralProperty property, Object object) {
        Field field = property.getJavaField();
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new ODataSystemException("Cannot read property: " + property + " of object: " + object, e);
        }
    }

    /**
     * Sets the value of a property.
     *
     * @param property The property.
     * @param object   The object to set the value in (typically an OData entity).
     * @param value    The value to set.
     */
    public static void setPropertyValue(StructuralProperty property, Object object, Object value) {
        Field field = property.getJavaField();
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new ODataSystemException("Cannot write property: " + property + " of object: " + object, e);
        }
    }

    /**
     * Creates a new instance of a collection that is compatible with the specified property.
     * <p>
     * At the moment, only List and Set are supported. If the property is of a type that is not compatible with List
     * or Set, an exception is thrown.
     *
     * @param property The property, which must be of a collection type.
     * @return A new instance of a collection type that is compatible with the specified property.
     */
    public static Collection<Object> createPropertyCollection(StructuralProperty property) {
        Class<?> fieldType = property.getJavaField().getType();
        if (List.class.isAssignableFrom(fieldType)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(fieldType)) {
            return new HashSet<>();
        } else {
            throw new ODataSystemException("Unsupported collection type '" + fieldType.getName() + "' for property: " +
                    property);
        }
    }

    /**
     * Calls a callback for each property of a structured type (entity or complex type), including the properties in
     * the base types that the type might derive from.
     *
     * @param entityDataModel The entity data model.
     * @param structType      The structured type of which to visit the properties.
     * @param visitor         The callback called for each property.
     * @param <E>             The type of exception that the callback can throw.
     * @throws E If the callback throws an exception.
     */
    public static <E extends ODataException> void visitProperties(EntityDataModel entityDataModel,
                                                                  StructuredType structType, PropertyVisitor<E> visitor)
            throws E {
        String baseTypeName = structType.getBaseTypeName();
        if (!isNullOrEmpty(baseTypeName)) {
            visitProperties(entityDataModel, (StructuredType) entityDataModel.getType(baseTypeName), visitor);
        }

        for (StructuralProperty property : structType.getStructuralProperties()) {
            visitor.visit(property);
        }
    }

    /**
     * Get the 'Structural Property' from the given 'Entity Data Model' and 'Structured Type' looking up all the base
     * types recursively.
     *
     * @param entityDataModel The given 'Entity Data Model'.
     * @param structuredType  The given 'Structured Type'.
     * @param propertyName    The name of the property to look up.
     * @return The 'Structural Property' or {@code null} if not found.
     */
    public static StructuralProperty getStructuralProperty(EntityDataModel entityDataModel,
                                                           StructuredType structuredType, String propertyName) {

        StructuralProperty structuralProperty = structuredType.getStructuralProperty(propertyName);
        if (structuralProperty != null) {
            return structuralProperty;
        } else {
            // Look up recursively in the 'base type'
            String baseTypeName = structuredType.getBaseTypeName();
            if (!isNullOrEmpty(baseTypeName)) {
                Type baseType = entityDataModel.getType(baseTypeName);
                if (baseType != null && baseType instanceof StructuredType) {
                    return getStructuralProperty(entityDataModel, (StructuredType) baseType, propertyName);
                }
            }
        }
        return null;
    }

    /**
     * Gets the names of the properties that are part of the key of an entity type.
     *
     * @param entityType The entity type.
     * @return A {@code Set} containing the names of the key properties of the entity type.
     */
    public static Set<String> getKeyPropertyNames(EntityType entityType) {
        Set<String> keyPropertyNames = entityType.getKey().getPropertyRefs().stream()
                .map(PropertyRef::getPath).collect(Collectors.toSet());
        return keyPropertyNames;
    }

    /**
     * Gets the values of the properties that part of the key of an entity type.
     *
     * @param entityType The entity type.
     * @param entity     The entity.
     * @return A {@code Map} containing the values of the key properties of the entity, mapped by property name.
     */
    public static Map<String, Object> getKeyPropertyValues(EntityType entityType, Object entity) {
        Map<String, Object> keyPropertyValues = new HashMap<>();
        for (PropertyRef propertyRef : entityType.getKey().getPropertyRefs()) {
            String propertyName = propertyRef.getPath();
            Object propertyValue = getPropertyValue(entityType.getStructuralProperty(propertyName), entity);
            keyPropertyValues.put(propertyName, propertyValue);
        }
        return keyPropertyValues;
    }

    /**
     * Gets the entity set with the specified name, throws an exception if no entity set with the specified name exists.
     *
     * @param entityDataModel The entity data model.
     * @param entitySetName   The name of the entity set.
     * @return The entity set.
     * @throws ODataSystemException If the entity data model does not contain an entity set with the specified name.
     */
    public static EntitySet getAndCheckEntitySet(EntityDataModel entityDataModel, String entitySetName) {
        EntitySet entitySet = entityDataModel.getEntityContainer().getEntitySet(entitySetName);
        if (entitySet == null) {
            throw new ODataSystemException("Entity set not found in the entity data model: " + entitySetName);
        }
        return entitySet;
    }

    /**
     * Get the Entity Set for a given Entity Type name through the Entity Data Model.
     *
     * @param entityDataModel The Entity Data Model.
     * @param entityTypeName  The Entity Type name.
     * @return The Entity Set.
     * @throws ODataEdmException if unable to get entity set in entity data model
     */
    public static EntitySet getEntitySetByEntityTypeName(EntityDataModel entityDataModel, String entityTypeName)
            throws ODataEdmException {
        for (EntitySet entitySet : entityDataModel.getEntityContainer().getEntitySets()) {
            if (entitySet.getTypeName().equals(entityTypeName)) {
                return entitySet;
            }
        }
        throw new ODataSystemException("Entity set not found in the entity data model for type: " + entityTypeName);
    }

    /**
     * Get the Entity Name for a given Entity Type name through the Entity Data Model.
     * This looks for entity in both EntitySets and Singletons in the container
     *
     * @param entityDataModel The Entity Data Model.
     * @param entityTypeName  The Entity Type name.
     * @return The Entity name
     * @throws ODataEdmException if unable to find entity name in entity data model
     */
    public static String getEntityNameByEntityTypeName(EntityDataModel entityDataModel, String entityTypeName)
            throws ODataEdmException {
        for (EntitySet entitySet : entityDataModel.getEntityContainer().getEntitySets()) {
            if (entitySet.getTypeName().equals(entityTypeName)) {
                return entitySet.getName();
            }
        }
        //If not found in EntitySet, try to find in Singletons
        for (Singleton singleton : entityDataModel.getEntityContainer().getSingletons()) {
            if (singleton.getTypeName().equals(entityTypeName)) {
                return singleton.getName();
            }
        }
        throw new ODataSystemException("Entity name not found in the entity data model for type: " + entityTypeName);
    }

    /**
     * Get the Entity Set of a given entity through the Entity Data Model.
     *
     * @param entityDataModel The Entity Data Model.
     * @param entity          The given entity.
     * @return The Entity Set.
     * @throws ODataEdmException If unable to get entity set
     */
    public static EntitySet getEntitySetByEntity(EntityDataModel entityDataModel, Object entity)
            throws ODataEdmException {
        return getEntitySetByEntityTypeName(entityDataModel,
                getAndCheckEntityType(entityDataModel, entity.getClass()).getFullyQualifiedName());
    }

    /**
     * Check if the given entity is a Singleton entity.
     *
     * @param entityDataModel The Entity Data Model.
     * @param entity          The given entity.
     * @return true if singleton, false if not
     * @throws ODataEdmException if unable to determine if entity is singleton
     */
    public static boolean isSingletonEntity(EntityDataModel entityDataModel, Object entity) throws ODataEdmException {
        // Get the entity type
        EntityType entityType = getAndCheckEntityType(entityDataModel, entity.getClass());
        boolean isSingletonEntity = false;

        for (Singleton singleton : entityDataModel.getEntityContainer().getSingletons()) {
            if (singleton.getTypeName().equals(entityType.getFullyQualifiedName())) {
                isSingletonEntity = true;
                break;
            }
        }

        return isSingletonEntity;
    }

    /**
     * Get the entity name in the entity data model for the given entity.
     *
     * @param entityDataModel The Entity Data Model.
     * @param entity          The given entity.
     * @return Entity name which can be either EntitySet name or a Singleton Name
     * @throws ODataEdmException If unable to get entity name from entity data model
     */
    public static String getEntityName(EntityDataModel entityDataModel, Object entity) throws ODataEdmException {

        // Get the entity type
        EntityType entityType = getAndCheckEntityType(entityDataModel, entity.getClass());

        // Check if the entity belongs to an Entity Set
        String entityName = null;

        for (EntitySet entitySet : entityDataModel.getEntityContainer().getEntitySets()) {
            if (entitySet.getTypeName().equals(entityType.getFullyQualifiedName())) {
                entityName = entitySet.getName();
                break;
            }
        }

        if (entityName == null) {
            for (Singleton singleton : entityDataModel.getEntityContainer().getSingletons()) {
                if (singleton.getTypeName().equals(entityType.getFullyQualifiedName())) {
                    entityName = singleton.getName();
                    break;
                }
            }

            if (entityName == null) {
                throw new ODataSystemException("Entity not found in the entity data model for type: "
                        + entityType.getFullyQualifiedName());
            }
        }
        return entityName;
    }

    /**
     * Gets the singleton with the specified name, throws an exception if no singleton with the specified name exists.
     *
     * @param entityDataModel The entity data model.
     * @param singletonName   The name of the singleton.
     * @return The singleton.
     * @throws ODataSystemException If the entity data model does not contain a singleton with the specified name.
     */
    public static Singleton getAndCheckSingleton(EntityDataModel entityDataModel, String singletonName) {
        Singleton singleton = entityDataModel.getEntityContainer().getSingleton(singletonName);
        if (singleton == null) {
            throw new ODataSystemException("Singleton not found in the entity data model: " + singletonName);
        }
        return singleton;
    }

    /**
     * Gets the function import by the specified name, throw an exception if no function import with the specified name
     * exists.
     *
     * @param entityDataModel    The entity data model.
     * @param functionImportName The name of the function import.
     * @return The function import
     */
    public static FunctionImport getAndCheckFunctionImport(EntityDataModel entityDataModel, String functionImportName) {
        FunctionImport functionImport = entityDataModel.getEntityContainer().getFunctionImport(functionImportName);
        if (functionImport == null) {
            throw new ODataSystemException("Function import not found in the entity data model: " + functionImportName);
        }

        return functionImport;
    }

    public static Function getAndCheckFunction(EntityDataModel entityDataModel, String functionName) {
        int namespaceLastIndex = functionName.lastIndexOf('.');
        String namespace = functionName.substring(0, namespaceLastIndex);
        String simpleFunctionName = functionName.substring(namespaceLastIndex + 1);
        Schema schema = entityDataModel.getSchema(namespace);
        if (schema == null) {
            throw new ODataSystemException("Could not find schema in entity data model with namespace: " +
                    namespace);
        }
        Function function = schema.getFunction(simpleFunctionName);
        if (function == null) {
            throw new ODataSystemException("Function not found in entity data model: " + functionName);
        }
        return function;
    }

    /**
     * Gets the action import by the specified name, throw an exception if no action import with the specified name
     * exists.
     *
     * @param entityDataModel  The entity data model.
     * @param actionImportName The name of action import.
     * @return The instance of action import.
     */
    public static ActionImport getAndCheckActionImport(EntityDataModel entityDataModel, String actionImportName) {
        ActionImport actionImport = entityDataModel.getEntityContainer().getActionImport(actionImportName);
        if (actionImport == null) {
            throw new ODataSystemException("Action import not found in the entity data model: " + actionImportName);
        }

        return actionImport;
    }

    /**
     * Gets the action by the specified name, throw an exception if no action with the specified name
     * exists.
     *
     * @param entityDataModel The entity data model.
     * @param actionName      The name of action.
     * @return the Action instance specified by given actionName.
     */
    public static Action getAndCheckAction(EntityDataModel entityDataModel, String actionName) {
        int namespaceLastIndex = actionName.lastIndexOf('.');
        String namespace = actionName.substring(0, namespaceLastIndex);
        String simpleActionName = actionName.substring(namespaceLastIndex + 1);
        Schema schema = entityDataModel.getSchema(namespace);
        if (schema == null) {
            throw new ODataSystemException("Could not find schema in entity data model with namespace: " +
                    namespace);
        }
        Action action = schema.getAction(simpleActionName);
        if (action == null) {
            throw new ODataSystemException("Action not found in entity data model: " + actionName);
        }
        return action;
    }

    /**
     * Checks if the specified typeName is a collection.
     *
     * @param entityDataModel The entity data model.
     * @param typeName        The type name to check.
     * @return True if the type is a collection, False if not
     */
    public static boolean isCollection(EntityDataModel entityDataModel, String typeName) {
        EntitySet entitySet = entityDataModel.getEntityContainer().getEntitySet(typeName);
        if (entitySet != null) {
            return true;
        }
        try {
            if (Collection.class.isAssignableFrom(Class.forName(typeName))
                    || COLLECTION_PATTERN.matcher(typeName).matches()) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            LOG.debug("Not possible to find class for type name: {}", typeName);
        }

        return false;
    }

    /**
     * Get the entity key for a given entity by inspecting the Entity Data Model.
     *
     * @param entityDataModel The Entity Data Model.
     * @param entity          The given entity.
     * @return The String representation of the entity key.
     * @throws ODataEdmException If unable to format entity key
     */
    public static String formatEntityKey(EntityDataModel entityDataModel, Object entity) throws ODataEdmException {

        Key entityKey = getAndCheckEntityType(entityDataModel, entity.getClass()).getKey();
        List<PropertyRef> keyPropertyRefs = entityKey.getPropertyRefs();
        try {
            if (keyPropertyRefs.size() == 1) {
                return getKeyValueFromPropertyRef(entityDataModel, entity, keyPropertyRefs.get(0));
            } else if (keyPropertyRefs.size() > 1) {
                List<String> processedKeys = new ArrayList<>();
                for (PropertyRef propertyRef : keyPropertyRefs) {
                    processedKeys.add(String.format("%s=%s", propertyRef.getPath(),
                            getKeyValueFromPropertyRef(entityDataModel, entity, propertyRef)));
                }
                return processedKeys.stream().map(Object::toString).collect(Collectors.joining(","));
            } else {
                LOG.error("Not possible to retrieve entity key for entity " + entity);
                throw new ODataEdmException("Entity key is not found for " + entity);
            }
        } catch (IllegalAccessException e) {
            LOG.error("Not possible to retrieve entity key for entity " + entity);
            throw new ODataEdmException("Not possible to retrieve entity key for entity " + entity, e);
        }
    }

    private static String getKeyValueFromPropertyRef(EntityDataModel entityDataModel, Object entity,
                                                     PropertyRef propertyRef)
            throws IllegalAccessException, ODataEdmException {

        EntityType entityType = getAndCheckEntityType(entityDataModel, entity.getClass());
        Field field = entityType.getStructuralProperty(propertyRef.getPath()).getJavaField();
        field.setAccessible(true);
        Object value = field.get(entity);
        if (value instanceof String) {
            return String.format("'%s'", ((String) value).replaceAll("'", "''"));
        } else if (value instanceof Period) {
            return String.format("duration'%s'", value.toString());
        } else {
            return value != null ? value.toString() : null;
        }
    }

    /**
     * Get the plural for the given English word.
     *
     * @param word The given English word. It can not be {@code null};
     * @return The plural word.
     * @see <a href='http://en.wikipedia.org/wiki/English_plurals'>Building English plurals.</a>
     */
    public static String pluralize(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }

        final String lowerCaseWord = word.toLowerCase();
        if (endsWithAny(lowerCaseWord, "s", "sh", "o")) {
            return word + "es";
        }
        if (lowerCaseWord.endsWith("y") &&
                !lowerCaseWord.endsWith("ay") || endsWithAny(lowerCaseWord, "ey", "oy", "uy")) {
            return word.substring(0, word.length() - 1) + "ies";
        } else {
            return word + "s";
        }
    }

    private static boolean endsWithAny(String word, String... endings) {
        for (String ending : endings) {
            if (word.endsWith(ending)) {
                return true;
            }
        }
        return false;
    }
}
