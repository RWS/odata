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
