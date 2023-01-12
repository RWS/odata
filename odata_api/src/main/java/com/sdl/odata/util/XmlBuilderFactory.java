/*
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
package com.sdl.odata.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlBuilderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(XmlBuilderFactory.class);
    private static final DocumentBuilderFactory FACTORY = create();

    private XmlBuilderFactory() {
    }

    public static DocumentBuilderFactory getSecuredInstance() {
        return FACTORY;
    }

    private static DocumentBuilderFactory create() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl",
                ClassLoader.getSystemClassLoader());
        factory.setNamespaceAware(true);
        // getting rid of XXE see:
        // see https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
        factory.setExpandEntityReferences(false);
        factory.setXIncludeAware(false);
        trySetAttribute(factory,
                XMLConstants.ACCESS_EXTERNAL_DTD,
                "file:,jar:,jar:file:");
        trySetAttribute(factory,
                XMLConstants.ACCESS_EXTERNAL_SCHEMA,
                "file:,jar:,jar:file:");
        trySetAttribute(factory,
                XMLConstants.ACCESS_EXTERNAL_STYLESHEET,
                "file:,jar:,jar:file:");
        //Xerces 1 Features:
        trySetFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        trySetFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        //Xerces 2 Features:
        trySetFeature(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
        trySetFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetFeature(factory, "http://xml.org/sax/features/external-parameter-entitiess", false);
        trySetFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        trySetFeature(factory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return factory;
    }

    static void trySetFeature(DocumentBuilderFactory factory, String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        } catch (ParserConfigurationException e) {
            logMessage(feature, "Could not set feature to factory: {}");
        }
    }

    static void trySetAttribute(DocumentBuilderFactory factory, String attribute, String value) {
        if ("all".equals(value) || value == null) {
            return;
        }
        try {
            factory.setAttribute(attribute, value);
        } catch (Exception e) {
            logMessage(attribute, "Could not set attribute to document builder factory: {}");
        }
    }

    static void logMessage(String property, String s) {
        LOG.warn(s, property);
    }
}
