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

import com.sdl.odata.api.ODataClientException;
import com.sdl.odata.api.ODataErrorCode;

/**
 * Exception thrown when trying to insert a new entity when an entity with the same key already exists.
 */
public class ODataDuplicateKeyException extends ODataClientException {

    public ODataDuplicateKeyException(String message) {
        super(ODataErrorCode.DUPLICATE_KEY_ERROR, message);
    }

    public ODataDuplicateKeyException(String message, String target) {
        super(ODataErrorCode.DUPLICATE_KEY_ERROR, message, target);
    }

    public ODataDuplicateKeyException(String message, Throwable cause) {
        super(ODataErrorCode.DUPLICATE_KEY_ERROR, message, cause);
    }

    public ODataDuplicateKeyException(String message, String target, Throwable cause) {
        super(ODataErrorCode.DUPLICATE_KEY_ERROR, message, target, cause);
    }
}
