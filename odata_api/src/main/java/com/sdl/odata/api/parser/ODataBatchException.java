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
package com.sdl.odata.api.parser;

import com.sdl.odata.api.ODataClientException;
import com.sdl.odata.api.ODataErrorCode;

/**
 * General parent for Batch Exceptions.
 * Exists for general catching batch errors if needed.
 */
public class ODataBatchException extends ODataClientException {

    public ODataBatchException(ODataErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ODataBatchException(ODataErrorCode errorCode, String message, String target) {
        super(errorCode, message, target);
    }

    public ODataBatchException(ODataErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ODataBatchException(ODataErrorCode errorCode, String message, String target, Throwable cause) {
        super(errorCode, message, target, cause);
    }


}
