/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.edm;

import com.sdl.odata.api.ODataErrorCode;
import com.sdl.odata.api.ODataServerException;

/**
 * Exception thrown when an inconsistency is found in the entity data model.
 */
public class ODataEdmException extends ODataServerException {

    public ODataEdmException(String message) {
        super(ODataErrorCode.EDM_ERROR, message);
    }

    public ODataEdmException(String message, String target) {
        super(ODataErrorCode.EDM_ERROR, message, target);
    }

    public ODataEdmException(String message, Throwable cause) {
        super(ODataErrorCode.EDM_ERROR, message, cause);
    }

    public ODataEdmException(String message, String target, Throwable cause) {
        super(ODataErrorCode.EDM_ERROR, message, target, cause);
    }
}
