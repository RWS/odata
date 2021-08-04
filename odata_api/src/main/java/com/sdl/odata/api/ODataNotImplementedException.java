/**
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
 * Exception thrown when an error occurs because something is not implemented.
 */
public class ODataNotImplementedException extends ODataServerException {

    public ODataNotImplementedException(String message) {
        super(ODataErrorCode.NOT_IMPLEMENTED_ERROR, message);
    }

    public ODataNotImplementedException(String message, String target) {
        super(ODataErrorCode.NOT_IMPLEMENTED_ERROR, message, target);
    }

    public ODataNotImplementedException(String message, Throwable cause) {
        super(ODataErrorCode.NOT_IMPLEMENTED_ERROR, message, cause);
    }

    public ODataNotImplementedException(String message, String target, Throwable cause) {
        super(ODataErrorCode.NOT_IMPLEMENTED_ERROR, message, target, cause);
    }
}
