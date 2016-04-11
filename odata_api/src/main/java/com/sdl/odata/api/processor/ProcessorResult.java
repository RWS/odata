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
package com.sdl.odata.api.processor;

import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Container with all the information resulted from processing a request (whether it is a query or write operation).
 * </p>
 * <p>
 * This information can be things like the 'HTTP status code', the optional data to include in the response body, or the
 * response headers or any other similar things.
 * </p>
 */
public final class ProcessorResult {

    private final ODataResponse.Status status;
    private final QueryResult data;
    private final Map<String, String> headers = new HashMap<>();
    private final ODataRequestContext requestContext;

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code.
     *
     * @param status The given status code.
     */
    public ProcessorResult(ODataResponse.Status status) {
        this.status = status;
        this.data = null;
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * and response headers.
     *
     * @param status  The given status code.
     * @param headers The response headers.
     */
    public ProcessorResult(ODataResponse.Status status, Map<String, String> headers) {
        this.status = status;
        this.data = null;
        this.headers.putAll(headers);
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * and data to include in the body.
     *
     * @param status The given status code.
     * @param data   The data to include in the body.
     */
    public ProcessorResult(ODataResponse.Status status, QueryResult data) {
        this.status = status;
        this.data = data;
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * the data to include in the body and the response headers.
     *
     * @param status  The given status code.
     * @param data    The data to include in the body.
     * @param headers The response headers.
     */
    public ProcessorResult(ODataResponse.Status status, QueryResult data, Map<String, String> headers) {
        this.status = status;
        this.data = data;
        this.headers.putAll(headers);
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * the data to include in the body and the response headers. Request context is also passed along as it's needed
     * for the final rendering of the odata entity in {@link com.sdl.odata.api.renderer.ODataRenderer} for batch
     * requests.
     *
     * @param status  The given status code.
     * @param data    The data to include in the body.
     * @param headers The response headers.
     * @param requestContext Request context used for generating the processor result. Needed for sub batch requests.
     */
    public ProcessorResult(ODataResponse.Status status, QueryResult data, Map<String, String> headers,
                           ODataRequestContext requestContext) {
        this.status = status;
        this.data = data;
        this.headers.putAll(headers);
        this.requestContext = requestContext;
    }

    /**
     * Get the HTTP status code.
     *
     * @return The HTTP status code.
     */
    public ODataResponse.Status getStatus() {
        return status;
    }

    /**
     * Get the data to include in the response body.
     *
     * @return The data to include in the response body.
     */
    public Object getData() {
        return data != null ? data.getData() : null;
    }

    /**
     * Get the {@link QueryResult} object.
     *
     * @return QueryResult object provided by query execution.
     */
    public QueryResult getQueryResult() {
        return data;
    }

    /**
     * Get the response headers.
     *
     * @return The response headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets the sub request context attached to the processor result.
     *
     * @return The respective context request.
     */
    public ODataRequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public String toString() {
        return "ProcessorResult { status=" + status + ", data=" + data + ", headers=" + headers + " }";
    }
}
