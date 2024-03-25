/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.renderer.xml.writer;

import com.sdl.odata.ErrorRendererConstants;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;

import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.XML_VERSION;
import static com.sdl.odata.ErrorRendererConstants.ERROR;
import static com.sdl.odata.ErrorRendererConstants.TARGET;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Writer capable of creating an error response stream.
 */
public class XMLErrorResponseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(XMLErrorResponseWriter.class);

    private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private XMLStreamWriter xmlWriter = null;
    private ByteArrayOutputStream outputStream = null;

    /**
     * Creates an instance of {@link XMLErrorResponseWriter}.
     */
    public XMLErrorResponseWriter() {
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
            xmlWriter = xmlOutputFactory.createXMLStreamWriter(outputStream, UTF_8.name());
            xmlWriter.writeStartDocument(UTF_8.name(), XML_VERSION);
            xmlWriter.setPrefix(METADATA, ODATA_METADATA_NS);
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
     * <p>
     * Write an error for a given exception.
     * </p>
     * <p>
     * <b>Note:</b> Make sure {@link XMLErrorResponseWriter#startDocument()}
     * has been previously invoked to start the XML
     * stream document, and {@link XMLErrorResponseWriter#endDocument()} is invoked after to end it.
     * </p>
     *
     * @param exception The exception to write an error for. It can not be {@code null}.
     * @throws ODataRenderException In case it is not possible to write to the XML stream.
     */
    public void writeError(ODataException exception) throws ODataRenderException {

        checkNotNull(exception);

        try {
            xmlWriter.writeStartElement(ODATA_METADATA_NS, ERROR);
            xmlWriter.writeNamespace(METADATA, ODATA_METADATA_NS);
            xmlWriter.writeStartElement(METADATA, ErrorRendererConstants.CODE, ODATA_METADATA_NS);
            xmlWriter.writeCharacters(String.valueOf(exception.getCode().getCode()));
            xmlWriter.writeEndElement();
            xmlWriter.writeStartElement(METADATA, ErrorRendererConstants.MESSAGE, ODATA_METADATA_NS);
            xmlWriter.writeCharacters(String.valueOf(exception.getMessage()));
            xmlWriter.writeEndElement();
            if (exception.getTarget() != null) {
                xmlWriter.writeStartElement(METADATA, TARGET, ODATA_METADATA_NS);
                xmlWriter.writeCharacters(String.valueOf(exception.getTarget()));
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            LOG.error("Not possible to marshall error stream XML");
            throw new ODataRenderException("Not possible to marshall error stream XML: ", e);
        }
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
