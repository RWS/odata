/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.unmarshaller.AbstractLinkUnmarshaller;
import com.sdl.odata.util.XmlBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.sdl.odata.AtomConstants.ID;
import static com.sdl.odata.AtomConstants.REF;
import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * Unmarshaller for POST and PUT requests where the URI is reference (it ends in ".../$ref"). The body of such a
 * request is expected to contain an entity reference in Atom XML format. This unmarshaller returns an ODataLink object
 * containing information about the link to be created.
 * <p>
 * See OData v4 specification part 1, paragraph 11.4.6 Modifying Relationships between Entities
 * See OData v4 Atom XML format specification, chapter 13 Entity Reference
 */
@Component
public class AtomLinkUnmarshaller extends AbstractLinkUnmarshaller {
    private static final MediaType[] SUPPORTED_MEDIA_TYPES = {
            MediaType.ATOM_XML,
            MediaType.XML
    };

    @Override
    protected MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

    @Override
    protected String getToEntityId(ODataRequestContext requestContext) throws ODataUnmarshallingException {
        // The body is expected to contain a single entity reference
        // See OData Atom XML specification chapter 13

        String bodyText;
        try {
            bodyText = requestContext.getRequest().getBodyText(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ODataSystemException("UTF-8 is not supported", e);
        }

        Document document = parseXML(bodyText);
        Element rootElement = document.getDocumentElement();
        if (!rootElement.getNodeName().equals(REF)) {
            throw new ODataUnmarshallingException("A " + requestContext.getRequest().getMethod() +
                    " request to an entity reference URI must contain a single entity reference in the body," +
                    " but something else was found instead: " + rootElement.getNodeName());
        }

        String idAttr = rootElement.getAttribute(ID);
        if (isNullOrEmpty(idAttr)) {
            throw new ODataUnmarshallingException("The <metadata:ref> element in the body has no 'id' attribute," +
                    " or the attribute is empty. The element must have an 'id' attribute that refers" +
                    " to the entity to link to.");
        }

        return idAttr;
    }

    private Document parseXML(String xml) throws ODataUnmarshallingException {
        DocumentBuilderFactory factory = XmlBuilderFactory.getSecuredInstance();
        try {
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
            throw new ODataUnmarshallingException("Error while parsing XML", e);
        } catch (Exception e) {
            throw new ODataSystemException(e);
        }
    }
}
