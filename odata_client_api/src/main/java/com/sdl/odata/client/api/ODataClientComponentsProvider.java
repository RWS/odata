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
package com.sdl.odata.client.api;

import com.sdl.odata.client.api.caller.EndpointCaller;
import com.sdl.odata.client.api.marshall.ODataEntityMarshaller;
import com.sdl.odata.client.api.marshall.ODataEntityUnmarshaller;

import java.net.URL;

/**
 * ODataClient components provider. Provides access to some main client parts like EndpointCaller, Unmarshaller, URL.
 */
public interface ODataClientComponentsProvider {

    /**
     * Returns endpoint caller responsible for OData web service calls.
     * @return The endpoint caller
     */
    EndpointCaller getEndpointCaller();

    /**
     * Returns unmarshaller for unmarshalling OData string response into edm entity objects.
     * @return The unmarshaller
     */
    ODataEntityUnmarshaller getUnmarshaller();

    /**
     * Returns marshaller for marshalling OData edm entity objects into string representation.
     * @return The marshaller
     */
    ODataEntityMarshaller getMarshaller();

    /**
     * Returns OData web service url.
     * @return The webservice url
     */
    URL getWebServiceUrl();
}
