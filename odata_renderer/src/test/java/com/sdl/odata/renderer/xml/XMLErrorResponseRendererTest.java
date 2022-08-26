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
package com.sdl.odata.renderer.xml;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.renderer.RendererTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.HeaderNames.CONTENT_LANGUAGE;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static com.sdl.odata.renderer.AbstractRenderer.CONTENT_TYPE_HEADER;
import static com.sdl.odata.renderer.AbstractRenderer.ERROR_EXTRA_SCORE;
import static com.sdl.odata.renderer.AbstractRenderer.MAXIMUM_HEADER_SCORE;
import static com.sdl.odata.renderer.AbstractRenderer.PRIORITY_SCORE;
import static com.sdl.odata.test.util.TestUtils.createODataUri;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link XMLErrorResponseRenderer}.
 */
public class XMLErrorResponseRendererTest extends RendererTest {

    private static final String ERROR_RESPONSE_PATH = "/xml/ErrorResponse.xml";
    private XMLErrorResponseRenderer renderer;
    private ODataException exception;

    @BeforeEach
    public void setUp() throws Exception {
        exception = new ODataEdmException("EDM error");
        renderer = new XMLErrorResponseRenderer();
    }

    @Test
    public void testScoreExceptionNull() throws Exception {
        prepareRequestContextHeader(XML);
        assertEquals(0, renderer.score(context, null));
    }

    @Test
    public void testScoreNoException() throws Exception {
        prepareRequestContextHeader(XML);
        assertEquals(0, renderer.score(context, QueryResult.from("data")));
    }

    @Test
    public void testScoreXmlInHeader() throws Exception {
        prepareRequestContextHeader(XML);
        assertEquals(MAXIMUM_HEADER_SCORE + ERROR_EXTRA_SCORE,
                renderer.score(context, QueryResult.from(exception)));
    }

    @Test
    public void testScoreAtomXmlInHeader() throws Exception {
        prepareRequestContextHeader(ATOM_XML);
        assertEquals(MAXIMUM_HEADER_SCORE + ERROR_EXTRA_SCORE,
                renderer.score(context, QueryResult.from(exception)));
    }

    @Test
    public void testScoreXmlContentTypeInHeader() throws Exception {
        prepareRequestContextHeaderWithContextType(XML);
        assertEquals(CONTENT_TYPE_HEADER + ERROR_EXTRA_SCORE,
                renderer.score(context, QueryResult.from(exception)));
    }

    @Test
    public void testScoreAtomXmlContentTypeInHeader() throws Exception {
        prepareRequestContextHeaderWithContextType(ATOM_XML);
        assertEquals(CONTENT_TYPE_HEADER + ERROR_EXTRA_SCORE,
                renderer.score(context, QueryResult.from(exception)));
    }

    @Test
    public void testScoreWithoutContentType() throws Exception {
        prepareSimpleRequestContextHeader();
        assertEquals(PRIORITY_SCORE + ERROR_EXTRA_SCORE, renderer.score(context, QueryResult.from(exception)));
    }

    @Test
    public void testScoreEmptyHeader() throws Exception {
        prepareRequestContextHeader();
        assertEquals(1 + ERROR_EXTRA_SCORE, renderer.score(context, QueryResult.from(exception)));
    }

    @Test
    public void testRender() throws Exception {
        prepareRequestContextRender(GET, createODataUri(XML));
        renderer.render(context, QueryResult.from(exception), responseBuilder);
        responseBuilder.setStatus(OK);
        ODataResponse response = responseBuilder.build();
        assertResponse(response, XML, ERROR_RESPONSE_PATH);
        assertEquals("en", response.getHeader(CONTENT_LANGUAGE));
    }

    @Test
    public void testRenderException() throws UnsupportedEncodingException {
        prepareRequestContextRenderException(GET, createODataUri());
        assertThrows(ODataSystemException.class, () ->
                renderer.render(context, QueryResult.from(exception), responseBuilderMock)
        );
    }
}
