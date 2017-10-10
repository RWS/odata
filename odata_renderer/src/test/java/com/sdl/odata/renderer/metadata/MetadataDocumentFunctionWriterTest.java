/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.renderer.metadata;

import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.Parameter;
import com.sdl.odata.edm.model.FunctionImpl;
import com.sdl.odata.edm.model.ParameterImpl;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * <p>
 * Tests for MetadataDocumentFunctionWriter.
 * </p>
 */
public class MetadataDocumentFunctionWriterTest {

  protected static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
  protected ByteArrayOutputStream outputStream;
  protected XMLStreamWriter xmlWriter = null;

  protected XPath xPath = XPathFactory.newInstance().newXPath();
  protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  DocumentBuilder documentBuilder = factory.newDocumentBuilder();


  @Before
  public void setUp() throws Exception {
    outputStream = new ByteArrayOutputStream();
    xmlWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, UTF_8.name());
  }

  protected static void assertAttribute(String attributeName, String expectedAttributeValue, Node node) {
    assertEquals(expectedAttributeValue, node.getAttributes().getNamedItem(attributeName).getNodeValue());
  }

  public MetadataDocumentFunctionWriterTest() throws ParserConfigurationException {
  }

  @Test
  public void write() throws Exception {

    MetadataDocumentFunctionWriter functionWriter = new MetadataDocumentFunctionWriter(xmlWriter);

    Parameter parameterOne = new ParameterImpl.Builder().setName("parameterOne")
        .setType("String")
        .setNullable(true)
        .build();


    Parameter parameterTwo = new ParameterImpl.Builder().setName("parameterTwo")
        .setType("Integer")
        .setNullable(false)
        .build();

    Parameter parameters[] = {parameterOne, parameterTwo};

    Function function = new FunctionImpl.Builder()
        .setName("TestFunction")
        .setParameters(new HashSet<>(Arrays.asList(parameters)))
        .setReturnType("String")
        .build();

    functionWriter.write(function);

    Document metadataDocumentSnippet = documentBuilder.parse(new ByteArrayInputStream(outputStream.toByteArray()));

    NodeList functionNodes = (NodeList) xPath.compile("/Function").evaluate(metadataDocumentSnippet, XPathConstants.NODESET);
    NodeList parameterNodes = (NodeList) xPath.compile("/Function/Parameter").evaluate(metadataDocumentSnippet, XPathConstants.NODESET);
    NodeList returnNodes = (NodeList) xPath.compile("/Function/ReturnType").evaluate(metadataDocumentSnippet, XPathConstants.NODESET);

    assertEquals(1, functionNodes.getLength());
    assertEquals(1, returnNodes.getLength());
    assertEquals(2, parameterNodes.getLength());

    Node functionNode = functionNodes.item(0);
    assertAttribute("Name", function.getName(), functionNode);

    Node returnNode = returnNodes.item(0);
    assertAttribute("Type", function.getReturnType(), returnNode);

    Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();

    for (Parameter parameter : parameters) {
      parameterMap.put(parameter.getName(), parameter);
    }

    for (int i = 0; i < parameterNodes.getLength(); i++) {

      Node parameterNode = parameterNodes.item(i);
      String parameterName = parameterNode.getAttributes().getNamedItem("Name").getNodeValue();

      Parameter parameter = parameterMap.get(parameterName);

      assertNotNull(parameter);
      assertAttribute("Type", parameter.getType(), parameterNode);
      assertAttribute("Nullable", parameter.isNullable() ? "true" : "false", parameterNode);
    }
  }

}
