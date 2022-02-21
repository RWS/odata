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
package com.sdl.odata.api.processor.query.strategy;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.processor.query.QueryResult;

/**
 * Query operation strategy.
 */
public interface QueryOperationStrategy {

    /**
     * Execute the query, returning the result.
     *
     * @return The result of executing the query.
     * @throws ODataException Thrown when an error occurs so that the query cannot be executed.
     */
    QueryResult execute() throws ODataException;
}
