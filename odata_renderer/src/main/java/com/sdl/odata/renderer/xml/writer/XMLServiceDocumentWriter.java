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
package com.sdl.odata.renderer.xml.writer;

import com.sdl.odata.api.edm.model.EntityContainer;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.Singleton;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ODataRenderException;
import com.sdl.odata.renderer.xml.util.XMLWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.ODATA_CONTEXT;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.ODataRendererUtils.getContextURL;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Generates service(or root) document in xml format.
 * OData uses the service document to describe the entity sets, singletons, and parameter less function imports but
 * we are supporting only entity sets and singletons.
 *
 */
public class XMLServiceDocumentWriter {
    private static final Logger LOG = LoggerFactory.getLogger(XMLServiceDocumentWriter.class);

    private static final String WORKSPACE = "workspace";
    private static final String ATOM = "atom";
    private static final String TITLE = "title";
    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    private static final String ODATA_SERVICE_NS = "http://www.w3.org/2007/app";
    private static final String SERVICE_BASE = "base";
    private static final String SERVICE = "service";
    private static final String SERVICE_COLLECTION = "collection";
    private static final String SERVICE_HREF = "href";
    private static final String SERVICE_SINGLETON = "singleton";
    private static final int BUFFER_SIZE = 1024;

    private final ODataUri oDataUri;
    private final EntityDataModel entityDataModel;

    public XMLServiceDocumentWriter(ODataUri uri, EntityDataModel entityDataModel) {
        this.oDataUri = checkNotNull(uri);
        this.entityDataModel = checkNotNull(entityDataModel);
    }

    /**
     * This is main method which generates service document.
     *
     * @return generated service (root collection) document
     * @throws ODataRenderException in case of errors
     */
    public String buildServiceDocument() throws ODataRenderException {
        LOG.info("Building service(root) document");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BUFFER_SIZE)) {
            XMLStreamWriter writer = startServiceDocument(outputStream);

            writeEntitySets(writer);
            writeSingleton(writer);

            endServiceDocument(writer);
            LOG.info("Successfully built service document");
            return outputStream.toString();
        } catch (XMLStreamException | IOException e) {
            String msg = "Something went wrong when writing service document.";
            LOG.error(msg, e);
            throw new ODataRenderException(msg, e);
        }
    }

    /**
     * This writes all singletons in entity data model as collection of "metadata:singleton".
     *
     * @param writer which writes to stream.
     * @throws XMLStreamException   in case of any xml errors
     * @throws ODataRenderException if entity container is null.
     */
    private void writeSingleton(XMLStreamWriter writer) throws XMLStreamException, ODataRenderException {
        List<Singleton> singletons = getEntityContainer().getSingletons();
        LOG.debug("Number of singletons to be written in service document are {}", singletons.size());
        for (Singleton singleton : singletons) {
            writeElement(writer, METADATA, SERVICE_SINGLETON, ODATA_METADATA_NS,
                    singleton.getName(), singleton.getName());
        }
    }

    /**
     * This writes all entity sets in entity data model as collection of elements.
     *
     * @param writer which writes to stream.
     * @throws XMLStreamException   in case of any xml errors
     * @throws ODataRenderException if entity container is null.
     */
    private void writeEntitySets(XMLStreamWriter writer) throws XMLStreamException, ODataRenderException {
        List<EntitySet> entitySets = getEntityContainer().getEntitySets();
        LOG.debug("Number of entity sets to be written in service document are {}", entitySets.size());
        for (EntitySet entitySet : entitySets) {
            if (entitySet.isIncludedInServiceDocument()) {
                writeElement(writer, null, SERVICE_COLLECTION, null, entitySet.getName(), entitySet.getName());
            }
        }
    }

    /**
     * This method writes to stream in following way with given parameters.
     * <p/>
     * <pre>
     *     &lt;app:elementName href="hrefType"&gt;
     *          &lt;atom:title type="text"&gt;title&lt;/atom:title&gt;
     *      &lt;/app:elementName&gt;
     * </pre>
     *
     * @param writer       to which writing happens. Should not be null.
     * @param prefix       is prefix of element if it is not null. Otherwise no prefix will be written out
     * @param elementName  name of the element. Should not be null.
     * @param nameSpaceURI name space of element
     * @param hrefType     type of the entity set or singleton
     * @param title        name of the entity set or singleton
     * @throws XMLStreamException
     */
    private void writeElement(XMLStreamWriter writer, String prefix, String elementName,
                              String nameSpaceURI, String hrefType, String title) throws XMLStreamException {
        if (isNullOrEmpty(prefix)) {
            writer.writeStartElement(elementName);
        } else {
            writer.writeStartElement(prefix, elementName, nameSpaceURI);
        }
        writer.writeAttribute(SERVICE_HREF, hrefType);
        writeTitle(writer, title);
        writer.writeEndElement();
    }

    /**
     * Starts "service" document with correct attributes and also writes "workspace", "title" elements.
     *
     * @param outputStream stream to write to
     * @return XMLStreamWriter to which writing happens
     * @throws XMLStreamException   in case of errors
     * @throws ODataRenderException in case of errors
     */
    private XMLStreamWriter startServiceDocument(ByteArrayOutputStream outputStream)
            throws XMLStreamException, ODataRenderException {
        XMLStreamWriter writer = XMLWriterUtil.startDocument(outputStream, null, SERVICE, ODATA_SERVICE_NS);
        writer.writeNamespace(ATOM, ATOM_NS);
        writer.writeNamespace(METADATA, ODATA_METADATA_NS);
        writer.writeNamespace(SERVICE_BASE, oDataUri.serviceRoot());
        writer.writeNamespace(ODATA_CONTEXT, getContextURL(oDataUri, entityDataModel));

        // WorkSpace element is only child of service document according to specifications.
        writer.writeStartElement(ODATA_SERVICE_NS, WORKSPACE);
        writeTitle(writer, getEntityContainer().getName());

        return writer;
    }

    private void endServiceDocument(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement(); //closing workspace
        XMLWriterUtil.endDocument(writer); //closing service document
    }

    /**
     * Returns entity container if it exists in entity data model otherwise throws ODataRenderException.
     *
     * @return EntityContainer if it exist in entity data model
     * @throws ODataRenderException if it is not exists in entity data model
     */
    private EntityContainer getEntityContainer() throws ODataRenderException {
        EntityContainer entityContainer = entityDataModel.getEntityContainer();
        if (entityContainer == null) {
            String message = "EntityContainer should not be null";
            LOG.error(message);
            throw new ODataRenderException(message);
        }
        return entityContainer;
    }

    private void writeTitle(XMLStreamWriter writer, String value) throws XMLStreamException {
        LOG.trace("atom:title value is {}", value);
        writer.writeStartElement(ATOM, TITLE, ATOM_NS);
        // 'type' attribute is not written to output because value of type is not specified in specification.
        // If it is necessary type="text" can be added here
        writer.writeCharacters(value);
        writer.writeEndElement();
    }
}
