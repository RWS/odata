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
package com.sdl.odata.renderer.atom.writer;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.edm.model.TypeDefinition;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.sdl.odata.AtomConstants.COLLECTION;
import static com.sdl.odata.AtomConstants.ELEMENT;
import static com.sdl.odata.AtomConstants.HASH;
import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.NULL;
import static com.sdl.odata.AtomConstants.ODATA_CONTENT;
import static com.sdl.odata.AtomConstants.ODATA_DATA;
import static com.sdl.odata.AtomConstants.ODATA_PROPERTIES;
import static com.sdl.odata.AtomConstants.TYPE;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * <p>
 * Helper writer capable of writing OData data elements for an {@code <entry>} element.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class AtomDataWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AtomDataWriter.class);
    private final XMLStreamWriter xmlWriter;
    private final EntityDataModel entityDataModel;
    private final AtomNSConfigurationProvider nsConfigurationProvider;

    /**
     * Creates an instance of {@link AtomDataWriter} by specifying the writer to use.
     *
     * @param xmlWriter                 The XML writer to use. It can not be {@code null}.
     * @param entityDataModel           The Entity Data Model. It can not be {@code null}.
     * @param nsConfigurationProvider   The NameSpace provider to provide OData Atom specific namespaces.
     */
    public AtomDataWriter(XMLStreamWriter xmlWriter, EntityDataModel entityDataModel,
                          AtomNSConfigurationProvider nsConfigurationProvider) {
        this.xmlWriter = checkNotNull(xmlWriter);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.nsConfigurationProvider = checkNotNull(nsConfigurationProvider);
    }

    /**
     * Write the data for a given entity.
     *
     * @param entity     The given entity.
     * @param entityType The entity type.
     * @throws XMLStreamException if unable to render the entity
     * @throws ODataRenderException if unable to render the entity
     */
    public void writeData(Object entity, EntityType entityType) throws XMLStreamException, ODataRenderException {

        xmlWriter.writeStartElement(ODATA_CONTENT);
        xmlWriter.writeAttribute(TYPE, XML.toString());
        xmlWriter.writeStartElement(METADATA, ODATA_PROPERTIES, "");

        marshall(entity, entityType);

        xmlWriter.writeEndElement();
        xmlWriter.writeEndElement();
    }

    /**
     * Marshall an object with a given OData type.
     *
     * @param object The object to marshall. Can be {@code null}.
     * @param type   The OData type of the object.
     * @throws XMLStreamException If an error occurs while marshalling.
     */
    private void marshall(Object object, Type type) throws XMLStreamException, ODataRenderException {
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

    /**
     * Marshall a primitive value.
     *
     * @param value         The value to marshall. Can be {@code null}.
     * @param primitiveType The OData primitive type.
     */
    private void marshallPrimitive(Object value, PrimitiveType primitiveType) throws XMLStreamException {

        LOG.debug("Primitive value: {} of type: {}", value, primitiveType);
        if (value != null) {
            xmlWriter.writeCharacters(value.toString());
        }
    }

    /**
     * Marshall an object that is of an OData structured type (entity type or complex type).
     *
     * @param object         The object to marshall. Can be {@code null}.
     * @param structuredType The structured type.
     * @throws ODataRenderException If an error occurs while rendering.
     * @throws XMLStreamException        If an error occurs while rendering.
     */
    private void marshallStructured(final Object object, StructuredType structuredType)
            throws ODataRenderException, XMLStreamException {

        LOG.debug("Start structured value of type: {}", structuredType);

        if (object != null) {
            visitProperties(entityDataModel, structuredType, property -> {
                try {
                    if (!(property instanceof NavigationProperty)) {
                        marshallStructuralProperty(object, property);
                    }
                } catch (XMLStreamException e) {
                    throw new ODataRenderException("Error while writing property: " + property.getName(), e);
                }
            });
        } else {
            LOG.debug("Structured value is null");
        }

        LOG.debug("End structured value of type: {}", structuredType);
    }

    /**
     * Marshall a property of an object.
     *
     * @param object   The object that contains the property. Must not be {@code null}.
     * @param property The property.
     * @throws ODataRenderException If an error occurs while rendering.
     * @throws XMLStreamException        If an error occurs while rendering.
     */
    private void marshallStructuralProperty(Object object, StructuralProperty property)
            throws ODataRenderException, XMLStreamException {

        String propertyName = property.getName();

        // Get the property value through reflection
        Object propertyValue;
        Field field = property.getJavaField();
        try {
            field.setAccessible(true);
            propertyValue = field.get(object);
        } catch (IllegalAccessException e) {
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
        final String odataDataNS = nsConfigurationProvider.getOdataDataNs();
        final String odataMetadataNs = nsConfigurationProvider.getOdataMetadataNs();
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
            xmlWriter.writeStartElement(ODATA_DATA, propertyName, odataDataNS);
            if (elementType.getMetaType().equals(MetaType.PRIMITIVE)) {
                xmlWriter.writeAttribute(METADATA, odataMetadataNs, TYPE, HASH + COLLECTION
                        + "(" + elementType.getName() + ")");
            } else {
                xmlWriter.writeAttribute(METADATA, odataMetadataNs, TYPE, HASH + COLLECTION
                        + "(" + elementType.getFullyQualifiedName() + ")");
            }

            while (iterator.hasNext()) {
                Object element = iterator.next();
                xmlWriter.writeStartElement(METADATA, ELEMENT, odataMetadataNs);
                marshall(element, elementType);
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
            LOG.debug("End collection property: {}", propertyName);
        } else {
            // Single value (non-collection) property
            LOG.debug("Start property: {}", propertyName);

            // Get the OData type of the property
            Type propertyType = entityDataModel.getType(property.getTypeName());
            if (propertyType == null) {
                throw new ODataRenderException("OData type not found for property: " + property);
            }

            xmlWriter.writeStartElement(ODATA_DATA, propertyName, odataDataNS);
            if (propertyValue == null) {
                xmlWriter.writeAttribute(METADATA, odataMetadataNs, NULL, "true");
            }

            switch (propertyType.getMetaType()) {
                case PRIMITIVE:
                    PrimitiveType primitiveType = (PrimitiveType) propertyType;
                    if (!primitiveType.equals(PrimitiveType.STRING)) {
                        xmlWriter.writeAttribute(METADATA, odataMetadataNs, TYPE, primitiveType.getName());
                    }
                    break;

                case COMPLEX:
                case ENTITY:
                case ENUM:
                    xmlWriter.writeAttribute(METADATA, odataMetadataNs, TYPE, HASH
                            + propertyType.getFullyQualifiedName());
                    break;

                default:
                    throw new UnsupportedOperationException("Unsupported meta type: " + propertyType.getMetaType());
            }

            marshall(propertyValue, propertyType);
            xmlWriter.writeEndElement();
            LOG.debug("End property: {}", propertyName);
        }
    }

    /**
     * Marshall an enum value.
     *
     * @param value    The value to marshall. Can be {@code null}.
     * @param enumType The OData enum type.
     */
    private void marshallEnum(Object value, EnumType enumType) throws XMLStreamException {

        LOG.debug("Enum value: {} of type: {}", value, enumType);
        xmlWriter.writeCharacters(value.toString());
    }
}
