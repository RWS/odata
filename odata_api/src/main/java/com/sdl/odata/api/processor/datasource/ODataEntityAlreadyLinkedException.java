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
package com.sdl.odata.api.processor.datasource;

import com.sdl.odata.api.ODataClientException;
import com.sdl.odata.api.ODataErrorCode;

/**
 * This Exception will be thrown when trying to insert a new entity bound to another entity when the later already has
 * a linked entity in the case of a single property linking them.
 */
public class ODataEntityAlreadyLinkedException extends ODataClientException {

    public ODataEntityAlreadyLinkedException(String message) {
        super(ODataErrorCode.ENTITY_ALREADY_LINKED_ERROR, message);
    }

    public ODataEntityAlreadyLinkedException(String message, String target) {
        super(ODataErrorCode.ENTITY_ALREADY_LINKED_ERROR, message, target);
    }

    public ODataEntityAlreadyLinkedException(String message, Throwable cause) {
        super(ODataErrorCode.ENTITY_ALREADY_LINKED_ERROR, message, cause);
    }

    public ODataEntityAlreadyLinkedException(String message, String target, Throwable cause) {
        super(ODataErrorCode.ENTITY_ALREADY_LINKED_ERROR, message, target, cause);
    }
}
