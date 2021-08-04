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
package com.sdl.odata.client.api.marshall;

import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;

import java.util.List;

/**
 * Marshaller that marshalls OData entities into String representation.
 * We need this marshaller in OData client to convert edm entity object into string representation so we can make
 * a POST request with a body containing marshalled entity. For example to create or update entities.
 */
public interface ODataEntityMarshaller {

    /**
     * Marshall OData entity into String.
     *
     * @param oDataEntity OData entity to marshall
     * @param query       query provides part of web service URL
     * @return string representation of OData entity
     * @throws ODataClientException
     */
    String marshallEntity(Object oDataEntity, ODataClientQuery query) throws ODataClientException;

    /**
     * Marshall OData entities into String.
     *
     * @param oDataEntities OData entities to marshall
     * @param query         query provides part of web service URL
     * @return string representation of OData entities
     * @throws ODataClientException
     */
    String marshallEntities(List<?> oDataEntities, ODataClientQuery query) throws ODataClientException;
}
