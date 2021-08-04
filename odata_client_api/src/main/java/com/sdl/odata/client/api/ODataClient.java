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
package com.sdl.odata.client.api;

import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.model.ODataIdAwareEntity;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * It represents a generic OData client able to handle requests on any entities, no matter on what type this entity has.
 * </p>
 * <p>
 * At the first step, it unmarshalls the response back from the OData service to the specific entity
 * Second, it is able to load the configuration data to the in-memory model
 * </p>
 */
public interface ODataClient {

    /**
     * Configure the 'OData Client' by using the given 'OData Components Provider'.
     *
     * @param clientComponentsProvider The given 'OData Components Provider'.
     */
    void configure(ODataClientComponentsProvider clientComponentsProvider);

    /**
     * Enables/Disables URL encoding for API calls. URLEncoding to be enabled by default.
     *
     * @param encode The given 'OData Components Provider'.
     */
    void encodeURL(boolean encode);

    /**
     * Gets the specific entity.
     *
     *
     * @param requestProperties request related properties
     * @param query ODataClientQuery
     * @return target entity
     */
    Object getEntity(Map<String, String> requestProperties, ODataClientQuery query);

    /**
     * Gets all possible entities of a specific entity.
     * <p>
     * {@code client.getEntities(TargetEntity.class)}
     *
     *
     * @param requestProperties request related properties
     * @param query ODataClientQuery
     * @return collection of entities
     */
    List<?> getEntities(Map<String, String> requestProperties, ODataClientQuery query);

    /**
     * Performs an action execution and retrieves the result back.
     *
     * @param requestProperties request related properties
     * @param query ODataClientQuery
     * @return  executed action result
     */
    Object performAction(Map<String, String> requestProperties, ODataActionClientQuery query);

    /**
     * Gets the metadata.
     *
     * @param requestProperties request related properties
     * @param builder ODataClientQuery builder
     * @return target metadata
     */
    Object getMetaData(Map<String, String> requestProperties, ODataClientQuery builder);

    /**
     * Get all links for the given entity.
     *
     * @param builder ODataClientQuery builder
     * @return collection of links
     */
    Iterable<Object> getLinks(ODataClientQuery builder);

    /**
     * Get all collections for the given entity.
     *
     * @param builder ODataClientQuery builder
     * @return all possible collections
     */
    Iterable<Object> getCollections(ODataClientQuery builder);

    /**
     * Create an entity using POST request to a service.
     *
     *
     * @param requestProperties request related properties
     * @param entity    entity to save
     * @return created entity
     */
    Object createEntity(Map<String, String> requestProperties, Object entity);

    /**
     * Update an existing entity using PUT request to a service.
     *
     * @param requestProperties request related properties
     * @param entity    entity to update
     * @return updated entity
     */
    Object updateEntity(Map<String, String> requestProperties, ODataIdAwareEntity entity);

    /**
     * Delete an existing entity using DELETE request to a service.
     *
     * @param entity entity to delete
     */
    void deleteEntity(Map<String, String> requestProperties, ODataIdAwareEntity entity);

    /**
     * Get input stream with applied OData settings (proxy connection, OAuth, timeout settings).
     * See {@link com.sdl.odata.client.api.caller.EndpointCaller#getInputStream(Map, URL)}.
     * @param url URL to get input stream
     * @return input stream for passed URL parameter
     */
    InputStream getInputStream(Map<String, String> requestProperties, URL url) throws ODataClientException;
}
