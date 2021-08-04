/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * Container with all the information resulted from processing a request
 * (whether it is a query or write operation).
 * </p>
 * <p>
 * This information can be things like the 'HTTP status code',
 * the optional query result object to include in the response body, or the response headers
 * or any other similar things.
 * </p>
 */
public final class ProcessorResult {

    private final ODataResponse.Status status;
    private final QueryResult queryResult;
    private final Map<String, String> headers = new HashMap<>();
    private final ODataRequestContext requestContext;

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code.
     *
     * @param status The given status code.
     */
    public ProcessorResult(ODataResponse.Status status) {
        this.status = status;
        this.queryResult = null;
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
        this.queryResult = null;
        this.headers.putAll(headers);
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * and query result to include in the body.
     *
     * @param status      The given status code.
     * @param queryResult The query result to include in the body.
     */
    public ProcessorResult(ODataResponse.Status status, QueryResult queryResult) {
        this.status = status;
        this.queryResult = queryResult;
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * the query result to include in the body and the response headers.
     *
     * @param status      The given status code.
     * @param queryResult The query result to include in the body.
     * @param headers     The response headers.
     */
    public ProcessorResult(ODataResponse.Status status, QueryResult queryResult, Map<String, String> headers) {
        this.status = status;
        this.queryResult = queryResult;
        this.headers.putAll(headers);
        this.requestContext = null;
    }

    /**
     * Create an instance of {@link ProcessorResult} with the given HTTP status code
     * the query result to include in the body and the response headers.
     * Request context is also passed along as it's needed for the final rendering of the odata entity
     * in {@link com.sdl.odata.api.renderer.ODataRenderer} for batch requests.
     *
     * @param status         The given status code.
     * @param queryResult    The query result to include in the body.
     * @param headers        The response headers.
     * @param requestContext Request context used for generating the processor result. Needed for sub batch requests.
     */
    public ProcessorResult(ODataResponse.Status status, QueryResult queryResult, Map<String, String> headers,
                           ODataRequestContext requestContext) {
        this.status = status;
        this.queryResult = queryResult;
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
     * Get the query result object.
     *
     * @return The result object provided by query execution.
     */
    public QueryResult getQueryResult() {
        return queryResult;
    }

    /**
     * Get the query result data to include in the response body,
     * ingoring additional metadata that query might have returned.
     *
     * @return The query result data to include in the response body.
     */
    public Object getData() {
        return queryResult != null ? queryResult.getData() : null;
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
        return "ProcessorResult { status=" + status + ", queryResult=" + queryResult + ", headers=" + headers + " }";
    }
}
