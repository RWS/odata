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
package com.sdl.odata.renderer.atom.writer;

import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import scala.Option;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.sdl.odata.AtomConstants.ATOM_AUTHOR;
import static com.sdl.odata.AtomConstants.ATOM_AUTHOR_ODATA_FRAMEWORK;
import static com.sdl.odata.AtomConstants.ATOM_CATEGORY;
import static com.sdl.odata.AtomConstants.ATOM_ID;
import static com.sdl.odata.AtomConstants.ATOM_LINK;
import static com.sdl.odata.AtomConstants.ATOM_NAME;
import static com.sdl.odata.AtomConstants.ATOM_NS;
import static com.sdl.odata.AtomConstants.ATOM_SUMMARY;
import static com.sdl.odata.AtomConstants.ATOM_UPDATED;
import static com.sdl.odata.AtomConstants.COUNT;
import static com.sdl.odata.AtomConstants.EDIT;
import static com.sdl.odata.AtomConstants.HASH;
import static com.sdl.odata.AtomConstants.HREF;
import static com.sdl.odata.AtomConstants.METADATA;
import static com.sdl.odata.AtomConstants.ODATA_CONTEXT;
import static com.sdl.odata.AtomConstants.ODATA_DATA;
import static com.sdl.odata.AtomConstants.ODATA_SCHEME_NS;
import static com.sdl.odata.AtomConstants.ODATA_XML_BASE;
import static com.sdl.odata.AtomConstants.REL;
import static com.sdl.odata.AtomConstants.SCHEME;
import static com.sdl.odata.AtomConstants.SELF;
import static com.sdl.odata.AtomConstants.TERM;
import static com.sdl.odata.AtomConstants.TITLE;
import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static com.sdl.odata.api.parser.ODataUriUtil.getEntitySetId;
import static com.sdl.odata.api.parser.ODataUriUtil.getEntitySetName;
import static com.sdl.odata.util.edm.EntityDataModelUtil.formatEntityKey;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getAndCheckEntityType;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getEntityName;
import static com.sdl.odata.util.edm.EntityDataModelUtil.isSingletonEntity;

/**
 * <p>
 * Helper writer capable of writing metadata elements for {@code <feed>} and {@code <entry>} elements. Those elements
 * include:
 * </p>
 * <ul>
 * <li>{@code <id>} element (both for {@code <feed>} and {@code <entry>}).</li>
 * <li>{@code <title>} element (both for {@code <feed>} and {@code <entry>}).</li>
 * <li>{@code <updated>} element (both for {@code <feed>} and {@code <entry>}).</li>
 * <li>{@code <link>} element (for {@code <feed>} and {@code <entry>}, excluding navigation properties).</li>
 * <li>{@code <summary>} element (only for {@code <entry>}).</li>
 * <li>{@code <author>} element (only for {@code <entry>}).</li>
 * <li>{@code <category>} element (both for {@code <feed>} and {@code <entry>}).</li>
 * </ul>
 * <p>
 * Please note that it is necessary to open the XML writer used by instances of this class before calling any method,
 * and close it after the writing process is finished.
 * </p>
 */
public class AtomMetadataWriter {

    private final XMLStreamWriter xmlWriter;
    private final ODataUri oDataUri;
    private final EntityDataModel entityDataModel;
    private final AtomNSConfigurationProvider nsConfigurationProvider;

    /**
     * Creates an instance of {@link AtomMetadataWriter}
     * by specifying the writer to use.
     *
     * @param xmlWriter       The XML writer to use. It can not be {@code null}.
     * @param oDataUri        The OData URI. It can not be {@code null}.
     * @param entityDataModel The Entity Data Model. It can not be {@code null}.
     * @param nsConfigurationProvider The NameSpace provider to provide OData Atom specific namespaces.
     */
    public AtomMetadataWriter(XMLStreamWriter xmlWriter, ODataUri oDataUri,
                              EntityDataModel entityDataModel, AtomNSConfigurationProvider nsConfigurationProvider) {
        this.xmlWriter = checkNotNull(xmlWriter);
        this.oDataUri = checkNotNull(oDataUri);
        this.entityDataModel = checkNotNull(entityDataModel);
        this.nsConfigurationProvider = checkNotNull(nsConfigurationProvider);
    }

    /**
     * Write the metadata XML attributes of the root element (both {@code <feed>} and {@code <entry>}).
     *
     * @param contextURL The 'Context URL' to write.
     * @throws XMLStreamException If unable to write to stream
     */
    void writeODataMetadata(String contextURL) throws XMLStreamException {

        xmlWriter.writeNamespace(METADATA, nsConfigurationProvider.getOdataMetadataNs());
        xmlWriter.writeNamespace(ODATA_DATA, nsConfigurationProvider.getOdataDataNs());
        xmlWriter.writeDefaultNamespace(ATOM_NS);
        xmlWriter.writeAttribute(nsConfigurationProvider.getOdataMetadataNs(), ODATA_CONTEXT, contextURL);
        xmlWriter.writeAttribute(ODATA_XML_BASE, oDataUri.serviceRoot());
    }

    /**
     * Write a {@code <title>} element.
     *
     * @throws XMLStreamException If unable to write to stream
     */
    void writeTitle() throws XMLStreamException {

        xmlWriter.writeStartElement(TITLE);
        xmlWriter.writeEndElement();
    }

    /**
     * Write a {@code <summary>} element.
     *
     * @throws XMLStreamException If unable to write to stream
     */
    void writeSummary() throws XMLStreamException {

        xmlWriter.writeStartElement(ATOM_SUMMARY);
        xmlWriter.writeEndElement();
    }

    /**
     * Write a {@code <updated>} element.
     *
     * @throws XMLStreamException If unable to write to stream
     */
    void writeUpdate(ZonedDateTime dateTime) throws XMLStreamException {

        xmlWriter.writeStartElement(ATOM_UPDATED);
        xmlWriter.writeCharacters(dateTime.format(DateTimeFormatter.ISO_INSTANT));
        xmlWriter.writeEndElement();
    }

    /**
     * Write an {@code <author>} element.
     *
     * @throws XMLStreamException If unable to write to stream
     */
    void writeAuthor() throws XMLStreamException {

        xmlWriter.writeStartElement(ATOM_AUTHOR);
        xmlWriter.writeStartElement(ATOM_NAME);
        xmlWriter.writeCharacters(ATOM_AUTHOR_ODATA_FRAMEWORK);
        xmlWriter.writeEndElement();
        xmlWriter.writeEndElement();
    }

    /**
     * Write an {@code <id>} element when the parent element is an {@code <entry>} element.
     *
     * @param entity The entity to be contained by the {@code <entry>} element.
     * @throws XMLStreamException If unable to write to stream
     * @throws ODataEdmException  If unable to write entry to stream
     */
    void writeEntryId(Object entity) throws XMLStreamException, ODataEdmException {

        xmlWriter.writeStartElement(ATOM_ID);
        xmlWriter.writeCharacters(getEntryIdString(entity));
        xmlWriter.writeEndElement();
    }

    /**
     * Write an {@code <id>} element when the parent element is a {@code <feed>} element.
     *
     * @throws XMLStreamException If unable to write to stream
     * @throws ODataEdmException  If unable to write feed id to stream
     */
    void writeFeedId() throws XMLStreamException, ODataEdmException {
        writeFeedId(null, null);
    }

    /**
     * Write an {@code <id>} element when the parent element is a {@code <feed>} element.
     *
     * @param entity   The enclosing entity for which the feed is getting generated.
     * @param property The property that will be expanded in the feed.
     * @throws XMLStreamException If unable to write to stream
     * @throws ODataEdmException  If unable to write feed id to stream
     */
    void writeFeedId(Object entity, NavigationProperty property) throws XMLStreamException, ODataEdmException {

        xmlWriter.writeStartElement(ATOM_ID);

        if (entity != null) {
            xmlWriter.writeCharacters(String.format("%s/%s/%s", oDataUri.serviceRoot(),
                    getEntityWithKey(entity), property.getName()));
        } else {
            String id;
            if (ODataUriUtil.isActionCallUri(oDataUri) ||
                    ODataUriUtil.isFunctionCallUri(oDataUri)) {
                id = buildFeedIdFromOperationCall(oDataUri);
            } else {
                id = getEntitySetId(oDataUri).get();
            }
            xmlWriter.writeCharacters(id);
        }
        xmlWriter.writeEndElement();
    }

    private String buildFeedIdFromOperationCall(ODataUri odataUri) {
        String serviceRoot = odataUri.serviceRoot();
        String returnType = ODataUriUtil.getOperationReturnType(odataUri, entityDataModel);
        return serviceRoot + "/" + returnType;
    }

    /**
     * Write a feed {@code <link>} element.
     *
     * @throws XMLStreamException If unable to write to stream
     */
    void writeFeedLink(Object entity, NavigationProperty property) throws XMLStreamException, ODataEdmException {

        xmlWriter.writeStartElement(ATOM_LINK);
        xmlWriter.writeAttribute(REL, SELF);

        if (entity == null) {
            if (ODataUriUtil.isActionCallUri(oDataUri) || ODataUriUtil.isFunctionCallUri(oDataUri)) {
                Option<TargetType> targetTypeOption = ODataUriUtil.resolveTargetType(oDataUri, entityDataModel);
                if (targetTypeOption.isDefined()) {
                    TargetType targetType = targetTypeOption.get();
                    String entitySetName = EntityDataModelUtil.getEntitySetByEntityTypeName(entityDataModel,
                            targetType.typeName()).getName();
                    xmlWriter.writeAttribute(TITLE, entitySetName);
                    xmlWriter.writeAttribute(HREF, entitySetName);
                } else {
                    throw new ODataEdmException("Failed to resolve entity target type");
                }

            } else {
                xmlWriter.writeAttribute(TITLE, getEntitySetName(oDataUri).get());
                xmlWriter.writeAttribute(HREF, getEntitySetName(oDataUri).get());
            }
        } else {
            xmlWriter.writeAttribute(TITLE, property.getName());
            xmlWriter.writeAttribute(HREF, String.format("%s/%s", getEntityWithKey(entity), property.getName()));
        }

        xmlWriter.writeEndElement();
    }

    /**
     * Write a {@code <link>} element for a given entity.
     *
     * @param entity The given entity.
     * @throws XMLStreamException If unable to write to stream
     * @throws ODataEdmException  if unable to check entity types
     */
    public void writeEntryEntityLink(Object entity) throws XMLStreamException, ODataEdmException {

        EntityType entityType = getAndCheckEntityType(entityDataModel, entity.getClass());
        xmlWriter.writeStartElement(ATOM_LINK);
        if (entityType.isReadOnly()) {
            xmlWriter.writeAttribute(REL, SELF);
        } else {
            xmlWriter.writeAttribute(REL, EDIT);
        }
        xmlWriter.writeAttribute(TITLE, entityType.getName());
        xmlWriter.writeAttribute(HREF, getEntityWithKey(entity));
        xmlWriter.writeEndElement();
    }

    /**
     * Write a {@code <category>} element for a given entity.
     *
     * @param entity The given entity.
     * @throws XMLStreamException If unable to write to stream
     */
    void writeEntryCategory(Object entity) throws XMLStreamException {

        Type entityType = entityDataModel.getType(entity.getClass());
        xmlWriter.writeStartElement(ATOM_CATEGORY);
        xmlWriter.writeAttribute(SCHEME, ODATA_SCHEME_NS);
        xmlWriter.writeAttribute(TERM, String.format("%s%s.%s", HASH, entityType.getNamespace(), entityType.getName()));
        xmlWriter.writeEndElement();
    }

    private String getEntityWithKey(Object entity) throws ODataEdmException {
        if (isSingletonEntity(entityDataModel, entity)) {
            return String.format("%s", getEntityName(entityDataModel, entity));
        } else {
            return String.format("%s(%s)", getEntityName(entityDataModel, entity),
                    formatEntityKey(entityDataModel, entity));
        }
    }

    private String getEntryIdString(Object entity) throws ODataEdmException {
        if (isSingletonEntity(entityDataModel, entity)) {
            return String.format("%s/%s", oDataUri.serviceRoot(), getEntityName(entityDataModel, entity));
        } else {
            return String.format("%s/%s(%s)", oDataUri.serviceRoot(),
                    getEntityName(entityDataModel, entity), formatEntityKey(entityDataModel, entity));
        }
    }

    public void writeCount(Object count) throws XMLStreamException {
        xmlWriter.writeStartElement(METADATA, COUNT, nsConfigurationProvider.getOdataMetadataNs());
        xmlWriter.writeCharacters(String.valueOf(count));
        xmlWriter.writeEndElement();
    }
}
