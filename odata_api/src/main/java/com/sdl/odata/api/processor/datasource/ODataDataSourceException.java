/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.processor.datasource;

import com.sdl.odata.api.ODataServerException;

import static com.sdl.odata.api.ODataErrorCode.DATA_SOURCE_ERROR;

/**
 * Exception thrown when an error occurs while accessing a data source.
 *
 */
public class ODataDataSourceException extends ODataServerException {

    public ODataDataSourceException(String message) {
        super(DATA_SOURCE_ERROR, message);
    }

    public ODataDataSourceException(String message, String target) {
        super(DATA_SOURCE_ERROR, message, target);
    }

    public ODataDataSourceException(String message, Throwable cause) {
        super(DATA_SOURCE_ERROR, message, cause);
    }

    public ODataDataSourceException(String message, String target, Throwable cause) {
        super(DATA_SOURCE_ERROR, message, target, cause);
    }
}
