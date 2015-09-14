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
package com.sdl.odata.renderer.xml;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.renderer.AbstractRenderer;
import com.sdl.odata.renderer.RendererTest;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static com.sdl.odata.test.util.TestUtils.createODataUriForServiceDocument;

/**
 * Unit tests for {@link XMLServiceDocumentRenderer}.
 */
public class XMLServiceDocumentRendererTest extends RendererTest {

    private static final String SERVICE_DOCUMENT = "/xml/ServiceDocument.xml";
    private XMLServiceDocumentRenderer serviceDocumentRenderer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        serviceDocumentRenderer = new XMLServiceDocumentRenderer();
    }

    @Test
    public void testScoreGetMethodWithHeader() throws Exception {
        prepareRequestContextHeader(XML);
        assertScore(serviceDocumentRenderer, AbstractRenderer.MAXIMUM_HEADER_SCORE);
    }

    @Test
    public void testScoreGetMethodWithFormat() throws Exception {
        prepareRequestContextFormat(XML);
        assertScore(serviceDocumentRenderer, AbstractRenderer.MAXIMUM_FORMAT_SCORE);

    }

    @Test
    public void testScoreGetWithJSONHeaderAndXMLFormat() throws Exception {
        prepareRequestContextHeaderFormat(JSON, XML);
        assertScore(serviceDocumentRenderer, AbstractRenderer.MAXIMUM_FORMAT_SCORE);
    }

    @Test
    public void testScoreGetWithXMLHeaderAndJSONFormat() throws Exception {
        prepareRequestContextHeaderFormat(XML, JSON);
        assertScore(serviceDocumentRenderer, AbstractRenderer.MAXIMUM_HEADER_SCORE);
    }

    @Test
    public void testScoreOfNoneGetMethod() throws Exception {
        prepareRequestContextPostMethod();
        assertScore(serviceDocumentRenderer, AbstractRenderer.DEFAULT_SCORE + 1);
    }

    @Test
    public void testScoreOfNoneServiceDocument() throws Exception {
        prepareRequestContextMetaDataUri();
        assertScore(serviceDocumentRenderer, AbstractRenderer.DEFAULT_SCORE);
    }

    @Test
    public void testRender() throws Exception {
        prepareRequestContextRender(POST, createODataUriForServiceDocument());
        serviceDocumentRenderer.render(context, null, responseBuilder);
        responseBuilder.setStatus(OK);
        assertResponse(responseBuilder.build(), XML, SERVICE_DOCUMENT);
    }

    @Test(expected = ODataSystemException.class)
    public void testRenderException() throws ODataException, UnsupportedEncodingException {
        prepareRequestContextRenderException(GET, createODataUriForServiceDocument(XML));
        serviceDocumentRenderer.render(context, null, responseBuilderMock);
    }
}

