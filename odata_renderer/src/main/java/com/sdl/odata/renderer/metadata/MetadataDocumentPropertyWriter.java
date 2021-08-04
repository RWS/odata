/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.StructuralProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.NAME;
import static com.sdl.odata.MetadataDocumentConstants.NAVIGATION_PROPERTY;
import static com.sdl.odata.MetadataDocumentConstants.NULLABLE;
import static com.sdl.odata.MetadataDocumentConstants.PARTNER;
import static com.sdl.odata.MetadataDocumentConstants.PROPERTY;
import static com.sdl.odata.MetadataDocumentConstants.TYPE;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * <p>
 * Helper writer capable of writing either {@code <Property>} or {@code <NavigationProperty>} elements.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class MetadataDocumentPropertyWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentPropertyWriter.class);
    private final XMLStreamWriter xmlWriter;

    /**
     * Creates an instance of {@link MetadataDocumentPropertyWriter}.
     *
     * @param xmlWriter The XML writer to use. It can not be {@code null}.
     */
    public MetadataDocumentPropertyWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = checkNotNull(xmlWriter);
    }

    /**
     * Write either a {@code <Property>} or {@code <NavigationProperty>} element
     * for the given {@code StructuralProperty}.
     *
     * @param property The given structural property. It can not be {@code null}.
     * @throws javax.xml.stream.XMLStreamException If unable to write to stream
     */
    public void write(StructuralProperty property) throws XMLStreamException {

        LOG.debug("Writing property {} of type {}", property.getName(), property.getTypeName());

        if (property instanceof NavigationProperty) {
            NavigationProperty navProperty = (NavigationProperty) property;
            xmlWriter.writeStartElement(NAVIGATION_PROPERTY);
            writeCommonPropertyAttributes(property);
            if (!isNullOrEmpty(navProperty.getPartnerName())) {
                xmlWriter.writeAttribute(PARTNER, navProperty.getPartnerName());
            }
            xmlWriter.writeEndElement();
        } else {
            xmlWriter.writeStartElement(PROPERTY);
            writeCommonPropertyAttributes(property);
            xmlWriter.writeEndElement();
        }
    }

    private void writeCommonPropertyAttributes(StructuralProperty property) throws XMLStreamException {

        xmlWriter.writeAttribute(NAME, property.getName());
        xmlWriter.writeAttribute(TYPE, property.getTypeName());
        xmlWriter.writeAttribute(NULLABLE, Boolean.toString(property.isNullable()));
    }
}
