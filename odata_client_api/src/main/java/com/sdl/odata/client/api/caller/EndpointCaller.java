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
package com.sdl.odata.client.api.caller;

import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.client.api.exception.ODataClientException;

import java.net.URL;

/**
 * A class which can access the provided URL and returns the response as a String.
 */
public interface EndpointCaller {

    /**
     * Sets token to be used to authenticate request.
     * @param token value to set.
     */
    void setAccessToken(String token);

    /**
     * Calls OData web service using url and returns string response.
     * @param urlToCall The url to call
     * @return The response
     * @throws ODataClientException If unable to complete the get call
     */
    String callEndpoint(URL urlToCall) throws ODataClientException;

    /**
     * Post an entity to OData web service that means creating it and returns passed created one.
     * @param urlToCall The url to call
     * @param body The body to post
     * @param contentType content type
     * @param acceptType accept media type
     * @return The response
     * @throws ODataClientException If unable to complete the post operation
     */
    String doPostEntity(URL urlToCall, String body, MediaType contentType, MediaType acceptType)
            throws ODataClientException;

    /**
     * Put an entity to OData web service that means updating it and returns the passed updated one.
     * @param urlToCall The url to call
     * @param body The body to put
     * @param type media type
     * @return The response
     * @throws ODataClientException If unable to complete the put operation
     */
    String doPutEntity(URL urlToCall, String body, MediaType type) throws ODataClientException;

}
