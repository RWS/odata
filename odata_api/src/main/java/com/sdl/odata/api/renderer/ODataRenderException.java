/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.api.renderer;

import com.sdl.odata.api.ODataErrorCode;
import com.sdl.odata.api.ODataServerException;

/**
 * Exception thrown when an error occurs during rendering.
 */
public class ODataRenderException extends ODataServerException {

    public ODataRenderException(String message) {
        super(ODataErrorCode.RENDERER_ERROR, message);
    }

    public ODataRenderException(String message, String target) {
        super(ODataErrorCode.RENDERER_ERROR, message, target);
    }

    public ODataRenderException(String message, Throwable cause) {
        super(ODataErrorCode.RENDERER_ERROR, message, cause);
    }

    public ODataRenderException(String message, String target, Throwable cause) {
        super(ODataErrorCode.RENDERER_ERROR, message, target, cause);
    }
}
