/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
 * Exception thrown when there are media type(s) specified by the client, but none of those media type(s) are supported
 * by the server.
 */
public class ODataUnsupportedMediaTypeException extends ODataClientException {

    public ODataUnsupportedMediaTypeException(String message) {
        super(ODataErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR, message);
    }

    public ODataUnsupportedMediaTypeException(String message, String target) {
        super(ODataErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR, message, target);
    }

    public ODataUnsupportedMediaTypeException(String message, Throwable cause) {
        super(ODataErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR, message, cause);
    }

    public ODataUnsupportedMediaTypeException(String message, String target, Throwable cause) {
        super(ODataErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR, message, target, cause);
    }
}
