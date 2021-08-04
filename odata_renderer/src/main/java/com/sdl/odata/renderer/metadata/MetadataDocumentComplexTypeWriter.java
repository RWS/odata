/**
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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

import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.ABSTRACT;
import static com.sdl.odata.MetadataDocumentConstants.COMPLEX_TYPE;
import static com.sdl.odata.MetadataDocumentConstants.NAME;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * <p>
 * Helper writer capable of writing {@code <ComplexType>} elements.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class MetadataDocumentComplexTypeWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentComplexTypeWriter.class);
    private final XMLStreamWriter xmlWriter;
    private final MetadataDocumentPropertyWriter propertyWriter;
    private final EntityDataModel entityDataModel;

    /**
     * Creates an instance of {@link MetadataDocumentComplexTypeWriter}.
     *
     * @param xmlWriter       The XML writer to use. It can not be {@code null}.
     * @param entityDataModel The Entity Data Model. It can not be {@code null}.
     */
    public MetadataDocumentComplexTypeWriter(XMLStreamWriter xmlWriter, EntityDataModel entityDataModel) {
        this.xmlWriter = checkNotNull(xmlWriter);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.propertyWriter = new MetadataDocumentPropertyWriter(xmlWriter);
    }

    /**
     * Write an {@code <ComplexType>} element for the given {@code ComplexType}.
     *
     * @param type The given complex type. It can not be {@code null}.
     * @throws ODataRenderException if unable to render
     */
    public void write(ComplexType type) throws ODataRenderException {

        LOG.debug("Writing type {} of type {}", type.getName(), type.getMetaType());

        try {
            xmlWriter.writeStartElement(COMPLEX_TYPE);
            xmlWriter.writeAttribute(NAME, type.getName());
            if (type.isAbstract()) {
                xmlWriter.writeAttribute(ABSTRACT, "true");
            }

            visitProperties(entityDataModel, type, property -> {
                try {
                    propertyWriter.write(property);
                } catch (XMLStreamException e) {
                    throw new ODataRenderException("Error while writing property: " + property.getName(), e);
                }
            });

            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ODataRenderException("Error while writing complex type: " + type.getName(), e);
        }
    }
}
