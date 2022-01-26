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
package com.sdl.odata.api;

/**
 * Exception thrown when there is something wrong with the content of a request.
 */
public class ODataBadRequestException extends ODataClientException {

    public ODataBadRequestException(String message) {
        super(ODataErrorCode.BAD_REQUEST_ERROR, message);
    }

    public ODataBadRequestException(String message, String target) {
        super(ODataErrorCode.BAD_REQUEST_ERROR, message, target);
    }

    public ODataBadRequestException(String message, Throwable cause) {
        super(ODataErrorCode.BAD_REQUEST_ERROR, message, cause);
    }

    public ODataBadRequestException(String message, String target, Throwable cause) {
        super(ODataErrorCode.BAD_REQUEST_ERROR, message, target, cause);
    }
}
