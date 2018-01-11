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
package com.sdl.odata.renderer.json.writer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.edm.model.TypeDefinition;
import com.sdl.odata.api.parser.AllExpandItem;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.renderer.json.util.JsonWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.JsonConstants.CONTEXT;
import static com.sdl.odata.JsonConstants.COUNT;
import static com.sdl.odata.JsonConstants.ID;
import static com.sdl.odata.JsonConstants.TYPE;
import static com.sdl.odata.JsonConstants.VALUE;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.ODataRendererUtils.isForceExpandParamSet;
import static com.sdl.odata.api.edm.model.MetaType.COMPLEX;
import static com.sdl.odata.api.edm.model.MetaType.ENTITY;
import static com.sdl.odata.api.parser.ODataUriUtil.asJavaList;
import static com.sdl.odata.api.parser.ODataUriUtil.getExpandItems;
import static com.sdl.odata.api.parser.ODataUriUtil.getSimpleExpandPropertyNames;
import static com.sdl.odata.api.parser.ODataUriUtil.hasCountOption;
import static com.sdl.odata.util.edm.EntityDataModelUtil.formatEntityKey;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getEntityName;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * Writer capable of creating a JSON stream containing either
 * a single entity (entry) or a list of OData V4 entities (feed).
 */
public class JsonWriter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonWriter.class);
    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private JsonGenerator jsonGenerator;
    private final ODataUri odataUri;
    private final EntityDataModel entityDataModel;
    private EntitySet entitySet;
    private List<String> expandedProperties = new ArrayList<>();
    private String contextURL = null;
    private final boolean forceExpand;
    private final String metadataRequest;

    /**
     * Create an OData JSON Writer.
     *
     * @param oDataUri        The OData parsed URI. It can not be {@code null}.
     * @param entityDataModel The <i>Entity Data Model (EDM)</i>. It can not be {@code null}.
     */
    public JsonWriter(ODataUri oDataUri, EntityDataModel entityDataModel, String metadataRequest) {
        this.odataUri = checkNotNull(oDataUri);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.metadataRequest = metadataRequest;
        expandedProperties.addAll(asJavaList(getSimpleExpandPropertyNames(oDataUri)));
        forceExpand = checkExpandAllParam(oDataUri) || isForceExpandParamSet(odataUri);
    }

    /**
     * Write a list of entities (feed) to the JSON stream.
     *
     * @param entities   The list of entities to fill in the JSON stream.
     * @param contextUrl The 'Context URL' to write.
     * @param meta       Additional metadata for the writer.
     * @return the rendered feed.
     * @throws ODataRenderException In case it is not possible to write to the JSON stream.
     */
    public String writeFeed(List<?> entities, String contextUrl, Map<String, Object> meta)
            throws ODataRenderException {
        this.contextURL = checkNotNull(contextUrl);

        try {
            return writeJson(entities, meta);
        } catch (IOException | IllegalAccessException | NoSuchFieldException
                | ODataEdmException | ODataRenderException e) {
            LOG.error("Not possible to marshall feed stream JSON");
            throw new ODataRenderException("Not possible to marshall feed stream JSON: ", e);
        }
    }

    /**
     * Write a single entity (entry) to the JSON stream.
     *
     * @param entity     The entity to fill in the JSON stream. It can not be {@code null}.
     * @param contextUrl The 'Context URL' to write. It can not be {@code null}.
     * @return the rendered entry
     * @throws ODataRenderException In case it is not possible to write to the JSON stream.
     */
    public String writeEntry(Object entity, String contextUrl) throws ODataRenderException {

        this.contextURL = checkNotNull(contextUrl);

        try {
            return writeJson(entity, null);
        } catch (IOException | IllegalAccessException | NoSuchFieldException |
                ODataEdmException | ODataRenderException e) {
            LOG.error("Not possible to marshall single entity stream JSON");
            throw new ODataRenderException("Not possible to marshall single entity stream JSON: ", e);
        }
    }

    /**
     * Writes raw json to the JSON stream.
     */
    public String writeRawJson(final String json, final String contextUrl) throws ODataRenderException {
        this.contextURL = checkNotNull(contextUrl);
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            jsonGenerator = JSON_FACTORY.createGenerator(stream, JsonEncoding.UTF8);
            jsonGenerator.writeRaw(json);
            jsonGenerator.close();
            return stream.toString(StandardCharsets.UTF_8.name());
        } catch (final IOException e) {
            throw new ODataRenderException("Not possible to write raw json to stream JSON: ", e);
        }
    }

    /**
     * Write the given data to the JSON stream. The data to write will be either a single entity or a feed depending on
     * whether it is a single object or list.
     *
     * @param data The given data.
     * @param meta Additional values to write.
     * @return The written JSON stream.
     * @throws ODataRenderException if unable to render
     */
    private String writeJson(Object data, Map<String, Object> meta) throws IOException, NoSuchFieldException,
            IllegalAccessException, ODataEdmException, ODataRenderException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        jsonGenerator = JSON_FACTORY.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();

        // Write @odata constants
        entitySet = (data instanceof List) ? getEntitySet((List<?>) data) : getEntitySet(data);
        if (!MediaType.METADATA_NONE.equals(metadataRequest)) {
            jsonGenerator.writeStringField(CONTEXT, contextURL);
        }

        // Write @odata.count if requested and provided.
        if (hasCountOption(odataUri) && data instanceof List &&
                meta != null && meta.containsKey("count")) {

            long count;
            Object countObj = meta.get("count");
            if (countObj instanceof Integer) {
                count = ((Integer) countObj).longValue();
            } else {
                count = (long) countObj;
            }
            jsonGenerator.writeNumberField(COUNT, count);
        }

        if (!(data instanceof List) && !MediaType.METADATA_NONE.equals(metadataRequest)) {
            if (entitySet != null) {
                jsonGenerator.writeStringField(ID, String.format("%s(%s)", getEntityName(entityDataModel, data),
                        formatEntityKey(entityDataModel, data)));
            } else {
                jsonGenerator.writeStringField(ID, String.format("%s", getEntityName(entityDataModel, data)));
            }
        }

        // Write feed
        if (data instanceof List) {
            marshallEntities((List<?>) data);
        } else {
            marshall(data, this.entityDataModel.getType(data.getClass()));
        }

        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        return stream.toString(StandardCharsets.UTF_8.name());
    }

    private void marshallEntities(List<?> entities) throws IOException,
            ODataRenderException, ODataEdmException, NoSuchFieldException, IllegalAccessException {
        jsonGenerator.writeArrayFieldStart(VALUE);
        for (Object entity : entities) {
            jsonGenerator.writeStartObject();
            if (!MediaType.METADATA_NONE.equals(metadataRequest)) {
                jsonGenerator.writeStringField(ID,
                    String.format("%s(%s)", getEntityName(entityDataModel, entity),
                        formatEntityKey(entityDataModel, entity)));
            }
            marshall(entity, entityDataModel.getType(entity.getClass()));
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void marshall(Object object, Type type)
            throws IOException, ODataRenderException, NoSuchFieldException, IllegalAccessException {
        // Decide what to do depending on what kind of type this is
        switch (type.getMetaType()) {
            case ABSTRACT:
                throw new UnsupportedOperationException("Marshalling abstract OData types is not supported");

            case PRIMITIVE:
                marshallPrimitive(object, (PrimitiveType) type);
                break;

            case ENTITY:
            case COMPLEX:
                marshallStructured(object, (StructuredType) type);
                break;

            case ENUM:
                marshallEnum(object, (EnumType) type);
                break;

            case TYPE_DEFINITION:
                marshallPrimitive(object, ((TypeDefinition) type).getUnderlyingType());
                break;

            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    private void marshallPrimitive(Object value, PrimitiveType primitiveType) throws IOException {
        LOG.debug("Primitive value: {} of type: {}", value, primitiveType);
        if (value != null) {
            JsonWriterUtil.writePrimitiveValue(value, jsonGenerator);
        } else {
            jsonGenerator.writeNull();
        }
    }

    private void marshallStructured(final Object object, StructuredType structuredType)
            throws ODataRenderException, IOException, NoSuchFieldException, IllegalAccessException {

        LOG.debug("Start structured value of type: {}", structuredType);
        if (object != null) {
            writeODataType(structuredType);

            visitProperties(entityDataModel, structuredType, property -> {
                try {
                    if (property instanceof NavigationProperty) {
                        LOG.debug("Start marshalling navigation property: {}", property.getName());
                        NavigationProperty navProperty = (NavigationProperty) property;
                        if (forceExpand || isExpandedProperty(navProperty)) {
                            final Object value = getValueFromProperty(object, navProperty);
                            if (value != null) {
                                if (navProperty.isCollection()) {
                                    jsonGenerator.writeArrayFieldStart(navProperty.getName());
                                    for (Object propertyValue : (Collection<?>) value) {
                                        jsonGenerator.writeStartObject();
                                        marshall(propertyValue, entityDataModel.getType(propertyValue.getClass()));
                                        jsonGenerator.writeEndObject();
                                    }
                                    jsonGenerator.writeEndArray();
                                } else {
                                    jsonGenerator.writeObjectFieldStart(navProperty.getName());
                                    marshall(value, entityDataModel.getType(value.getClass()));
                                    jsonGenerator.writeEndObject();
                                }
                            }
                        }
                        LOG.debug("Navigation property: {} marshalled", property.getName());
                    } else {
                        LOG.debug("Started marshalling property: {}", property.getName());
                        marshallStructuralProperty(object, property);
                        LOG.debug("Property: {} marshalled", property.getName());
                    }
                } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
                    throw new ODataRenderException("Error while writing property: " + property.getName(), e);
                }
            });
        } else {
                jsonGenerator.writeNull();
            LOG.debug("Structured value is null");
        }
        LOG.debug("End structured value of type: {}", structuredType);
    }

    /**
     * This methods write @odata.type of complex type.
     * If complex type has root-level, @odata.type won't be written.
     *
     * @param structuredType structuredType
     */
    private void writeODataType(StructuredType structuredType) throws IOException {
        if (entitySet != null) {
            String typeName = entitySet.getTypeName();
            String type = typeName.substring(typeName.lastIndexOf(".") + 1, typeName.length());

            if (MediaType.METADATA_FULL.equals(metadataRequest) || !type.equals(structuredType.getName())) {
                jsonGenerator.writeStringField(TYPE, String.format("#%s.%s",
                        structuredType.getNamespace(), structuredType.getName()));
            } else {
                LOG.debug("{} has root level. {} won't be written here", entitySet.getName(), TYPE);
            }
        }
    }

    private void marshallStructuralProperty(Object object, StructuralProperty property)
            throws ODataRenderException, IOException, NoSuchFieldException, IllegalAccessException {
        String propertyName = property.getName();

        // Get the property value through reflection
        Object propertyValue;
        Field field = property.getJavaField();
        try {
            field.setAccessible(true);
            propertyValue = field.get(object);
        } catch (IllegalAccessException e) {
            LOG.error("Error getting field value of field: " + field.toGenericString());
            throw new ODataRenderException("Error getting field value of field: " + field.toGenericString());
        }

        // Collection properties and non-nullable properties should not be null
        if (propertyValue == null) {
            if (property.isCollection()) {
                throw new ODataRenderException("Collection property has null value: " + property);
            } else if (!property.isNullable()) {
                throw new ODataRenderException("Non-nullable property has null value: " + property);
            }
        }

        // Check if the property is a collection
        if (property.isCollection()) {
            // Get an iterator for the array or collection
            Iterator<?> iterator;
            if (propertyValue.getClass().isArray()) {
                iterator = Arrays.asList((Object[]) propertyValue).iterator();
            } else if (Collection.class.isAssignableFrom(propertyValue.getClass())) {
                iterator = ((Collection<?>) propertyValue).iterator();
            } else {
                throw new UnsupportedOperationException("Unsupported collection type: " +
                        propertyValue.getClass().getName() + " for property: " + propertyName);
            }

            // Get the OData type of the elements of the collection
            Type elementType = entityDataModel.getType(property.getElementTypeName());
            if (elementType == null) {
                throw new ODataRenderException("OData type not found for elements of property: " + property);
            }

            LOG.debug("Start collection property: {}", propertyName);
            if (((Collection) propertyValue).isEmpty()) {
                jsonGenerator.writeArrayFieldStart(propertyName);
                jsonGenerator.writeEndArray();
            } else {
                while (iterator.hasNext()) {
                    Object element = iterator.next();
                    if (element instanceof Number | element instanceof String | element.getClass().isEnum()) {
                        marshallToArray(propertyName, element, iterator);
                    } else {
                        marshallCollection(propertyName, iterator, element, elementType);
                    }
                }
            }
            LOG.debug("End collection property: {}", propertyName);
        } else {
            // Single value (non-collection) property
            LOG.debug("Start property: {}", propertyName);

            // Get the OData type of the property
            Type propertyType = entityDataModel.getType(property.getTypeName());
            if (propertyType == null) {
                throw new ODataRenderException("OData type not found for property: " + property);
            }

            jsonGenerator.writeFieldName(propertyName);
            if (propertyType.getMetaType().equals(COMPLEX) && propertyValue != null) {
                jsonGenerator.writeStartObject();
            }
            marshall(propertyValue, propertyType);
            if (propertyType.getMetaType().equals(COMPLEX) && propertyValue != null) {
                jsonGenerator.writeEndObject();
            }
            LOG.debug("End property: {}", propertyName);
        }
    }

    private void marshallCollection(String propertyName, Iterator<?> iterator, Object first, Type elementType)
            throws IOException, ODataRenderException, NoSuchFieldException, IllegalAccessException {
        jsonGenerator.writeArrayFieldStart(propertyName);
        jsonGenerator.writeStartObject();
        marshall(first, elementType);
        jsonGenerator.writeEndObject();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            jsonGenerator.writeStartObject();
            marshall(element, elementType);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void marshallToArray(String propertyName, Object first, Iterator<?> iterator) throws IOException {
        jsonGenerator.writeArrayFieldStart(propertyName);
        jsonGenerator.writeObject(first.toString());
        while (iterator.hasNext()) {
            Object element = iterator.next();
            jsonGenerator.writeObject(element.toString());
        }
        jsonGenerator.writeEndArray();
    }

    /**
     * Marshall an enum value.
     *
     * @param value    The value to marshall. Can be {@code null}.
     * @param enumType The OData enum type.
     */
    private void marshallEnum(Object value, EnumType enumType) throws IOException {
        LOG.debug("Enum value: {} of type: {}", value, enumType);
        jsonGenerator.writeString(value.toString());
    }

    private boolean isExpandedProperty(NavigationProperty property) {
        return expandedProperties.contains(property.getName());
    }

    private Object getValueFromProperty(Object entity, NavigationProperty property)
            throws NoSuchFieldException, IllegalAccessException {

        Field propertyField = property.getJavaField();
        propertyField.setAccessible(true);

        return propertyField.get(entity);
    }

    private EntitySet getEntitySet(Object entity) {
        String entityTypeName = getEntityType(entity).getFullyQualifiedName();
        for (EntitySet eSet : entityDataModel.getEntityContainer().getEntitySets()) {
            if (eSet.getTypeName().equals(entityTypeName)) {
                return eSet;
            }
        }
        return null;
    }

    private EntitySet getEntitySet(List<?> entityList) {
        return entityList.size() > 0 ? getEntitySet(entityList.get(0)) : null;
    }

    private EntityType getEntityType(Object entity) {
        final Type type = entityDataModel.getType(entity.getClass());
        if (type.getMetaType() != ENTITY) {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        return (EntityType) type;
    }

    private boolean checkExpandAllParam(ODataUri oDataUri) {
        return asJavaList(getExpandItems(oDataUri)).stream().anyMatch(i -> i instanceof AllExpandItem);
    }
}
