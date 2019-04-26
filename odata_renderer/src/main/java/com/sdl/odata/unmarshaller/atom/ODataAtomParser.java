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
package com.sdl.odata.unmarshaller.atom;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataNotImplementedException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.ComplexType;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.edm.model.Type;
import com.sdl.odata.api.parser.ODataParser;
import com.sdl.odata.api.parser.util.ParserUtil;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.unmarshaller.AbstractParser;
import com.sdl.odata.unmarshaller.PropertyType;
import com.sdl.odata.util.edm.EntityDataModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static com.sdl.odata.AtomConstants.ATOM_CATEGORY;
import static com.sdl.odata.AtomConstants.ATOM_ENTRY;
import static com.sdl.odata.AtomConstants.ATOM_FEED;
import static com.sdl.odata.AtomConstants.ATOM_ID;
import static com.sdl.odata.AtomConstants.ATOM_LINK;
import static com.sdl.odata.AtomConstants.ATOM_NS;
import static com.sdl.odata.AtomConstants.ATOM_UPDATED;
import static com.sdl.odata.AtomConstants.COLLECTION;
import static com.sdl.odata.AtomConstants.ELEMENT;
import static com.sdl.odata.AtomConstants.FEED_METADATA_ATOM_ID_IDX;
import static com.sdl.odata.AtomConstants.FEED_METADATA_LINK_IDX;
import static com.sdl.odata.AtomConstants.FEED_METADATA_MIN_ITEMS;
import static com.sdl.odata.AtomConstants.FEED_METADATA_TITLE_IDX;
import static com.sdl.odata.AtomConstants.FEED_METADATA_UPDATED_IDX;
import static com.sdl.odata.AtomConstants.HREF;
import static com.sdl.odata.AtomConstants.ID;
import static com.sdl.odata.AtomConstants.INLINE;
import static com.sdl.odata.AtomConstants.NULL;
import static com.sdl.odata.AtomConstants.ODATA_CONTENT;
import static com.sdl.odata.AtomConstants.ODATA_METADATA_NS;
import static com.sdl.odata.AtomConstants.ODATA_NAVIGATION_LINK_REL_NS_PREFIX;
import static com.sdl.odata.AtomConstants.ODATA_PROPERTIES;
import static com.sdl.odata.AtomConstants.ODATA_SCHEME_NS;
import static com.sdl.odata.AtomConstants.REF;
import static com.sdl.odata.AtomConstants.REL;
import static com.sdl.odata.AtomConstants.SCHEME;
import static com.sdl.odata.AtomConstants.TERM;
import static com.sdl.odata.AtomConstants.TITLE;
import static com.sdl.odata.AtomConstants.TYPE;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static com.sdl.odata.util.edm.EntityDataModelUtil.getStructuralProperty;

/**
 * The OData Atom Parser.
 */
public class ODataAtomParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(ODataAtomParser.class);
    private final Set<String> foundCollectionProperties = new HashSet<>();
    private static final int COLLECTION_INDEX = 11;

    public ODataAtomParser(ODataRequestContext context, ODataParser uriParser) {
        super(context, uriParser);
    }

    /**
     * Document Builder Factory.
     */
    public static final DocumentBuilderFactory DOCBUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    static {
        DOCBUILDER_FACTORY.setNamespaceAware(true);
    }

    @Override
    protected Object processEntity(String bodyText) throws ODataException {
        return processEntity(parseXML(bodyText).getDocumentElement());
    }

    @Override
    protected List<?> processEntities(String bodyText) throws ODataException {
        return processEntities(parseXML(bodyText).getDocumentElement());
    }

    private Document parseXML(String xml) throws ODataUnmarshallingException {
        try {
            return DOCBUILDER_FACTORY.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
            throw new ODataUnmarshallingException("Error while parsing XML", e);
        } catch (IOException | ParserConfigurationException e) {
            throw new ODataSystemException(e);
        }
    }

    private Object processEntity(Element entryElement) throws ODataException {
        if (!entryElement.getNodeName().equals(ATOM_ENTRY)) {
            throw new ODataUnmarshallingException("Expected <entry> as the root element, but found: " +
                    entryElement.getNodeName());
        }

        EntityType entityType = getEntityType(entryElement);

        Object entity;
        try {
            Class<?> javaType = entityType.getJavaType();
            LOG.debug("Creating new instance of type: {}", javaType.getName());
            entity = javaType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ODataUnmarshallingException("Error while instantiating entity of type: " +
                    entityType.getFullyQualifiedName(), e);
        }

        setEntityProperties(entity, entityType, entryElement);
        setEntityNavigationProperties(entity, entityType, entryElement);
        ensureNonNullableCollectionArePresent(entityType);
        return entity;
    }

    private List<?> processEntities(Element feedElement) throws ODataException {
        checkFeedMetadata(feedElement);
        List<Object> entities = new ArrayList<>();
        for (Element feedEntry : getFeedEntries(feedElement)) {
            entities.add(processEntity(feedEntry));
        }
        return entities;
    }

    protected String getOdataSchemeNS() {
        return ODATA_SCHEME_NS;
    }

    protected String getEntityTerm(Element element) {
        String entityTypeName = null;

        String term = (element.getAttribute(TERM) == null) ? "" : element.getAttribute(TERM);
        int index = term.lastIndexOf('#');
        if (index >= 0 && term.length() > index + 1) {
            entityTypeName = term.substring(index + 1);
        }
        return entityTypeName;
    }

    private EntityType getEntityType(Element entryElement) throws ODataUnmarshallingException {
        NodeList elements = entryElement.getElementsByTagNameNS(ATOM_NS, ATOM_CATEGORY);
        // Note that when a payload has nested inline feed or entries, there are more than one <category> element. In
        // this case, by iterating in reverse mode, we are sure we pick the right <category> element; this is, the
        // <category> element that belongs to the main entry and not the ones that belongs to the inline entries.
        for (int i = elements.getLength() - 1; i >= 0; i--) {
            Element element = (Element) elements.item(i);
            String scheme = element.getAttribute(SCHEME);
            if (scheme != null && scheme.equals(getOdataSchemeNS())) {
                String entityTypeName = getEntityTerm(element);

                if (entityTypeName == null) {
                    throw new ODataUnmarshallingException("Found a <category> element, but its term attribute " +
                            "does not correctly specify the entity type: term=\"" + element.getAttribute(TERM) + "\"");
                }

                LOG.debug("Found entity type name: {}", entityTypeName);
                Type type = getEntityDataModel().getType(entityTypeName);

                if (type == null) {
                    throw new ODataUnmarshallingException("Entity type does not exist in the entity data model: " +
                            entityTypeName);
                }

                if (type.getMetaType() != MetaType.ENTITY) {
                    throw new ODataUnmarshallingException("This type exists in the entity data model, but it is " +
                            "not an entity type: " + entityTypeName + "; it is: " + type.getMetaType());
                }

                return (EntityType) type;
            } else {
                LOG.debug("Found a <category> element with an unexpected 'scheme' attribute: " + scheme);
            }
        }

        throw new ODataUnmarshallingException("No <category> element found with attribute scheme=\""
                + getOdataSchemeNS() + "\" that specifies the entity type.");
    }

    protected String getODataMetadataNS() {
        return ODATA_METADATA_NS;
    }

    private void setEntityProperties(Object entity, EntityType entityType, Element entryElement)
            throws ODataException {
        LOG.trace("setEntityProperties: entityType={}", entityType);

        NodeList childNodes = entryElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element && node.getNodeName().equals(ODATA_CONTENT)) {
                Element contentElement = (Element) node;

                NodeList propertiesElements =
                        contentElement.getElementsByTagNameNS(getODataMetadataNS(), ODATA_PROPERTIES);
                for (int j = 0; j < propertiesElements.getLength(); j++) {
                    Element propertiesElement = (Element) propertiesElements.item(j);

                    NodeList propertyNodes = propertiesElement.getChildNodes();
                    for (int k = 0; k < propertyNodes.getLength(); k++) {
                        Node propertyNode = propertyNodes.item(k);
                        if (propertyNode instanceof Element) {
                            setStructProperty(entity, entityType, (Element) propertyNode);
                        }
                    }
                }
            }
        }
    }

    private void setStructProperty(Object instance, StructuredType structType, Element propertyElement)
            throws ODataException {
        String propertyName = propertyElement.getLocalName();
        LOG.debug("Found property element: {}", propertyName);

        PropertyType propertyTypeFromXML = getPropertyTypeFromXML(propertyElement);
        if (propertyTypeFromXML == null) {
            LOG.debug("Skip rendering for {} property", propertyName);
            return;
        }

        LOG.debug("Property type from XML: {}", propertyTypeFromXML);

        StructuralProperty property = getStructuralProperty(getEntityDataModel(), structType, propertyName);
        if (property == null) {
            if (!structType.isOpen()) {
                LOG.debug("{} property is not found in the following {} type. Ignoring",
                        propertyName, structType.toString());
                return;
            } else {
                throw new ODataNotImplementedException("Open types are not supported, cannot set property value " +
                        "for property '" + propertyName + "' in instance of type: " + structType);
            }
        }


        if (propertyTypeFromXML.isCollection()) {
            if (!property.isCollection()) {
                throw new ODataUnmarshallingException("The type of the property '" + propertyName + "' is a " +
                        "collection type: " + propertyTypeFromXML + ", but according to the entity data model it " +
                        "is not a collection: " + property.getTypeName());
            }
        } else {
            if (property.isCollection()) {
                throw new ODataUnmarshallingException("The type of the property '" + propertyName + "' is not a " +
                        "collection type: " + propertyTypeFromXML + ", but according to the entity data model it " +
                        "is a collection: " + property.getTypeName());
            }
        }

        Object propertyValue;
        if (propertyTypeFromXML.isCollection()) {
            foundCollectionProperties.add(propertyName);
            Class<?> fieldType = EntityDataModelUtil.getPropertyJavaType(property);
            propertyValue = parsePropertyValueCollection(propertyElement, propertyTypeFromXML.getType(), fieldType);
        } else {
            propertyValue = parsePropertyValueSingle(propertyElement, propertyTypeFromXML.getType());
        }

        boolean notNullableProperty = propertyValue != null;

        LOG.debug("Property value: {} ({})", propertyValue,
                notNullableProperty ? propertyValue.getClass().getName() : "<null>");

        try {
            Field field = property.getJavaField();
            field.setAccessible(true);
            field.set(instance, propertyValue);
        } catch (IllegalAccessException e) {
            throw new ODataUnmarshallingException("Error while setting property value for property '" +
                    propertyName + "': " + propertyValue, e);
        }
    }

    private PropertyType getPropertyTypeFromXML(Element propertyElement) throws ODataUnmarshallingException {
        String propertyName = propertyElement.getLocalName();

        Type type;
        boolean collection = false;

        String typeName = propertyElement.getAttributeNS(getODataMetadataNS(), TYPE);

        // If there is no type attribute, then use the default, which is String
        if (isNullOrEmpty(typeName)) {
            typeName = PrimitiveType.STRING.getName();
        }

        // If the type name contains a '#', then remove everything up to and including the '#'
        int index = typeName.lastIndexOf('#');
        if (index >= 0) {
            if (typeName.length() < index + 1) {
                throw new ODataUnmarshallingException("The type attribute is specified incorrectly on this " +
                        "property element: <" + propertyName + " type=\"" + typeName + "\">");
            }

            typeName = typeName.substring(index + 1);
        }

        // Check if it is a collection
        if (typeName.startsWith(COLLECTION + "(")) {
            collection = true;
            typeName = typeName.substring(COLLECTION_INDEX, typeName.length() - 1);
        }

        // If the type name does not contain a '.', then it is probably an unqualified primitive type name;
        // prefix it with the standard namespace
        if (typeName.indexOf('.') < 0) {
            typeName = EntityDataModel.EDM_NAMESPACE + "." + typeName;
        }

        if (typeName.equals("Edm.DateTime")) {
            typeName = EntityDataModel.EDM_NAMESPACE + "." + PrimitiveType.DATE_TIME_OFFSET.getName();
        }

        type = getEntityDataModel().getType(typeName);
        if (type == null) {
            LOG.debug("Type for property {} is not found", propertyName);
            return null;
        }

        return new PropertyType(type, collection);
    }

    private Object parsePropertyValueCollection(Element propertyElement, Type elementType, Class<?> javaCollectionType)
            throws ODataException {
        Collection<Object> result;
        if (List.class.isAssignableFrom(javaCollectionType)) {
            result = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(javaCollectionType)) {
            result = new HashSet<>();
        } else {
            throw new ODataNotImplementedException("Unsupported collection type: " + javaCollectionType.getName() +
                    "; only List and Set are supported");
        }

        NodeList elements = propertyElement.getChildNodes();
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (ELEMENT.equals(node.getLocalName()) && getODataMetadataNS().equals(node.getNamespaceURI())) {
                Element element = (Element) node;
                result.add(parsePropertyValueSingle(element, elementType));
            }
        }

        return result;
    }

    private Object parsePropertyValueSingle(Element propertyElement, Type type) throws ODataException {
        String nullAttr = propertyElement.getAttributeNS(getODataMetadataNS(), NULL);
        if (nullAttr != null && nullAttr.equals("true")) {
            return null;
        }

        switch (type.getMetaType()) {
            case PRIMITIVE:
                return parsePropertyValuePrimitive(propertyElement, (PrimitiveType) type);

            case ENUM:
                return parsePropertyValueEnum(propertyElement, (EnumType) type);

            case COMPLEX:
                return parsePropertyValueComplex(propertyElement, (ComplexType) type);

            default:
                throw new ODataUnmarshallingException("The property '" + propertyElement.getLocalName() + "' must be " +
                        "of a PRIMITIVE, ENUM or COMPLEX type; something else was found instead: " + type +
                        " (" + type.getMetaType() + ")");
        }
    }

    private Object parsePropertyValuePrimitive(Element propertyElement, PrimitiveType primitiveType)
            throws ODataException {
        return ParserUtil.parsePrimitiveValue(propertyElement.getTextContent().trim(), primitiveType);
    }

    private Object parsePropertyValueEnum(Element propertyElement, EnumType enumType) throws ODataException {
        String text = propertyElement.getTextContent().trim();
        String[] values = text.split(",");
        if (values.length > 1) {
            throw new ODataNotImplementedException("Multiple enum values are not supported, type: " + enumType +
                    " for value: " + text);
        }

        return ParserUtil.parseEnumValue(values[0].trim(), enumType);
    }

    private Object parsePropertyValueComplex(Element propertyElement, ComplexType complexType) throws ODataException {
        Object instance;
        try {
            instance = complexType.getJavaType().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ODataUnmarshallingException("Error while instantiating instance of complex type: " +
                    complexType.getFullyQualifiedName(), e);
        }

        NodeList nodes = propertyElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                setStructProperty(instance, complexType, (Element) node);
            }
        }

        return instance;
    }

    protected String getODataNavLinkRelationNSPrefix() {
        return ODATA_NAVIGATION_LINK_REL_NS_PREFIX;
    }

    private void setEntityNavigationProperties(Object entity, EntityType entityType, Element entryElement)
            throws ODataException {
        Set<String> foundNavigationProperties = new HashSet<>();
        NodeList childNodes = entryElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element && node.getNodeName().equals(ATOM_LINK)) {
                Element linkElement = (Element) node;
                String relAttribute = (linkElement.getAttribute(REL) == null) ? "" : linkElement.getAttribute(REL);
                if (relAttribute.startsWith(getODataNavLinkRelationNSPrefix())) {
                    foundNavigationProperties.add(processNavigationLink(entity, entityType, linkElement));
                }
            }
        }

        ensureNonNullableNavigationPropertiesArePresent(foundNavigationProperties, entityType);
    }

    private String processNavigationLink(Object entity, EntityType entityType, Element linkElement)
            throws ODataException {

        String propertyName = linkElement.getAttribute(REL).substring(getODataNavLinkRelationNSPrefix().length());
        LOG.debug("Found link element for navigation property: {}", propertyName);

        StructuralProperty property = entityType.getStructuralProperty(propertyName);
        if (!(property instanceof NavigationProperty)) {
            throw new ODataUnmarshallingException("The request contains a navigation link '" + propertyName +
                    "' but the entity type '" + entityType + "' does not contain a navigation property " +
                    "with this name.");
        }

        processReferencedEntity(entity, entityType, linkElement);
        return propertyName;
    }

    private void processReferencedEntity(Object entity, EntityType entityType,
                                         Element linkElement) throws ODataException {

        String propertyName = linkElement.getAttribute(REL).substring(getODataNavLinkRelationNSPrefix().length());
        StructuralProperty property = entityType.getStructuralProperty(propertyName);

        // TODO-SL: Handle if (isDeepInsert())
        // Note: For now 'write operations' containing 'inline' feeds or entries is not supported. See:
        // http://docs.oasis-open.org/odata/odata-atom-format/v4.0/cs02/odata-atom-format-v4.0-cs02.html#_Toc372792739

        if (isWriteOperation()) {
            if (property.isCollection()) {
                Element feed = getInlineFeed(linkElement);
                if (feed != null) {
                    List<Element> elements = getFeedMetadataRefs(feed);
                    for (Element element : elements) {
                        String id = element.getAttribute(ID);
                        Object referencedEntity = getReferencedEntity(id, propertyName);
                        LOG.debug("Referenced entity: {}", referencedEntity);
                        saveReferencedEntity(entity, propertyName, property, referencedEntity);
                    }
                }
            } else {
                // Get the referenced entity, but only with the key fields filled in.
                String hrefAttr = (linkElement.getAttribute(HREF) == null) ? "" : linkElement.getAttribute(HREF);
                if (hrefAttr.isEmpty()) {
                    throw new ODataUnmarshallingException("The request contains a navigation link for the property '"
                            + propertyName + "' but the element 'href' is empty.");
                }
                Object referencedEntity = getReferencedEntity(hrefAttr, propertyName);
                LOG.debug("Referenced entity: {}", referencedEntity);
                saveReferencedEntity(entity, propertyName, property, referencedEntity);
            }
        } else {
            // Note: In the case of 'read operations' we only process a link when it is 'inline'
            Element inlineEntry = getInlineEntry(linkElement);
            if (inlineEntry != null) {
                Object linkedEntry = processEntity(inlineEntry);
                LOG.debug("Linked entry: {}", linkedEntry);
                saveReferencedEntity(entity, propertyName, property, linkedEntry);
            } else {
                Element inlineFeed = getInlineFeed(linkElement);
                if (inlineFeed != null) {
                    List<?> linkedFeed = processEntities(inlineFeed);
                    for (Object linkedEntry : linkedFeed) {
                        LOG.debug("Linked feed entry: {}", linkedEntry);
                        saveReferencedEntity(entity, propertyName, property, linkedEntry);
                    }
                }
            }
        }
    }

    protected void checkFeedMetadata(Element feedElement) throws ODataUnmarshallingException {
        List<Element> feedMetadataElements = new ArrayList<>();
        NodeList childNodes = feedElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (isFeedMetadataRef(node)) {
                //Note: decide how to process metadata:ref - references to entities for read operation
                return;
            }
            if (isFeedMetadataElement(node)) {
                feedMetadataElements.add((Element) node);
            }
        }
        if (feedMetadataElements.size() < FEED_METADATA_MIN_ITEMS) {
            throw new ODataUnmarshallingException("Feed metadata information missing. Expected metadata: '<id>', " +
                    "'<title>', '<updated>', '<link>'");
        }
        checkFeedMetadata(feedMetadataElements.get(FEED_METADATA_ATOM_ID_IDX), ATOM_ID);
        checkFeedMetadata(feedMetadataElements.get(FEED_METADATA_TITLE_IDX), TITLE);
        checkFeedMetadata(feedMetadataElements.get(FEED_METADATA_UPDATED_IDX), ATOM_UPDATED);
        checkFeedMetadata(feedMetadataElements.get(FEED_METADATA_LINK_IDX), ATOM_LINK);
    }

    protected void checkFeedMetadata(Element feedMetadataElement, String nodeLocalName)
            throws ODataUnmarshallingException {
        if (!nodeLocalName.equals(feedMetadataElement.getLocalName())) {
            throw new ODataUnmarshallingException("Wrong Feed metadata. Found: '" + feedMetadataElement.getLocalName() +
                    "'. Expected: '" + nodeLocalName + "'");
        }
    }

    private boolean isFeedMetadataElement(Node node) {
        if (node instanceof Element) {
            String nodeLocalName = node.getLocalName();
            return ATOM_ID.equals(nodeLocalName) || TITLE.equals(nodeLocalName) || ATOM_UPDATED.equals(nodeLocalName)
                    || ATOM_LINK.equals(nodeLocalName);
        }
        return false;
    }

    private boolean isFeedMetadataRef(Node node) {
        if (node instanceof Element) {
            String nodeLocalName = node.getLocalName();
            return REF.equals(nodeLocalName);
        }
        return false;
    }

    private List<Element> getFeedEntries(Element feedElement) {
        List<Element> feedEntries = new ArrayList<>();
        NodeList childNodes = feedElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element && ATOM_ENTRY.equals(node.getLocalName())) {
                feedEntries.add((Element) node);
            }
        }
        return feedEntries;
    }

    private List<Element> getFeedMetadataRefs(Element feedElement) {
        List<Element> feedEntries = new ArrayList<>();
        NodeList childNodes = feedElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element && REF.equals(node.getLocalName())) {
                feedEntries.add((Element) node);
            }
        }
        return feedEntries;
    }

    private Element getInlineEntry(Element linkElement) throws ODataUnmarshallingException {
        Element inlineElement = getInlineElement(linkElement);
        if (inlineElement != null) {
            Element entryElement = getFirstChildElement(inlineElement);
            if (entryElement != null && ATOM_ENTRY.equals(entryElement.getLocalName())) {
                return entryElement;
            }
        }
        return null;
    }

    private Element getInlineFeed(Element linkElement) throws ODataUnmarshallingException {
        Element inlineElement = getInlineElement(linkElement);
        if (inlineElement != null) {
            Element feedElement = getFirstChildElement(inlineElement);
            if (feedElement != null && ATOM_FEED.equals(feedElement.getLocalName())) {
                return feedElement;
            }
        }
        return null;
    }

    private Element getInlineElement(Element linkElement) throws ODataUnmarshallingException {
        Element inlineElement = getFirstChildElement(linkElement);
        if (inlineElement != null && INLINE.equals(inlineElement.getLocalName()) &&
                getODataMetadataNS().equals(inlineElement.getNamespaceURI())) {
            return inlineElement;
        }
        return null;
    }

    private Element getFirstChildElement(Element element) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                return (Element) node;
            }
        }
        return null;
    }

    private void ensureNonNullableCollectionArePresent(StructuredType entityType) throws ODataException {
        if (getRequest().getMethod() != ODataRequest.Method.POST) {
            return;
        }
        List<String> missingCollectionPropertyName = new ArrayList<>();

        entityType.getStructuralProperties().stream()
                .filter(property -> (property.isCollection()) &&
                        !(property instanceof NavigationProperty) && (!property.isNullable()))
                .forEach(property -> {
                    LOG.debug("Validating non-nullable collection property : {}", property.getName());
                    if (!foundCollectionProperties.contains(property.getName())) {
                        missingCollectionPropertyName.add(property.getName());
                    }
                });
        if (missingCollectionPropertyName.size() != 0) {
            StringJoiner joiner = new StringJoiner(",");
            missingCollectionPropertyName.forEach(joiner::add);
            LOG.debug("Non-nullable collections of {} are not found in the request" + missingCollectionPropertyName);
            throw new ODataUnmarshallingException("The request does not specify the non-nullable collections: '"
                    + joiner.toString() + ".");
        }

    }

    private void ensureNonNullableNavigationPropertiesArePresent(Set<String> navigationPropertyNames,
                                                                 StructuredType entityType) throws ODataException {
        if (getRequest().getMethod() != ODataRequest.Method.POST) {
            return;
        }

        List<String> missingNavigationPropertyNames = new ArrayList<>();
        entityType.getStructuralProperties().stream()
                .filter(property -> (property instanceof NavigationProperty) && (!property.isNullable()))
                .forEach(property -> {
                    LOG.debug("Validating non-nullable property : {}", property.getName());
                    if (!navigationPropertyNames.contains(property.getName())) {
                        missingNavigationPropertyNames.add(property.getName());
                    }
                });
        if (missingNavigationPropertyNames.size() != 0) {
            LOG.debug("Non-nullable navigation properties of {} are not found in the request"
                    + missingNavigationPropertyNames);
            // NOTE: We are just logging if a navigation property is not present.
            // In future, we will impose this restriction like ODataJsonParser after
            // we support 8.2 of docs.oasis-open.org/odata/odata-atom-format/v4.0/cs02/odata-atom-format-v4.0-cs02.html
        }
    }

}
