/*
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
package com.sdl.odata.client.api.exception;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * Exception to be thrown, if request is not authorized.
 */
public class ODataClientNotAuthorized extends ODataClientHttpError {
    public ODataClientNotAuthorized(String message, Throwable cause) {
        super(HTTP_UNAUTHORIZED, message, cause);
    }

    public ODataClientNotAuthorized(String message) {
        super(HTTP_UNAUTHORIZED, message);
    }
}

