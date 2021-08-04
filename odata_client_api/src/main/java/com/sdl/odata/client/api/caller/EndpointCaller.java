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
package com.sdl.odata.client.api.caller;

import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.client.api.exception.ODataClientException;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * A class which can access the provided URL and returns the response as a String.
 */
public interface EndpointCaller {

    /**
     * Performs the call endpoint for the given url.
     *
     * @param requestProperties request properties
     * @param urlToCall url to call
     * @return response
     * @throws ODataClientException
     */
    String callEndpoint(Map<String, String> requestProperties, URL urlToCall) throws ODataClientException;

    /**
     * Get input stream with applied OData settings (proxy connection, OAuth, timeout settings).
     *
     * @param requestProperties request properties
     * @param url URL to get input stream
     * @return input stream for passed URL parameter
     */
    InputStream getInputStream(Map<String, String> requestProperties, URL url) throws ODataClientException;

    /**
     * Post an entity to OData web service that means creating it and returns passed created one.
     *
     * @param requestProperties request properties
     * @param urlToCall The url to call
     * @param body The body to post
     * @param contentType content type
     * @param acceptType accept media type
     * @return The response
     * @throws ODataClientException If unable to complete the post operation
     */
    String doPostEntity(Map<String, String> requestProperties, URL urlToCall, String body, MediaType contentType,
                        MediaType acceptType) throws ODataClientException;

    /**
     * Put an entity to OData web service that means updating it and returns the passed updated one.
     *
     * @param requestProperties request properties
     * @param urlToCall The url to call
     * @param body The body to put
     * @param type media type
     * @return The response
     * @throws ODataClientException If unable to complete the put operation
     */
    String doPutEntity(Map<String, String> requestProperties, URL urlToCall, String body,
                       MediaType type) throws ODataClientException;

    /**
     * Delete an entity from OData web service.
     *
     * @param requestProperties request properties
     * @param urlToCall The url of the entity.
     * @throws ODataClientException
     */
    void doDeleteEntity(Map<String, String> requestProperties, URL urlToCall) throws ODataClientException;
}
