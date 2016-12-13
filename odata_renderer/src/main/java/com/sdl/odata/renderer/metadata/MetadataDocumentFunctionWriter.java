package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.sdl.odata.MetadataDocumentConstants.*;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;

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

    public void write(Function function) {

        try {
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
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
