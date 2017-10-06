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
package com.sdl.odata.renderer.xml.util;

import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.sdl.odata.AtomConstants.ELEMENT;
import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.NULL;
import static com.sdl.odata.AtomConstants.ODATA_CONTEXT;
import static com.sdl.odata.AtomConstants.ODATA_DATA;
import static com.sdl.odata.AtomConstants.ODATA_DATA_NS;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.TYPE;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class is util class for writing atom xml properties.
 */
public final class XMLWriterUtil {

    private XMLWriterUtil() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(XMLWriterUtil.class);

    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    public static String getPropertyXmlForPrimitives(String rootName, Type type, Object data, String context)
            throws ODataRenderException {
        LOG.debug("PropertyXMLForPrimitives invoked with {}, {}, {}", rootName, type, data);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XMLStreamWriter writer = startElement(outputStream, rootName, type.getName(), context, false);
            // write values
            if (data instanceof List<?>) {
                writeMultipleElementsForPrimitives(writer, (List<?>) data);
            } else {
                writer.writeCharacters(data.toString());
            }
            endElement(writer);
            return outputStream.toString(UTF_8.name());
        } catch (XMLStreamException | IOException e) {
            throw new ODataRenderException("Error while rendering primitive property value.", e);
        }
    }

    public static XMLStreamWriter getPropertyXmlForPrimitivesStartDocument(
            String rootName, Type type, Object data, String context, OutputStream outputStream)
            throws ODataRenderException {
        LOG.debug("PropertyXMLForPrimitivesStartDocument invoked with {}, {}, {}", rootName, type, data);
        try {
            return startElement(outputStream, rootName, type.getName(), context, false);
        } catch (XMLStreamException e) {
            throw new ODataRenderException("Error while rendering start document primitive property value.", e);
        }
    }

    public static void getPropertyXmlForPrimitivesBodyDocument(
            String rootName, Type type, Object data, XMLStreamWriter xmlStreamWriter) throws ODataRenderException {
        LOG.debug("PropertyXMLForPrimitivesBodyDocument invoked with {}, {}, {}", rootName, type, data);
        try {
            // write values
            if (data instanceof List<?>) {
                writeMultipleElementsForPrimitives(xmlStreamWriter, (List<?>) data);
            } else {
                xmlStreamWriter.writeCharacters(data.toString());
            }
        } catch (XMLStreamException e) {
            throw new ODataRenderException("Error while rendering body document primitive property value.", e);
        }
    }

    public static void getPropertyXmlForPrimitivesEndDocument(
            String rootName, Type type, Object data, XMLStreamWriter xmlStreamWriter)
            throws ODataRenderException {
        LOG.debug("PropertyXMLForPrimitivesEndDocument invoked with {}, {}, {}", rootName, type, data);
        try {
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ODataRenderException("Error while rendering end document primitive property value.", e);
        }
    }

    /**
     * Creates document root with given name.
     *
     * @param outputStream     stream to write to
     * @param prefix           if given prefix is not null then root element will be prefixed
     *                         followed by ":" and documentRootName
     * @param documentRootName name of the document root element. This should not be null. Null check not done
     * @param nameSpaceURI     name space of the element. This should not be null. Null check not done
     * @return created XMLStreamWriter will be returned
     * @throws XMLStreamException If unable to open stream
     */
    public static XMLStreamWriter startDocument(ByteArrayOutputStream outputStream,
                                                String prefix, String documentRootName, String nameSpaceURI)
            throws XMLStreamException {
        XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, UTF_8.name());
        if (isNullOrEmpty(prefix)) {
            writer.writeStartElement(documentRootName);
            writer.setDefaultNamespace(nameSpaceURI);
            writer.writeDefaultNamespace(nameSpaceURI);
        } else {
            writer.writeStartElement(prefix, documentRootName, nameSpaceURI);
            writer.writeNamespace(prefix, nameSpaceURI);
        }
        return writer;
    }

    public static void endDocument(XMLStreamWriter writer) throws XMLStreamException {
        endElement(writer);
        writer.close();
    }

    public static XMLStreamWriter startElement(OutputStream outputStream, String rootName, String typeName,
                                               String context, boolean defaultNameSpace) throws XMLStreamException {
        XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, UTF_8.name());
        writer.writeStartElement(METADATA, rootName, ODATA_METADATA_NS);
        if (defaultNameSpace) {
            LOG.debug("Starting {} element with default data namespace", rootName);
            writer.setDefaultNamespace(ODATA_DATA_NS);
            writer.writeDefaultNamespace(ODATA_DATA_NS);
        } else {
            LOG.debug("Starting {} element without default namespaces", rootName);
            writer.writeNamespace(ODATA_DATA, ODATA_DATA_NS);
        }
        writer.writeNamespace(METADATA, ODATA_METADATA_NS);
        writer.writeAttribute(ODATA_METADATA_NS, ODATA_CONTEXT, context);
        if (!PrimitiveType.STRING.getName().equals(typeName)) {
            writer.writeAttribute(ODATA_METADATA_NS, TYPE, typeName);
        }
        return writer;
    }

    public static void writePrimitiveElement(XMLStreamWriter writer, String nodeName, Object nodeValue)
            throws XMLStreamException {
        LOG.trace("Field name is {}, field value is {}", nodeName, nodeValue);
        writer.writeStartElement(nodeName);
        writer.writeCharacters(nodeValue.toString());
        writer.writeEndElement();
    }

    public static void writePrimitiveCollection(XMLStreamWriter writer, String nodeName, List values)
            throws XMLStreamException {
        LOG.trace("Field name is {}, field value is {}", nodeName, values);
        writer.writeStartElement(nodeName);
        for (Object object : values) {
            writer.writeStartElement(ODATA_METADATA_NS, ELEMENT);
            writer.writeCharacters(object.toString());
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    public static void writeElementWithNull(XMLStreamWriter writer, String nodeName) throws XMLStreamException {
        LOG.debug("Field {} with value null", nodeName);
        writer.writeStartElement(nodeName);
        writer.writeAttribute(NULL, "true");
        writer.writeEndElement();
    }

    public static void endElement(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        writer.flush();
    }


    public static String getNullPropertyXML(String rootName, String context) throws ODataRenderException {
        LOG.debug("NullPropertyXML invoked with {}", rootName);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, UTF_8.name());
            writer.writeStartElement(METADATA, rootName, ODATA_METADATA_NS);
            writer.writeNamespace(METADATA, ODATA_METADATA_NS);
            writer.writeAttribute(ODATA_METADATA_NS, ODATA_CONTEXT, context);
            writer.writeAttribute(ODATA_METADATA_NS, NULL, "true");

            endElement(writer);
            return outputStream.toString(UTF_8.name());
        } catch (XMLStreamException | IOException e) {
            throw new ODataRenderException("Error while rendering null property.", e);
        }
    }

    private static void writeMultipleElementsForPrimitives(XMLStreamWriter writer, List<?> list)
            throws XMLStreamException {
        for (Object obj : list) {
            writer.writeStartElement(METADATA, ELEMENT, ODATA_METADATA_NS);
            writer.writeCharacters(obj.toString());
            writer.writeEndElement();
        }
    }
}
