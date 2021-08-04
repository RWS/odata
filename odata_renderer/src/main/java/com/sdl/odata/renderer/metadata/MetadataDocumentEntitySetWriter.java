/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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

import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.NavigationPropertyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.ENTITY_SET;
import static com.sdl.odata.MetadataDocumentConstants.ENTITY_TYPE;
import static com.sdl.odata.MetadataDocumentConstants.NAME;
import static com.sdl.odata.MetadataDocumentConstants.NAVIGATION_PROPERTY_BINDING;
import static com.sdl.odata.MetadataDocumentConstants.PATH;
import static com.sdl.odata.MetadataDocumentConstants.TARGET;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;

/**
 * <p>
 * Helper writer capable of writing {@code <EntitySet>} elements inside an {@code <EntityContainer>} element.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class MetadataDocumentEntitySetWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentEntitySetWriter.class);
    private final XMLStreamWriter xmlWriter;

    /**
     * Creates an instance of {@link MetadataDocumentEntitySetWriter}.
     *
     * @param xmlWriter The XML writer to use. It can not be {@code null}.
     */
    public MetadataDocumentEntitySetWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = checkNotNull(xmlWriter);
    }

    /**
     * Write an {@code <EntitySet>} element for a given {@code EntitySet}.
     *
     * @param entitySet The given {@code EntitySet}. It can not be {@code null}.
     * @throws javax.xml.stream.XMLStreamException If unable to write to stream
     */
    public void write(EntitySet entitySet) throws XMLStreamException {

        LOG.debug("Writing entity set {} of type {}", entitySet.getName(), entitySet.getTypeName());

        xmlWriter.writeStartElement(ENTITY_SET);
        xmlWriter.writeAttribute(NAME, entitySet.getName());
        xmlWriter.writeAttribute(ENTITY_TYPE, entitySet.getTypeName());
        for (NavigationPropertyBinding navPropertyBinding : entitySet.getNavigationPropertyBindings()) {
            xmlWriter.writeStartElement(NAVIGATION_PROPERTY_BINDING);
            xmlWriter.writeAttribute(PATH, navPropertyBinding.getPath());
            xmlWriter.writeAttribute(TARGET, navPropertyBinding.getTarget());
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();
    }
}
