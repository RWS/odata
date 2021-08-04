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

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.PropertyRef;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.ABSTRACT;
import static com.sdl.odata.MetadataDocumentConstants.BASE_TYPE;
import static com.sdl.odata.MetadataDocumentConstants.ENTITY_TYPE;
import static com.sdl.odata.MetadataDocumentConstants.ENTITY_TYPE_KEY;
import static com.sdl.odata.MetadataDocumentConstants.HAS_STREAM;
import static com.sdl.odata.MetadataDocumentConstants.NAME;
import static com.sdl.odata.MetadataDocumentConstants.OPEN_TYPE;
import static com.sdl.odata.MetadataDocumentConstants.PROPERTY_REF;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static com.sdl.odata.util.edm.EntityDataModelUtil.visitProperties;

/**
 * <p>
 * Helper writer capable of writing {@code <EntityType>} elements.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class MetadataDocumentEntityTypeWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentEntityTypeWriter.class);
    private final XMLStreamWriter xmlWriter;
    private final MetadataDocumentPropertyWriter propertyWriter;
    private EntityDataModel entityDataModel;

    /**
     * Creates an instance of {@link MetadataDocumentEntityTypeWriter}.
     *
     * @param xmlWriter       The XML writer to use. It can not be {@code null}.
     * @param entityDataModel The Entity Data Model. It can not be {@code null}.
     */
    public MetadataDocumentEntityTypeWriter(XMLStreamWriter xmlWriter, EntityDataModel entityDataModel) {
        this.xmlWriter = checkNotNull(xmlWriter);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.propertyWriter = new MetadataDocumentPropertyWriter(xmlWriter);
    }

    /**
     * Write an {@code <EntityType>} element for the given {@code EntityType}.
     *
     * @param type The given entity type. It can not be {@code null}.
     * @throws ODataRenderException if unable to render
     */
    public void write(EntityType type) throws ODataRenderException {

        LOG.debug("Writing type {} of type {}", type.getName(), type.getMetaType());
        try {
            xmlWriter.writeStartElement(ENTITY_TYPE);
            if (!isNullOrEmpty(type.getBaseTypeName())) {
                xmlWriter.writeAttribute(BASE_TYPE, type.getBaseTypeName());
            }
            if (type.isOpen()) {
                xmlWriter.writeAttribute(OPEN_TYPE, Boolean.toString(type.isOpen()));
            }
            if (type.hasStream()) {
                xmlWriter.writeAttribute(HAS_STREAM, Boolean.toString(type.hasStream()));
            }
            xmlWriter.writeAttribute(NAME, type.getName());
            if (type.isAbstract()) {
                xmlWriter.writeAttribute(ABSTRACT, "true");
            }
            xmlWriter.writeStartElement(ENTITY_TYPE_KEY);
            for (PropertyRef propertyRef : type.getKey().getPropertyRefs()) {
                xmlWriter.writeStartElement(PROPERTY_REF);
                xmlWriter.writeAttribute(NAME, propertyRef.getPath());
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();

            visitProperties(entityDataModel, type, property -> {
                try {
                    propertyWriter.write(property);
                } catch (XMLStreamException e) {
                    throw new ODataRenderException("Error while writing property: " + property.getName(), e);
                }
            });
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ODataRenderException("Error while writing entity type: " + type.getName(), e);
        }

    }
}
