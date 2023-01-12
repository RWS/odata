/*
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
 * This Exception will be thrown when trying to delete entity which is not exist.
 */
public class ODataEntityNotFoundException extends ODataClientException {

    public ODataEntityNotFoundException(String message) {
        super(ODataErrorCode.ENTITY_NOT_FOUND_ERROR, message);
    }

    public ODataEntityNotFoundException(String message, String target) {
        super(ODataErrorCode.ENTITY_NOT_FOUND_ERROR, message, target);
    }

    public ODataEntityNotFoundException(String message, Throwable cause) {
        super(ODataErrorCode.ENTITY_NOT_FOUND_ERROR, message, cause);
    }

    public ODataEntityNotFoundException(String message, String target, Throwable cause) {
        super(ODataErrorCode.ENTITY_NOT_FOUND_ERROR, message, target, cause);
    }
}
