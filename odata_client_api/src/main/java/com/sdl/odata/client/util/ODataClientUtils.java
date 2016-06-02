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
package com.sdl.odata.client.util;

import com.sdl.odata.api.service.HeaderNames;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientHttpError;
import com.sdl.odata.client.api.exception.ODataClientNotAuthorized;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.exception.ODataClientTimeout;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * OData client utils class.
 */
public final class ODataClientUtils {

    private ODataClientUtils() {
    }

    /**
     * Close closable objects if it is necessary. Mostly used to close connections.
     *
     * @param closeable closable object
     * @throws ODataClientException
     */
    public static void closeIfNecessary(Closeable closeable) throws ODataClientException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new ODataClientException("Could not close '" + closeable.getClass().getSimpleName() + "'", e);
            }
        }
    }

    /**
     * Util method for first populating request properties before execution.
     *
     * @param requestProperties request properties, may be immutable.
     *                          It can be null or empty, so copying will take place, if necessary.
     * @param bodyLength        body length
     * @param contentType       content type
     * @param acceptType        access type
     * @return populated request properties
     */
    public static Map<String, String> populateRequestProperties(
            Map<String, String> requestProperties, int bodyLength, MediaType contentType, MediaType acceptType) {
        Map<String, String> properties;
        // requestProperties
        if (requestProperties == null || requestProperties.isEmpty()) {
            properties = new HashMap<>();
        } else {
            properties = new HashMap<>(requestProperties);
        }
        if (acceptType != null) {
            properties.put(HeaderNames.ACCEPT, acceptType.toString());
        }
        if (contentType != null) {
            properties.put(HeaderNames.CONTENT_TYPE, contentType.toString());
        }
        if (bodyLength > -1) {
            properties.put(HeaderNames.CONTENT_LENGTH, String.valueOf(bodyLength));
        }
        return properties;
    }

    public static ODataClientRuntimeException buildException(String errorMessage, int responseCode) {
        if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
            return new ODataClientTimeout(errorMessage);
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return new ODataClientNotAuthorized(errorMessage);
        } else if (responseCode > 0) {
            return new ODataClientHttpError(responseCode, errorMessage);
        }
        return new ODataClientRuntimeException(errorMessage);
    }
}
