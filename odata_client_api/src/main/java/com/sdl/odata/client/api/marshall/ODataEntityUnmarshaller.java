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
package com.sdl.odata.client.api.marshall;

import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;

import java.util.List;

/**
 * A class which can unmarshall an Odata Service Response back to it's original Entity object.
 */
public interface ODataEntityUnmarshaller {

    /**
     * Unmarshalls a response (which contains a single entity) back into an OData entity.
     * @param odataServiceResponseEntry a response which contains a single entry
     * @return the OData entity
     * @throws ODataClientException a client exception
     */
   Object unmarshallEntity(String odataServiceResponseEntry, ODataClientQuery query) throws ODataClientException;

    /**
     * Unmarshalls a response (which contains one or more entities) into a list of OData entities.
     * @param odataServiceResponse a response which contains one or more Odata entities
     * @return the OData entities
     * @throws ODataClientException a client exception
     */
   List<?> unmarshall(String odataServiceResponse, ODataClientQuery query) throws ODataClientException;
}
