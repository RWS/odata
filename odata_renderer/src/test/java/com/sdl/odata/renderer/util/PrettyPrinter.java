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
package com.sdl.odata.renderer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static javax.xml.transform.OutputKeys.ENCODING;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static javax.xml.transform.OutputKeys.VERSION;

/**
 * Class capable of pretty-printing different resources in different formats.
 */
public final class PrettyPrinter {

    private PrettyPrinter() {
    }

    private static final int DEFAULT_INDENT = 4;

    /**
     * Pretty-print a given XML.
     *
     * @param xml The not-formatted XML.
     * @return The pretty-printed XML.
     */
    public static String prettyPrintXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        // Ignores all the comments described in the XML File
        factory.setIgnoringComments(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream xmlInput = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(xmlInput);

        try (StringWriter stringWriter = new StringWriter()) {
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", DEFAULT_INDENT);
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(
                    new File(PrettyPrinter.class.getResource("/xslt/stylesheet.xslt").getFile())));


            transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(ENCODING, StandardCharsets.UTF_8.name());
            transformer.setOutputProperty(VERSION, "1.0");
            transformer.setOutputProperty(INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            transformer.transform(source, xmlOutput);

            String result = xmlOutput.getWriter().toString();
            return result.replaceAll("(\n|\r)+", "\n");
        }
    }

    /**
     * Pretty-print a given Json.
     *
     * @param json The not-formatted Json.
     * @return The pretty-printed Json
     * @throws IOException
     */
    public static String prettyPrintJson(String json) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(json, Object.class);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
}
