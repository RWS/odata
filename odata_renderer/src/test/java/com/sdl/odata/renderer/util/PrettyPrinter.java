/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

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
    public static String prettyPrintXml(String xml) throws TransformerException, IOException {

        Source xmlInput = new StreamSource(new StringReader(xml));
        try (StringWriter stringWriter = new StringWriter()) {
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", DEFAULT_INDENT);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);

            return xmlOutput.getWriter().toString();
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
