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
package com.sdl.odata.api.renderer;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;

import java.io.OutputStream;

/**
 * OData renderer. A renderer converts data that is the result of a query or another operation into a response body
 * in the appropriate format.
 */
public interface ODataRenderer {

    /**
     * Returns a score that indicates how suitable this renderer is for rendering the response body for the specified
     * request. The score should be a number between 0 and 100 that indicates how suitable this renderer is for the
     * specified request and data. A return value of 0 means that this renderer cannot be used this request.
     *
     * @param requestContext The request context.
     * @param data           The data to render.
     * @return A score that indicates how suitable this renderer is for the specified request and data;
     * 0 if this renderer cannot render the response body for this request.
     */
    int score(ODataRequestContext requestContext, QueryResult data);

    /**
     * Renders the response body for a request.
     *
     * @param requestContext  The request context.
     * @param data            The data to render.
     * @param responseBuilder The response builder to which the appropriate status code, headers and body are added.
     * @throws ODataException If an error occurs while rendering.
     */
    void render(ODataRequestContext requestContext, QueryResult data, ODataResponse.Builder responseBuilder)
            throws ODataException;

    /**
     * Renders the response start content including OData specification metadata.
     * Used for streaming requests.
     * First render start and write to response, then sequentially proceed with renderBody and finish with renderEnd.
     *
     * @param requestContext The request context.
     * @param result         The data to render.
     * @param outputStream   OutputStream to write data in.
     * @return Response start content including OData specification metadata
     * @throws ODataException If an error occurs while rendering.
     */
    ChunkedActionRenderResult renderStart(ODataRequestContext requestContext, QueryResult result,
                                          OutputStream outputStream) throws ODataException;

    /**
     * Renders the response body.
     * Used for streaming requests.
     * This method should be called sequentially as new data comes through stream.
     *
     * @param requestContext The request context.
     * @param result         The data to render.
     * @param previousResult Previous result needed for body rendering.
     * @throws ODataException If an error occurs while rendering.
     */
    ChunkedActionRenderResult renderBody(ODataRequestContext requestContext, QueryResult result,
                                         ChunkedActionRenderResult previousResult) throws ODataException;

    /**
     * Renders the response end content tags.
     * Used for streaming requests.
     * Finish writing to response with this method.
     *
     * @param requestContext The request context.
     * @param result         The data to render.
     * @throws ODataException If an error occurs while rendering.
     */
    void renderEnd(ODataRequestContext requestContext, QueryResult result,
                   ChunkedActionRenderResult previousResult) throws ODataException;
}
