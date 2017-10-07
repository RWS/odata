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
package com.sdl.odata.renderer.xml.writer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ChunkedActionRenderResult;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.renderer.AbstractPropertyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

import static com.sdl.odata.AtomConstants.ELEMENT;
import static com.sdl.odata.AtomConstants.HASH;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.VALUE;
import static com.sdl.odata.ODataRendererUtils.getContextURL;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.endDocument;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.endElement;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.getNullPropertyXML;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.getPropertyXmlForPrimitives;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.getPropertyXmlForPrimitivesBodyDocument;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.getPropertyXmlForPrimitivesEndDocument;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.getPropertyXmlForPrimitivesStartDocument;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.startElement;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.writeElementWithNull;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.writePrimitiveCollection;
import static com.sdl.odata.renderer.xml.util.XMLWriterUtil.writePrimitiveElement;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;

/**
 * This class responsible for writing property in XML format.
 */
public class XMLPropertyWriter extends AbstractPropertyWriter {
    private static final Logger LOG = LoggerFactory.getLogger(XMLPropertyWriter.class);

    private XMLStreamWriter xmlStreamWriter;

    public XMLPropertyWriter(ODataUri uri, EntityDataModel entityDataModel) throws ODataRenderException {
        super(uri, entityDataModel);
    }

    @Override
    protected ChunkedActionRenderResult getPrimitivePropertyChunked(
            Object data, Type type, ChunkedStreamAction action, ChunkedActionRenderResult previousResult)
            throws ODataException {
        switch (action) {
            case START_DOCUMENT:
                String context = getContextURL(getODataUri(), getEntityDataModel(), true);
                xmlStreamWriter = getPropertyXmlForPrimitivesStartDocument(VALUE, type, data, context,
                        previousResult.getOutputStream());
                return previousResult;
            case BODY_DOCUMENT:
                getPropertyXmlForPrimitivesBodyDocument(VALUE, type, data, xmlStreamWriter);
                return previousResult;
            case END_DOCUMENT:
                getPropertyXmlForPrimitivesEndDocument(VALUE, type, data, xmlStreamWriter);
                return previousResult;
            default:
                throw new ODataRenderException(format(
                        "Unable to render primitive type value because of wrong ChunkedStreamAction: {0}",
                        action));
        }
    }

    @Override
    protected ChunkedActionRenderResult getComplexPropertyChunked(
            Object data, StructuredType type, ChunkedStreamAction action, ChunkedActionRenderResult previousResult)
            throws ODataException {
        try {
            XMLStreamWriter writer;
            OutputStream outputStream = previousResult.getOutputStream();
            switch (action) {
                case START_DOCUMENT:
                    String typeFullyQualifiedName = type.getFullyQualifiedName();
                    String context = getContextURL(getODataUri(), getEntityDataModel());
                    LOG.debug("Context for complex property is {}", context);
                    writer = startElement(outputStream, VALUE, HASH + typeFullyQualifiedName, context, true);
                    return new ChunkedActionRenderResult(outputStream, writer);
                case BODY_DOCUMENT:
                    writer = (XMLStreamWriter) previousResult.getWriter();
                    handleCollectionAndComplexProperties(data, type, writer);
                    return previousResult;
                case END_DOCUMENT:
                    writer = (XMLStreamWriter) previousResult.getWriter();
                    endDocument(writer);
                    return previousResult;
                default:
                    throw new ODataRenderException(format(
                            "Unable to render complex type value because of wrong ChunkedStreamAction: {0}",
                            action));
            }
        } catch (XMLStreamException e) {
            throw new ODataRenderException("Error while rendering complex property value", e);
        }
    }

    @Override
    protected String generateNullPropertyString() throws ODataRenderException {
        LOG.debug("Given property value is null!!");
        return getNullPropertyXML(VALUE, getContextURL(getODataUri(), getEntityDataModel()));
    }

    @Override
    protected String generatePrimitiveProperty(Object data, Type type) throws ODataRenderException {
        String context = getContextURL(getODataUri(), getEntityDataModel(), true);
        LOG.debug("Given data context is {}", context);
        return getPropertyXmlForPrimitives(VALUE, type, data, context);
    }

    @Override
    protected String generateComplexProperty(Object data, StructuredType type) throws ODataException {
        return generateXMLForComplexProperty(data, type);
    }

    private String generateXMLForComplexProperty(Object entity, StructuredType type) throws ODataException {
        LOG.debug("Complex property rendering started");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String typeFullyQualifiedName = type.getFullyQualifiedName();
            String context = getContextURL(getODataUri(), getEntityDataModel());
            LOG.debug("Context for complex property is {}", context);
            XMLStreamWriter writer = startElement(outputStream, VALUE, HASH + typeFullyQualifiedName, context, true);
            handleCollectionAndComplexProperties(entity, type, writer);
            endElement(writer);
            return outputStream.toString(UTF_8.name());
        } catch (XMLStreamException | IOException e) {
            throw new ODataRenderException("Error while rendering complex property value.", e);
        }
    }

    private void handleCollectionAndComplexProperties(Object entity, StructuredType type, XMLStreamWriter writer)
            throws XMLStreamException, ODataRenderException {
        if (isCollection(entity)) {
            LOG.debug("Given property is collection of complex values");
            for (Object obj : (List) entity) {
                writer.writeStartElement(ODATA_METADATA_NS, ELEMENT);
                writeAllProperties(obj, type, writer);
                writer.writeEndElement();
            }
        } else {
            LOG.debug("Given property is single complex value");
            writeAllProperties(entity, type, writer);
        }
    }

    private void writeAllProperties(final Object entity, StructuredType type, final XMLStreamWriter writer)
            throws ODataRenderException {
        visitProperties(getEntityDataModel(), type, property -> {
            try {
                if (!(property instanceof NavigationProperty)) {
                    handleProperty(entity, property, writer);
                }
            } catch (XMLStreamException | ODataException | IllegalAccessException e) {
                throw new ODataRenderException("Error while writing property: " + property.getName(), e);
            }
        });
    }

    private void handleProperty(Object entity, StructuralProperty property, XMLStreamWriter writer)
            throws IllegalAccessException, XMLStreamException, ODataException {
        Field field = property.getJavaField();
        field.setAccessible(true);
        Object value = field.get(entity);
        LOG.trace("Property name is '{}' and its value is '{}'", property.getName(), value);
        Type type = getType(value);
        if (type == null) {
            String msg = String.format("Field type %s is not found in entity data model", field.getType());
            LOG.error(msg);
            throw new ODataRenderException(msg);
        }
        switch (type.getMetaType()) {
            case PRIMITIVE:
                handleCollectionsAndPrimitiveProperties(writer, property.getName(), value);
                break;
            case COMPLEX:
                writer.writeStartElement(property.getName());
                handleCollectionAndComplexProperties(value, (StructuredType) type, writer);
                writer.writeEndElement();
                break;
            default:
                defaultHandling(type);
        }

    }

    private void handleCollectionsAndPrimitiveProperties(XMLStreamWriter writer, String name, Object value)
            throws XMLStreamException {
        if (isCollection(value)) {
            writePrimitiveCollection(writer, name, (List) value);
        } else if (value == null) {
            writeElementWithNull(writer, name);
        } else {
            writePrimitiveElement(writer, name, value);
        }
    }
}
