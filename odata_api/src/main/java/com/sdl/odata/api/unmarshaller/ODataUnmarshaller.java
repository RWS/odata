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
package com.sdl.odata.api.unmarshaller;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.service.ODataRequestContext;

/**
 * OData unmarshaller. The unmarshaller validates and converts an entity from a format such as Atom XML or JSON in
 * the request to an entity object.
 */
public interface ODataUnmarshaller {

    /**
     * Returns a score that indicates how suitable this unmarshaller is for unmarshalling the entities in the request.
     * The score should be a number between 1 and 100; the higher the score, the more suitable this unmarshaller is
     * for unmarshalling the entities in the request. A return value of 0 means that this unmarshaller cannot be used
     * for this request.
     *
     * @param requestContext The request context.
     * @return A score that indicates how suitable this unmarshaller is for unmarshalling the entities in this request;
     *      0 if this marshaller cannot unmarshall this request.
     */
    int score(ODataRequestContext requestContext);

    /**
     * Unmarshalls and validates the entities in the request. Validating means that the fields of each entity are
     * checked against the metadata model (for example, the data types must be correct, and required fields must
     * not be empty).
     *
     * @param requestContext The request context.
     * @return The unmarshalled entities.
     * @throws ODataException If an error occurs while unmarshalling the entities.
     */
    Object unmarshall(ODataRequestContext requestContext) throws ODataException;
}
