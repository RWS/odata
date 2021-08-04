/**
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
package com.sdl.odata.renderer;

import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.renderer.ODataRenderer;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import org.junit.Before;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintJson;
import static com.sdl.odata.renderer.util.PrettyPrinter.prettyPrintXml;
import static com.sdl.odata.test.util.TestUtils.createODataRequest;
import static com.sdl.odata.test.util.TestUtils.createODataRequestContext;
import static com.sdl.odata.test.util.TestUtils.createODataRequestWithContentType;
import static com.sdl.odata.test.util.TestUtils.createODataUri;
import static com.sdl.odata.test.util.TestUtils.createODataUriForMetaData;
import static com.sdl.odata.test.util.TestUtils.createODataUriForServiceDocument;
import static com.sdl.odata.test.util.TestUtils.createSimpleODataRequest;
import static com.sdl.odata.test.util.TestUtils.getEdmEntityClasses;
import static com.sdl.odata.test.util.TestUtils.readContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class with common functionality to use in renderer tests.
 */
public abstract class RendererTest {

    protected ODataRequestContext context;
    protected ODataResponse.Builder responseBuilder;
    protected ODataResponse.Builder responseBuilderMock;
    protected EntityDataModel entityDataModel;

    @Before
    protected void setUp() throws Exception {
        entityDataModel = new AnnotationEntityDataModelFactory()
                .addClasses(getEdmEntityClasses()).buildEntityDataModel();
    }

    /**
     * Prepare the request context by specifying the media type for the accept-header.
     *
     * @param headerMediaType The media type for the accept-header.
     * @throws Exception
     */
    protected void prepareRequestContextHeader(MediaType... headerMediaType) throws Exception {
        context = createODataRequestContext(createODataRequest(GET, headerMediaType),
                createODataUriForServiceDocument(), entityDataModel);
    }

    protected void prepareRequestContextHeaderWithContextType(MediaType headerMediaType) throws Exception {
        context = createODataRequestContext(createODataRequestWithContentType(GET, headerMediaType),
                createODataUriForServiceDocument(), entityDataModel);
    }

    protected void prepareSimpleRequestContextHeader() throws Exception {
        context = createODataRequestContext(createSimpleODataRequest(GET),
                createODataUriForServiceDocument(), entityDataModel);
    }

    /**
     * Prepare the request context by specifying the media type for the $format query parameter.
     *
     * @param formatMediaType The media type for the $format query parameter.
     * @throws Exception
     */
    protected void prepareRequestContextFormat(MediaType formatMediaType) throws Exception {
        context = createODataRequestContext(createODataRequest(GET), createODataUri(formatMediaType), entityDataModel);
    }

    /**
     * Prepare the request context by specifying the media type for the accept-header and $format query parameter.
     *
     * @param headerMediaType The media type for the accept-header.
     * @param formatMediaType The media type for the $format query parameter.
     * @throws Exception
     */
    protected void prepareRequestContextHeaderFormat(MediaType headerMediaType, MediaType formatMediaType)
            throws Exception {
        context = createODataRequestContext(createODataRequest(GET, headerMediaType),
                createODataUri(formatMediaType), entityDataModel);
    }

    /**
     * Prepare the request context for a POST method.
     *
     * @throws Exception
     */
    protected void prepareRequestContextPostMethod() throws Exception {
        context = createODataRequestContext(POST, entityDataModel);
    }

    /**
     * Prepare the request context for a $metadata document request.
     *
     * @throws Exception
     */
    protected void prepareRequestContextMetaDataUri() throws Exception {
        context = createODataRequestContext(GET, createODataUriForMetaData(), entityDataModel);
    }

    /**
     * Invoke the score method and assert that the resulted score is equal to the given expected score.
     *
     * @param renderer      The OData Renderer to invoke the score method in.
     * @param expectedScore The expected resulting score.
     */
    protected void assertScore(ODataRenderer renderer, int expectedScore) {
        assertThat(renderer.score(context, null), is(expectedScore));
    }

    /**
     * Assert that a given valid response is of the given media type and contains the body specified in the given file.
     *
     * @param response  The given valid response.
     * @param mediaType The given expected media type.
     * @param body      The given file containing the expected response body.
     * @throws java.io.IOException
     * @throws javax.xml.transform.TransformerException
     */
    protected void assertResponse(ODataResponse response, MediaType mediaType, String body)
            throws IOException, TransformerException {

        assertThat(response.getContentType(), is(mediaType));
        String bodyText = response.getBodyText(UTF_8.name());
        assertThat(bodyText, is(not(nullValue())));
        if (mediaType.equals(XML)) {
            assertThat(prettyPrintXml(bodyText), is(prettyPrintXml(readContent(body))));
        } else if (mediaType.equals(JSON)) {
            assertThat(prettyPrintJson(bodyText), is(prettyPrintJson(readContent(body))));
        }
    }

    /**
     * Prepare the request context for a given HTTP method and OData URI to perform a render operation.
     *
     * @param method   The given HTTP method.
     * @param oDataUri The given OData URI.
     * @throws java.io.UnsupportedEncodingException
     */
    protected void prepareRequestContextRender(ODataRequest.Method method, ODataUri oDataUri)
            throws UnsupportedEncodingException {

        context = createODataRequestContext(method, oDataUri, entityDataModel);
        responseBuilder = new ODataResponse.Builder();
    }

    /**
     * Prepare the request context for a render operation expecting an exception.
     *
     * @param method   The given HTTP method.
     * @param oDataUri The given OData URI.
     * @throws java.io.UnsupportedEncodingException
     */
    protected void prepareRequestContextRenderException(ODataRequest.Method method, ODataUri oDataUri)
            throws UnsupportedEncodingException {

        context = createODataRequestContext(method, oDataUri, entityDataModel);
        responseBuilderMock = mock(ODataResponse.Builder.class);
        when(responseBuilderMock.setBodyText(
                any(String.class), any(String.class))).thenThrow(new UnsupportedEncodingException());
        when(responseBuilderMock.setStatus(any(ODataResponse.Status.class))).thenReturn(responseBuilderMock);
        when(responseBuilderMock.setContentType(any(MediaType.class))).thenReturn(responseBuilderMock);
        when(responseBuilderMock.setHeader(any(String.class), any(String.class))).thenReturn(responseBuilderMock);
    }
}
