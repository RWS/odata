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
package com.sdl.odata.api;

/**
 * Superclass for OData-related checked exceptions.
 */
public abstract class ODataException extends Exception {

    private final ODataErrorCode errorCode;
    private final String target;

    protected ODataException(ODataErrorCode errorCode, String message) {
        this(errorCode, message, (String) null);
    }

    protected ODataException(ODataErrorCode errorCode, String message, String target) {
        super(message);
        this.errorCode = errorCode;
        this.target = target;
    }

    protected ODataException(ODataErrorCode errorCode, String message, Throwable cause) {
        this(errorCode, message, null, cause);
    }

    protected ODataException(ODataErrorCode errorCode, String message, String target, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.target = target;
    }

    public ODataErrorCode getCode() {
        return errorCode;
    }

    public String getTarget() {
        return target;
    }
}
