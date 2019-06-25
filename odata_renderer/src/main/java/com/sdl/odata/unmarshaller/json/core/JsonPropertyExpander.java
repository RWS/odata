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
package com.sdl.odata.unmarshaller.json.core;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sdl.odata.unmarshaller.json.core.JsonParserUtils.getAllProperties;
import static com.sdl.odata.unmarshaller.json.core.JsonParserUtils.getAppropriateFieldValue;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Json Property Expander.
 * <p>
 * This support class expand the content for any kind of property for parser.
 */
public class JsonPropertyExpander {
    private static final Logger LOG = LoggerFactory.getLogger(JsonPropertyExpander.class);

    private EntityDataModel entityDataModel;

    public JsonPropertyExpander(EntityDataModel entityDataModel) {
        this.entityDataModel = entityDataModel;
    }

    /**
     * Populates the embedded object property with the relevant field values.
     *
     * @param entity      the entity
     * @param currentNode the current node to to process
     * @param property    the embedded object property
     * @param node        the node
     * @param map         the map of field values
     * @throws ODataException If unable to fill object properties
     */
    public void fillUpdatedObjectProperty(Object entity, Object currentNode, StructuralProperty property,
                                          String node, Map<String, Object> map) throws ODataException {
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) currentNode).entrySet()) {
            if (findAppropriateElement(entity, property, node, map, entry)) {
                break;
            }
        }
    }


    private void fillEntryFeed(Object entity, Object currentNode, StructuralProperty property,
                               String node, Map<String, Object> map) throws ODataException {
        Map<String, Object> entryMap = (Map<String, Object>) map.get(currentNode);
        for (Map.Entry<String, Object> entry : entryMap.entrySet()) {
            if (findAppropriateElement(entity, property, node, entryMap, entry)) {
                break;
            }
        }
    }

    private boolean findAppropriateElement(Object entity, StructuralProperty property, String node,
                                           Map<String, Object> map, Map.Entry<String, Object> entry)
            throws ODataException {
        if (node.equalsIgnoreCase(entry.getKey()) && entry.getValue() != null) {
            Object value = getFieldValueByType(property.getTypeName(), entry.getValue(), map, true);
            if (value != null) {
                EntityDataModelUtil.setPropertyValue(property, entity, value);
                return true;
            } else {
                LOG.warn("There is no element with name '{}'", node);
            }
        }
        return false;
    }

    /**
     * Populates the collection property with the relevant field values.
     *
     * @param entity      the entity
     * @param currentNode the current node to to process
     * @param property    the embedded collection
     * @param node        the node
     * @param map         the map of field values
     * @throws ODataException If unable to update collection properties
     */
    public void fillUpdatedCollectionProperty(Object entity, Object currentNode, StructuralProperty property,
                                              String node, Map<String, Object> map) throws ODataException {

        Collection<Object> values = EntityDataModelUtil.createPropertyCollection(property);

        Object current = (currentNode instanceof Map) ? currentNode : map.get(currentNode);
        Iterable currentIt = (Iterable) ((Map) current).get(node);
        if (currentIt != null) {
            for (Object subValue : currentIt) {
                Object value = getFieldValueByType(property.getElementTypeName(), subValue, map, true);

                if (value != null) {
                    values.add(value);
                }
            }
        }
        EntityDataModelUtil.setPropertyValue(property, entity, values);
    }

    /**
     * Populates the primitive property of the entity with the relevant field value.
     *
     * @param entity   the entity
     * @param keySet   the set of entity properties
     * @param property the primitive property
     * @param node     the current node
     * @param map      the map of field values
     * @throws ODataException If unable to fill primitive properties
     */
    public void fillPrimitiveProperty(Object entity, Set<String> keySet, StructuralProperty property,
                                      String node, Map<String, Object> map) throws ODataException {
        for (String target : keySet) {
            if (node.equalsIgnoreCase(target)) {
                Object value = getFieldValueByType(property.getTypeName(), target, map, false);
                if (value != null) {
                    EntityDataModelUtil.setPropertyValue(property, entity, value);
                    break;
                } else {
                    LOG.warn("There is no element with name '{}'", node);
                }
            }
        }
    }

    /**
     * Populates the collection property of the entity with field values.
     *
     * @param entity   the entity
     * @param keySet   the set of entity properties
     * @param property the collection property
     * @param node     the current node
     * @param map      the map of field values
     * @throws ODataException If unable to fill collection properties
     */
    public void fillCollectionProperty(Object entity, Set<String> keySet, StructuralProperty property,
                                       String node, Map<String, Object> map) throws ODataException {
        for (String target : keySet) {
            if (node.equalsIgnoreCase(target)) {
                    Iterable subValues = (Iterable) map.get(target);
                Collection<Object> values = EntityDataModelUtil.createPropertyCollection(property);
                for (Object subValue : subValues) {
                    Object value = getFieldValueByType(property.getElementTypeName(), subValue, map, true);

                    if (value != null) {
                        values.add(value);
                    }
                }
                EntityDataModelUtil.setPropertyValue(property, entity, values);
                break;
            }
        }
    }

    /**
     * Gets the field value for the property type.
     *
     * @param typeName    the type name
     * @param targetNode  the target node
     * @param map         the map of field value
     * @param isExtracted boolean indicating whether the field is extracted
     * @return the field object
     * @throws ODataException If unable to get field values
     */
    protected Object getFieldValueByType(String typeName, Object targetNode, Map<String, Object> map,
                                         boolean isExtracted) throws ODataException {
        Object fieldValue = null;
        LOG.debug("Type is {}", typeName);
        Type type = entityDataModel.getType(typeName);
        if (type == null) {
            throw new ODataUnmarshallingException("OData type not found: " + typeName);
        }
        switch (type.getMetaType()) {
            case ENUM:
            case PRIMITIVE:
                if (isExtracted) {
                    fieldValue = getAppropriateFieldValue(type.getJavaType(), String.valueOf(targetNode));
                } else {
                    if (map.get(targetNode) != null) {
                        fieldValue = getAppropriateFieldValue(type.getJavaType(), String.valueOf(map.get(targetNode)));
                    }
                }
                break;

            case ENTITY:
            case COMPLEX:
                fieldValue = unmarshallEntityByName(typeName, map, targetNode);
                break;

            default:
                LOG.warn("Unsupported type {}.", type.getMetaType().name());
                throw new UnsupportedOperationException("Unsupported type: " + typeName);
        }
        return fieldValue;
    }


    /**
     * Unmarsall a named entity.
     *
     * @param entityName  the entity to unmarshall
     * @param map         the map of field values
     * @param currentNode the node to process
     * @return the value object of the unmarshalled entity
     * @throws ODataException If unable to unmarshall entity by name
     */
    private Object unmarshallEntityByName(String entityName, Map<String, Object> map,
                                          Object currentNode) throws ODataException {
        LOG.debug("Entity '{}' created.", entityName);
        if (!isNullOrEmpty(entityName)) {
            Object entity = loadEntity(entityName);
            setEntityProperties(entity,
                    JsonParserUtils.getStructuredType(entityName, entityDataModel), map, currentNode);
            LOG.debug("Entity '{}' properties mapped successfully.", entityName);
            return entity;
        } else {
            throw new ODataUnmarshallingException("Unmarshalling Entity name should be null !!!!...");
        }
    }

    /**
     * Creates the an entity based on its name.
     *
     * @param entityName The name of the entity
     * @return the entity object
     * @throws ODataUnmarshallingException If unable to load entity
     */
    public Object loadEntity(String entityName) throws ODataUnmarshallingException {
        Object entity = null;
        if (entityName != null) {
            try {
                StructuredType entityType = JsonParserUtils.getStructuredType(entityName, entityDataModel);
                if (entityType != null) {
                    entity = entityType.getJavaType().newInstance();
                } else {
                    LOG.warn("Given entity '{}' is not found in entity data model", entityName);
                    throw new ODataUnmarshallingException("Couldn't initiate entity because given entity [" + entityName
                            + "] is not found in entity data model.");
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ODataUnmarshallingException("Cannot instantiate entity", e);
            }
        }
        return entity;
    }

    /**
     * Sets the given entity with structural properties from the fields.
     *
     * @param entity      entity
     * @param entityType  entityType
     * @param map         a map of field values
     * @param currentNode the current node to process
     * @throws ODataException If unable to set entity properties
     */
    public void setEntityProperties(Object entity, StructuredType entityType, Map<String, Object> map,
                                    Object currentNode) throws ODataException {

        Set<String> keySet = map.keySet();
        for (StructuralProperty property : getAllProperties(entityType, entityDataModel)) {
            Field field = property.getJavaField();
            String node = property.getName();
            LOG.debug("Property Name is {}", node);

            if (property.isCollection()) {
                if (currentNode == null) {
                    fillCollectionProperty(entity, keySet, property, node, map);
                } else {
                    fillUpdatedCollectionProperty(entity, currentNode, property,  node, map);
                }
            } else {
                if (currentNode == null) {
                    fillPrimitiveProperty(entity, keySet, property, node, map);
                } else {
                    if (currentNode instanceof String) {
                        fillEntryFeed(entity, currentNode, property, node, map);
                    } else {
                        fillUpdatedObjectProperty(entity, currentNode, property, node, map);
                    }
                }
            }
        }
    }
}

