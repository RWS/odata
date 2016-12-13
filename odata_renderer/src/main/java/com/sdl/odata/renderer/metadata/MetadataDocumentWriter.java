/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.edm.model.Singleton;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;

import static com.sdl.odata.MetadataDocumentConstants.*;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Writer capable of creating an XML stream containing the 'Metadata Document'.
 */
public class MetadataDocumentWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentWriter.class);
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    private XMLStreamWriter xmlWriter = null;
    private ByteArrayOutputStream outputStream = null;
    private MetadataDocumentEntityTypeWriter entityTypeWriter = null;
    private MetadataDocumentComplexTypeWriter complexTypeWriter = null;
    private MetadataDocumentEnumTypeWriter enumTypeWriter = null;
    private MetadataDocumentEntitySetWriter entitySetWriter = null;
    private MetadataDocumentSingletonWriter singletonWriter = null;
    private MetadataDocumentFunctionWriter functionWriter = null;
    private final EntityDataModel entityDataModel;

    /**
     * Create an instance of {@link MetadataDocumentWriter}.
     *
     * @param entityDataModel The Entity Data Model. It can not be {@code null}.
     */
    public MetadataDocumentWriter(EntityDataModel entityDataModel) {
        this.entityDataModel = checkNotNull(entityDataModel);
    }

    /**
     * Start the XML stream document by defining things like the type of encoding, and prefixes used.
     * It needs to be used before calling any write method.
     *
     * @throws ODataRenderException if unable to render
     */
    public void startDocument() throws ODataRenderException {

        outputStream = new ByteArrayOutputStream();
        try {
            xmlWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, UTF_8.name());
            entityTypeWriter = new MetadataDocumentEntityTypeWriter(xmlWriter, entityDataModel);
            complexTypeWriter = new MetadataDocumentComplexTypeWriter(xmlWriter, entityDataModel);
            enumTypeWriter = new MetadataDocumentEnumTypeWriter(xmlWriter);
            entitySetWriter = new MetadataDocumentEntitySetWriter(xmlWriter);
            singletonWriter = new MetadataDocumentSingletonWriter(xmlWriter);
            functionWriter = new MetadataDocumentFunctionWriter(xmlWriter);

            xmlWriter.writeStartDocument(UTF_8.name(), XML_VERSION);
            xmlWriter.setPrefix(EDMX_PREFIX, EDMX_NS);
        } catch (XMLStreamException e) {
            LOG.error("Not possible to start stream XML");
            throw new ODataRenderException("Not possible to start stream XML: ", e);
        }
    }

    /**
     * End the XML stream document.
     *
     * @throws ODataRenderException if unable to render
     */
    public void endDocument() throws ODataRenderException {

        try {
            xmlWriter.writeEndDocument();
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            LOG.error("Not possible to end stream XML");
            throw new ODataRenderException("Not possible to end stream XML: ", e);
        }
    }

    /**
     * Write the 'Metadata Document'.
     *
     * @throws ODataRenderException if unable to render metadata document
     */
    public void writeMetadataDocument() throws ODataRenderException {

        try {
            xmlWriter.writeStartElement(EDMX_NS, EDMX);
            xmlWriter.writeNamespace(EDMX_PREFIX, EDMX_NS);
            xmlWriter.writeAttribute(VERSION, ODATA_VERSION);
            xmlWriter.writeStartElement(EDMX_NS, EDMX_DATA_SERVICES);

            boolean entityContinerWritten = false;
            // Loop over all the schemas present in the Entity Data Model
            for (Schema schema : entityDataModel.getSchemas()) {
                xmlWriter.writeStartElement(SCHEMA);
                xmlWriter.writeDefaultNamespace(EDM_NS);
                xmlWriter.writeAttribute(NAMESPACE, schema.getNamespace());

                for (Type type : schema.getTypes()) {
                    switch (type.getMetaType()) {
                        case ENTITY:
                            entityTypeWriter.write((EntityType) type);
                            break;
                        case COMPLEX:
                            complexTypeWriter.write((ComplexType) type);
                            break;
                        case ENUM:
                            enumTypeWriter.write((EnumType) type);
                            break;
                        default:
                            LOG.error("Unexpected type: {}", type.getFullyQualifiedName());
                            throw new ODataRenderException("Unexpected type: " + type.getFullyQualifiedName());
                    }
                }

                for (Function function : schema.getFunctions()) {
                    functionWriter.write(function);
                }

                if (!entityContinerWritten) {
                    writeEntityContainer(entityDataModel.getEntityContainer());
                    entityContinerWritten = true;
                }

                // End of <Schema> element
                xmlWriter.writeEndElement();
            }

            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            LOG.error("Not possible to start stream XML");
            throw new ODataRenderException("Not possible to start stream XML: ", e);
        }
    }

    /**
     * Write the 'Entity Container'.
     * <p>
     * Note: The entity container is written only for the 'Schema' which namespace matches the name of the entity
     * container itself. This entity container contains the types for all the schemas contained by the Entity Data
     * Model though.
     * </p>
     *
     * @param entityContainer The 'Entity Container' to write.
     * @throws XMLStreamException
     */
    private void writeEntityContainer(EntityContainer entityContainer) throws XMLStreamException {

        xmlWriter.writeStartElement(ENTITY_CONTAINER);
        xmlWriter.writeAttribute(NAME, entityContainer.getName());
        for (EntitySet entitySet : entityContainer.getEntitySets()) {
            entitySetWriter.write(entitySet);
        }
        for (Singleton singleton : entityContainer.getSingletons()) {
            singletonWriter.write(singleton);
        }
        xmlWriter.writeEndElement();
    }

    /**
     * Get the generated XML.
     *
     * @return The generated XML.
     */
    public String getXml() {

        return outputStream.toString();
    }
}
