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
package com.sdl.odata.api.processor;


import com.sdl.odata.api.ODataErrorCode;
import com.sdl.odata.api.ODataServerException;

/**
 * OData Processor Exception
 * Exception thrown when some process in odata processor is going wrong.
 *
 */
public class ODataProcessorException extends ODataServerException {
    public ODataProcessorException(ODataErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ODataProcessorException(ODataErrorCode errorCode, String message, String target) {
        super(errorCode, message, target);
    }

    public ODataProcessorException(ODataErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ODataProcessorException(ODataErrorCode errorCode, String message, String target, Throwable cause) {
        super(errorCode, message, target, cause);
    }
}
