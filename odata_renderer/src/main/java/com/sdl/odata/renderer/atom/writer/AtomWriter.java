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
package com.sdl.odata.renderer.atom.writer;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.renderer.ODataRenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sdl.odata.AtomConstants.ATOM_ENTRY;
import static com.sdl.odata.AtomConstants.ATOM_FEED;
import static com.sdl.odata.AtomConstants.ATOM_LINK;
import static com.sdl.odata.AtomConstants.HREF;
import static com.sdl.odata.AtomConstants.ID;
import static com.sdl.odata.AtomConstants.INLINE;
import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.ODATA_ASSOCIATION_LINK_REL_NS_PREFIX;
import static com.sdl.odata.AtomConstants.ODATA_DATA;
import static com.sdl.odata.AtomConstants.ODATA_ENTRY_LINK_TYPE_PATTERN;
import static com.sdl.odata.AtomConstants.ODATA_FEED_LINK_TYPE_PATTERN;
import static com.sdl.odata.AtomConstants.ODATA_NAVIGATION_LINK_REL_NS_PREFIX;
import static com.sdl.odata.AtomConstants.REF;
import static com.sdl.odata.AtomConstants.REL;
import static com.sdl.odata.AtomConstants.TITLE;
import static com.sdl.odata.AtomConstants.TYPE;
import static com.sdl.odata.AtomConstants.XML_VERSION;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.ODataRendererUtils.isForceExpandParamSet;
import static com.sdl.odata.api.parser.ODataUriUtil.asJavaList;
import static com.sdl.odata.api.parser.ODataUriUtil.getSimpleExpandPropertyNames;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.util.edm.EntityDataModelUtil.formatEntityKey;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getAndCheckEntityType;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getEntityName;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getPropertyValue;
import static com.sdl.odata.util.edm.EntityDataModelUtil.isSingletonEntity;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Writer capable of creating an Atom XML stream containing either a single entity (entry) or a list of OData V4
 * entities (feed).
 */
public class AtomWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AtomWriter.class);
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    private XMLStreamWriter xmlWriter = null;
    private ByteArrayOutputStream outputStream = null;
    private AtomMetadataWriter metadataWriter = null;
    private AtomDataWriter dataWriter = null;
    private final ZonedDateTime dateTime;
    private final ODataUri oDataUri;
    private final EntityDataModel entityDataModel;
    private final AtomNSConfigurationProvider nsConfigurationProvider;
    // Note: At the moment only a list of comma-separated properties are supported in the $expand operation
    private final List<String> expandedProperties = new ArrayList<>();
    private String contextURL;
    private final boolean isWriteOperation;
    private final boolean isDeepInsert;
    private final boolean isActionCall;
    private final boolean forceExpand;

    /**
     * Creates an instance of {@link AtomWriter} specifying the local date and time to stamp in the XML to write.
     *
     * @param dateTime                The given date and time. It can not be {@code null}.
     * @param oDataUri                The OData parsed URI. It can not be {@code null}.
     * @param entityDataModel         The <i>Entity Data Model (EDM)</i>. It can not be {@code null}.
     * @param nsConfigurationProvider The configuration provider for namespaces
     * @param isWriteOperation        True if this is a write operation or false if its a read operation
     * @param isActionCall            True if this is a action call
     */
    public AtomWriter(ZonedDateTime dateTime, ODataUri oDataUri, EntityDataModel entityDataModel,
                      AtomNSConfigurationProvider nsConfigurationProvider,
                      boolean isWriteOperation, boolean isActionCall) {
        this(dateTime, oDataUri, entityDataModel, nsConfigurationProvider, isWriteOperation, isActionCall, false);
    }

    /**
     * Creates an instance of {@link AtomWriter} specifying the local date and time to stamp in the XML to write.
     *
     * @param dateTime                The given date and time. It can not be {@code null}.
     * @param oDataUri                The OData parsed URI. It can not be {@code null}.
     * @param entityDataModel         The <i>Entity Data Model (EDM)</i>. It can not be {@code null}.
     * @param nsConfigurationProvider The configuration provider for namespaces
     * @param isWriteOperation        True if this is a write operation or false if its a read operation
     * @param isActionCall            True if this is a action call
     * @param isDeepInsert            True if this is a deep insert
     */
    public AtomWriter(ZonedDateTime dateTime, ODataUri oDataUri, EntityDataModel entityDataModel,
                      AtomNSConfigurationProvider nsConfigurationProvider,
                      boolean isWriteOperation, boolean isActionCall, boolean isDeepInsert) {

        this.dateTime = checkNotNull(dateTime);
        this.oDataUri = checkNotNull(oDataUri);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.isWriteOperation = checkNotNull(isWriteOperation);
        this.nsConfigurationProvider = checkNotNull(nsConfigurationProvider);
        this.isDeepInsert = isDeepInsert;
        this.isActionCall = isActionCall;

        expandedProperties.addAll(asJavaList(getSimpleExpandPropertyNames(oDataUri)));
        forceExpand = isForceExpandParamSet(oDataUri);
    }

    /**
     * Start the XML stream document by defining things like the type of encoding, and prefixes used. It needs to be
     * used before calling any write method.
     *
     * @throws ODataRenderException if unable to render the feed
     */
    public void startDocument() throws ODataRenderException {

        outputStream = new ByteArrayOutputStream();
        try {
            xmlWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, UTF_8.name());
            metadataWriter = new AtomMetadataWriter(xmlWriter, oDataUri, entityDataModel, nsConfigurationProvider);
            dataWriter = new AtomDataWriter(xmlWriter, entityDataModel, nsConfigurationProvider);
            xmlWriter.writeStartDocument(UTF_8.name(), XML_VERSION);
            xmlWriter.setPrefix(METADATA, nsConfigurationProvider.getOdataMetadataNs());
            xmlWriter.setPrefix(ODATA_DATA, nsConfigurationProvider.getOdataDataNs());
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
     * <p> Write a list of entities (feed) to the XML stream. </p> <p> <b>Note:</b> Make sure {@link
     * AtomWriter#startDocument()} has been previously invoked to start the XML stream document, and {@link
     * AtomWriter#endDocument()} is invoked after to end it. </p>
     *
     * @param entities          The list of entities to fill in the XML stream. It can not {@code null}.
     * @param requestContextURL The 'Context URL' to write for the feed. It can not {@code null}.
     * @param meta              Additional metadata to write.
     * @throws ODataRenderException In case it is not possible to write to the XML stream.
     */
    public void writeFeed(List<?> entities, String requestContextURL, Map<String, Object> meta)
            throws ODataRenderException {
        writeStartFeed(requestContextURL, meta);
        writeBodyFeed(entities);
        writeEndFeed();
    }

    /**
     * Write start feed to the XML stream.
     *
     * @param requestContextURL The 'Context URL' to write for the feed. It can not {@code null}.
     * @param meta              Additional metadata to write.
     * @throws ODataRenderException In case it is not possible to write to the XML stream.
     */
    public void writeStartFeed(String requestContextURL, Map<String, Object> meta) throws ODataRenderException {
        this.contextURL = checkNotNull(requestContextURL);
        try {
            startFeed(false);

            if (ODataUriUtil.hasCountOption(oDataUri) &&
                    meta != null && meta.containsKey("count")) {
                metadataWriter.writeCount(meta.get("count"));
            }

            metadataWriter.writeFeedId(null, null);
            metadataWriter.writeTitle();
            metadataWriter.writeUpdate(dateTime);
            metadataWriter.writeFeedLink(null, null);
        } catch (XMLStreamException | ODataEdmException e) {
            LOG.error("Not possible to marshall feed stream XML");
            throw new ODataRenderException("Not possible to marshall feed stream XML: ", e);
        }
    }

    /**
     * Write feed body.
     *
     * @param entities The list of entities to fill in the XML stream. It can not {@code null}.
     * @throws ODataRenderException In case it is not possible to write to the XML stream.
     */
    public void writeBodyFeed(List<?> entities) throws ODataRenderException {
        checkNotNull(entities);
        try {
            for (Object entity : entities) {
                writeEntry(entity, true);
            }
        } catch (XMLStreamException | IllegalAccessException | NoSuchFieldException | ODataEdmException e) {
            LOG.error("Not possible to marshall feed stream XML");
            throw new ODataRenderException("Not possible to marshall feed stream XML: ", e);
        }
    }

    /**
     * Write end feed.
     *
     * @throws ODataRenderException In case it is not possible to write to the XML stream.
     */
    public void writeEndFeed() throws ODataRenderException {
        try {
            endFeed();
        } catch (XMLStreamException e) {
            LOG.error("Not possible to marshall feed stream XML");
            throw new ODataRenderException("Not possible to marshall feed stream XML: ", e);
        }
    }

    /**
     * <p> Write a single entity to the XML stream. </p> <p> <b>Note:</b> Make sure {@link AtomWriter#startDocument()}
     * has been previously invoked to start the XML stream document, and {@link AtomWriter#endDocument()} is invoked
     * after to end it. </p>
     *
     * @param entity            The entity to fill in the XML stream. It can not be {@code null}.
     * @param requestContextURL The 'Context URL' to write for the feed. It can not {@code null}.
     * @throws ODataRenderException In case it is not possible to write to the XML stream.
     */
    public void writeEntry(Object entity, String requestContextURL) throws
            ODataRenderException {

        checkNotNull(entity);
        this.contextURL = checkNotNull(requestContextURL);

        try {
            writeEntry(entity, false);
        } catch (XMLStreamException | IllegalAccessException | NoSuchFieldException | ODataEdmException e) {
            LOG.error("Not possible to render single entity stream XML");
            throw new ODataRenderException("Not possible to render single entity stream XML: ", e);
        }
    }

    /**
     * Get the generated XML.
     *
     * @return The generated XML.
     */
    public String getXml() {
        try {
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return outputStream.toString();
        }
    }

    /**
     * <p>Write a list of entities (feed) to the XML stream.</p> <p>The entities to write will be either the main
     * content of the feed XML stream, or in-lined as part of the content of navigation property (relevant for the
     * $expand operation).</p>
     *
     * @param entities        The list of entities to fill in the XML stream.
     * @param enclosingEntity Entity that enclose this list of entities.
     *                        If it is null, it implies that the feed is at the root.
     * @param property        The NavigationProperty for which the feed is generated.
     * @param meta            Additional metadata to write.
     * @throws XMLStreamException     if unable to render the feed
     * @throws ODataRenderException   if unable to render the feed
     * @throws NoSuchFieldException   if unable to render the feed
     * @throws IllegalAccessException if unable to render the feed
     */
    private void writeFeed(Collection<?> entities, Object enclosingEntity, NavigationProperty property,
                           Map<String, Object> meta) throws XMLStreamException, ODataRenderException,
            NoSuchFieldException, IllegalAccessException, ODataEdmException {

        final boolean isInlineFeed = (enclosingEntity != null);

        startFeed(isInlineFeed);

        if (ODataUriUtil.hasCountOption(oDataUri) &&
                meta != null && meta.containsKey("count")) {
            metadataWriter.writeCount(meta.get("count"));
        }

        metadataWriter.writeFeedId(enclosingEntity, property);
        metadataWriter.writeTitle();
        metadataWriter.writeUpdate(dateTime);
        metadataWriter.writeFeedLink(enclosingEntity, property);

        for (Object entity : entities) {
            writeEntry(entity, true);
        }

        endFeed();
    }

    private void writeEntry(Object entity, boolean isFeedEntry) throws XMLStreamException,
            ODataRenderException, NoSuchFieldException, IllegalAccessException, ODataEdmException {

        EntityType entityType = getAndCheckEntityType(entityDataModel, entity.getClass());

        startEntry(isFeedEntry);
        metadataWriter.writeEntryId(entity);
        metadataWriter.writeTitle();
        metadataWriter.writeSummary();
        metadataWriter.writeUpdate(dateTime);
        metadataWriter.writeAuthor();
        metadataWriter.writeEntryEntityLink(entity);

        for (StructuralProperty property : entityType.getStructuralProperties()) {
            if (property instanceof NavigationProperty) {
                // Nullable navigation properties that have null values should not be included in the output of writes
                if (isWriteOperation) {
                    final Object value = getPropertyValue(property, entity);
                    if (value != null) {
                        NavigationProperty navigationProperty = (NavigationProperty) property;
                        writeEntryPropertyLink(entity, navigationProperty);
                    }
                } else {
                    NavigationProperty navigationProperty = (NavigationProperty) property;
                    writeEntryPropertyLink(entity, navigationProperty);
                }
            }
        }
        metadataWriter.writeEntryCategory(entity);

        // Note Iterate through all the entity properties in order to write elements of type <data:Property>
        // (including nested entities)
        dataWriter.writeData(entity, entityType);

        endEntry();
    }

    private void startFeed(boolean isInline) throws XMLStreamException {

        xmlWriter.writeStartElement(ATOM_FEED);
        if (!isInline) {
            metadataWriter.writeODataMetadata(contextURL);
        }
    }

    private void endFeed() throws XMLStreamException {
        xmlWriter.writeEndElement();
    }

    private void startEntry(boolean isFeedEntry) throws XMLStreamException {

        xmlWriter.writeStartElement(ATOM_ENTRY);
        if (!isFeedEntry) {
            metadataWriter.writeODataMetadata(contextURL);
        }
    }

    private void endEntry() throws XMLStreamException {

        xmlWriter.writeEndElement();
    }

    private void writeEntryPropertyLink(Object entity, NavigationProperty property) throws XMLStreamException,
            ODataRenderException, NoSuchFieldException, IllegalAccessException,
            ODataEdmException {

        String linkType = property.isCollection() ? ODATA_FEED_LINK_TYPE_PATTERN : ODATA_ENTRY_LINK_TYPE_PATTERN;

        // The navigation link
        startLink();
        xmlWriter.writeAttribute(REL, ODATA_NAVIGATION_LINK_REL_NS_PREFIX + property.getName());
        xmlWriter.writeAttribute(TYPE, String.format(linkType, ATOM_XML.toString()));
        xmlWriter.writeAttribute(TITLE, property.getName());

        // Deep inserts allow us to create referenced entities as part of a single create entity operation. See spec:
        // http://docs.oasis-open.org/odata/odata-atom-format/v4.0/cs02/odata-atom-format-v4.0-cs02.html#_Toc372792739:
        if (isDeepInsert) {
            // Handle deep insert create operations (only applicable to POST)
            xmlWriter.writeAttribute(HREF, getHrefAttributeValue(entity, property));

            final Object value = getPropertyValue(property, entity);

            // Only write inline elements for referenced entities that have values
            if (property.isCollection()) {
                if (value != null && ((Collection) value).size() > 0) {
                    startMetadata();
                    writeFeed((Collection<?>) value, entity, property, null);
                    endMetadata();
                }
            } else if (value != null) {
                startMetadata();
                writeEntry(value, true);
                endMetadata();
            }

        } else if (isWriteOperation && !isActionCall) {
            // Handle Bind operations
            final Object value = getPropertyValue(property, entity);

            if (property.isCollection()) {
                xmlWriter.writeAttribute(HREF, String.format("%s(%s)/%s", getEntityName(entityDataModel, entity),
                        formatEntityKey(entityDataModel, entity), property.getName()));
                if (((Collection<?>) value).size() > 0) {
                    writeCollectionRefs(((Collection<?>) value));
                }
            } else if (value != null) {
                if (isSingletonEntity(entityDataModel, getPropertyValue(property, entity))) {
                    xmlWriter.writeAttribute(HREF, String.format("%s", getEntityName(entityDataModel, value)));
                } else {
                    xmlWriter.writeAttribute(HREF, String.format("%s(%s)", getEntityName(entityDataModel, value),
                            formatEntityKey(entityDataModel, value)));
                }
            }
        } else if (isActionCall || expandedProperties.contains(property.getName()) || forceExpand) {
            xmlWriter.writeAttribute(HREF, getHrefAttributeValue(entity, property));

            final Object value = getPropertyValue(property, entity);

            startMetadata();
            if (value != null) {
                if (property.isCollection()) {
                    writeFeed((Collection<?>) value, entity, property, null);
                } else {
                    writeEntry(value, true);
                }
            }
            endMetadata();
        } else {
            xmlWriter.writeAttribute(HREF, getHrefAttributeValue(entity, property));
        }
        endLink();

        // The association link
        startLink();
        xmlWriter.writeAttribute(REL, ODATA_ASSOCIATION_LINK_REL_NS_PREFIX + property.getName());
        xmlWriter.writeAttribute(TYPE, XML.toString());
        xmlWriter.writeAttribute(TITLE, property.getName());

        if (isSingletonEntity(entityDataModel, entity)) {
            xmlWriter.writeAttribute(HREF, String.format("%s/%s/$ref",
                    getEntityName(entityDataModel, entity), property.getName()));
        } else {
            xmlWriter.writeAttribute(HREF, String.format("%s(%s)/%s/$ref", getEntityName(entityDataModel, entity),
                    formatEntityKey(entityDataModel, entity), property.getName()));
        }

        endLink();
    }

    private void writeCollectionRefs(Collection<?> collection) throws XMLStreamException, ODataEdmException {
        startMetadata();
        startFeed(true);
        for (Object entity : collection) {
            writeMetadataRef(entity);
        }
        endFeed();
        endMetadata();
    }

    private void writeMetadataRef(Object entity) throws XMLStreamException, ODataEdmException {
        xmlWriter.writeStartElement(METADATA, REF, "");
        xmlWriter.writeAttribute(ID, String.format("%s(%s)", getEntityName(entityDataModel, entity),
                formatEntityKey(entityDataModel, entity)));
        xmlWriter.writeEndElement();
    }

    private void startMetadata() throws XMLStreamException {
        xmlWriter.writeStartElement(METADATA, INLINE, "");
    }

    private void endMetadata() throws XMLStreamException {
        xmlWriter.writeEndElement();
    }

    private void startLink() throws XMLStreamException {
        xmlWriter.writeStartElement(ATOM_LINK);
    }

    private void endLink() throws XMLStreamException {
        xmlWriter.writeEndElement();
    }

    private String getHrefAttributeValue(Object entity, NavigationProperty property) throws ODataEdmException {
        if (isSingletonEntity(entityDataModel, entity)) {
            return String.format("%s/%s", getEntityName(entityDataModel, entity), property.getName());
        } else {
            return String.format("%s(%s)/%s", getEntityName(entityDataModel, entity),
                    formatEntityKey(entityDataModel, entity), property.getName());
        }
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }
}
