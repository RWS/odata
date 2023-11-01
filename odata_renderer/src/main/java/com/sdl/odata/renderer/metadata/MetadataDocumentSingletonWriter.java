/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.edm.model.NavigationPropertyBinding;
import com.sdl.odata.api.edm.model.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.NAME;
import static com.sdl.odata.MetadataDocumentConstants.NAVIGATION_PROPERTY_BINDING;
import static com.sdl.odata.MetadataDocumentConstants.PATH;
import static com.sdl.odata.MetadataDocumentConstants.SINGLETON;
import static com.sdl.odata.MetadataDocumentConstants.TARGET;
import static com.sdl.odata.MetadataDocumentConstants.TYPE;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;

/**
 * <p>
 * Helper writer capable of writing {@code <Singleton>} elements inside an {@code <EntityContainer>} element.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class MetadataDocumentSingletonWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentSingletonWriter.class);
    private final XMLStreamWriter xmlWriter;

    /**
     * Creates an instance of {@link MetadataDocumentSingletonWriter}.
     *
     * @param xmlWriter The XML writer to use. It can not be {@code null}.
     */
    public MetadataDocumentSingletonWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = checkNotNull(xmlWriter);
    }

    /**
     * Write a {@code <Singleton>} element for a given {@code Singleton}.
     *
     * @param singleton The given {@code Singleton}. It can not be {@code null}.
     * @throws javax.xml.stream.XMLStreamException if unable to write to stream
     */
    public void write(Singleton singleton) throws XMLStreamException {

        LOG.debug("Writing singleton {} of type {}", singleton.getName(), singleton.getTypeName());

        xmlWriter.writeStartElement(SINGLETON);
        xmlWriter.writeAttribute(NAME, singleton.getName());
        xmlWriter.writeAttribute(TYPE, singleton.getTypeName());
        for (NavigationPropertyBinding navPropertyBinding : singleton.getNavigationPropertyBindings()) {
            xmlWriter.writeStartElement(NAVIGATION_PROPERTY_BINDING);
            xmlWriter.writeAttribute(PATH, navPropertyBinding.getPath());
            xmlWriter.writeAttribute(TARGET, navPropertyBinding.getTarget());
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();
    }
}
