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
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.FUNCTION;
import static com.sdl.odata.MetadataDocumentConstants.NAME;
import static com.sdl.odata.MetadataDocumentConstants.PARAMETER;
import static com.sdl.odata.MetadataDocumentConstants.TYPE;
import static com.sdl.odata.MetadataDocumentConstants.NULLABLE;
import static com.sdl.odata.MetadataDocumentConstants.RETURN_TYPE;

import static com.sdl.odata.ODataRendererUtils.checkNotNull;

/**
 * <p>
 * Helper writer capable of writing  {@code <Function>} elements.
 * </p>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class MetadataDocumentFunctionWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDocumentSingletonWriter.class);
    private final XMLStreamWriter xmlWriter;

    /**
     * Creates an instance of {@link MetadataDocumentSingletonWriter}.
     *
     * @param xmlWriter The XML writer to use. It can not be {@code null}.
     */
    public MetadataDocumentFunctionWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = checkNotNull(xmlWriter);
    }

    public void write(Function function) throws XMLStreamException {

        xmlWriter.writeStartElement(FUNCTION);
        xmlWriter.writeAttribute(NAME, function.getName());

        for (Parameter parameter : function.getParameters()) {
            xmlWriter.writeStartElement(PARAMETER);
            xmlWriter.writeAttribute(NAME, parameter.getName());
            xmlWriter.writeAttribute(TYPE, parameter.getType());
            xmlWriter.writeAttribute(NULLABLE, Boolean.toString(parameter.isNullable()));
            xmlWriter.writeEndElement();
        }

        xmlWriter.writeStartElement(RETURN_TYPE);
        xmlWriter.writeAttribute(TYPE, function.getReturnType());
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement();

    }

}
