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
package com.sdl.odata.client.api;

import java.io.Serializable;

/**
 * The class represents the query to OData web service.
 */
public interface ODataClientQuery extends Serializable {

    /**
     * It return string representation of query.
     *
     * @return The query
     */
    String getQuery();

    /**
     * Returns a string representation of EdmEntity name.
     *
     * @return The entity name
     */
    String getEdmEntityName();

    /**
     * Returns result type of OData execution result.
     *
     * @return result type of executed result
     */
    Class<?> getEntityType();

    String getCacheKey();

    /**
     * Returns boolean value that says whether we should use streaming for current ODataClientQuery.
     *
     * @return true if we expect to receive response with chunked Transfer Encoding, otherwise - false
     */
    boolean isStreamingSupport();
}
