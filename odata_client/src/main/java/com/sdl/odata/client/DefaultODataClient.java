/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.client;

import com.sdl.odata.client.api.ODataActionClientQuery;
import com.sdl.odata.client.api.ODataClient;
import com.sdl.odata.client.api.ODataClientComponentsProvider;
import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.exception.ODataNotImplementedException;
import com.sdl.odata.client.api.model.ODataIdAwareEntity;
import java.io.InputStream;
import java.util.Map;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.client.marshall.AtomEntityUnmarshaller.PRIMITIVE_CLASSES;
import static java.text.MessageFormat.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Default {@link ODataClient} implementation for calling OData service.
 * Used by client to retrieve OData entities and other data using {@link ODataClientQuery}
 * for query.
 */
public class DefaultODataClient implements ODataClient {

    private static final Logger LOG = getLogger(DefaultODataClient.class);

    private ODataClientComponentsProvider componentsProvider;
    private boolean encodeURL = true;


    @Override
    public void configure(ODataClientComponentsProvider clientComponentsProvider) {
        componentsProvider = clientComponentsProvider;
    }

    @Override
    public void encodeURL(boolean encode) {
        this.encodeURL = encode;
    }

    @Override
    public Object getEntity(Map<String, String> requestProperties, ODataClientQuery query) {
        LOG.debug("Getting entity for query {}", query);
        String oDataResponse = getODataResponse(requestProperties, query);
        try {
            return oDataResponse.isEmpty() ? null :
                    componentsProvider.getUnmarshaller().unmarshallEntity(oDataResponse, query);
        } catch (ODataClientException oce) {
            throw new ODataClientRuntimeException(
                    format("Unable unmarshall OData entity service response: \"{0}\"", oDataResponse),
                    oce);
        }
    }

    @Override
    public Object performAction(Map<String, String> properties, ODataActionClientQuery actionQuery) {
        try {
            String oDataResponse = getComponentsProvider().getEndpointCaller()
                    .doPostEntity(properties, buildURL(actionQuery), actionQuery.getActionRequestBody(),
                                  JSON, ATOM_XML);
            return oDataResponse.isEmpty() ? null :
                    (PRIMITIVE_CLASSES.contains(actionQuery.getEntityType()) ?
                            componentsProvider.getUnmarshaller().unmarshallEntity(oDataResponse, actionQuery) :
                            componentsProvider.getUnmarshaller().unmarshall(oDataResponse,
                                    new BasicODataClientQuery.Builder().withEntityType(actionQuery.getEntityType())
                                            .build()));
        } catch (ODataClientException e) {
            throw new ODataClientRuntimeException("Unable to perform action", e);
        }
    }

    /**
     * Returns Component Provider.
     *
     * @return componentProvider
     */
    public ODataClientComponentsProvider getComponentsProvider() {
        return componentsProvider;
    }

    @Override
    public List<?> getEntities(Map<String, String> requestProperties, ODataClientQuery query) {
        LOG.debug("Getting entities for query {}", query);
        String oDataResponse = getODataResponse(requestProperties, query);
        try {
            return oDataResponse.isEmpty() ? new ArrayList<>() :
                    componentsProvider.getUnmarshaller().unmarshall(oDataResponse, query);
        } catch (ODataClientException e) {
            throw new ODataClientRuntimeException(
                    format("Unable unmarshall OData entities service response: \"{0}\"", oDataResponse),
                    e);
        }
    }

    private URL buildURL(ODataClientQuery query) {
        String builtQuery = query.getQuery();
        try {
            // encoding can be an issue due to tomcat's validation
            if (encodeURL) {
                LOG.debug("Encoding input query : " + query);
                builtQuery = URLEncoder.encode(builtQuery, "UTF-8");
                LOG.debug("Encoded query : " + query);
            }

            return new URL(componentsProvider.getWebServiceUrl().toString() + "/" + builtQuery);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException(
                    format("MalformedURLException, cannot form a valid URL for endpoint "
                                    + "\"{0}\" and service query \"/{1}\"",
                            componentsProvider.getWebServiceUrl().toString(), builtQuery), e);
        }
    }

    private String getODataResponse(Map<String, String> requestProperties, ODataClientQuery query) {
        try {
            return componentsProvider.getEndpointCaller().callEndpoint(requestProperties, buildURL(query));
        } catch (ODataClientException e) {
            throw new ODataClientRuntimeException(
                    format("Unable to call OData service for \"{0}\" URL and service query \"/{1}\"",
                            componentsProvider.getWebServiceUrl().toString(), query.getQuery()), e);
        }
    }

    @Override
    public Object getMetaData(Map<String, String> requestProperties, ODataClientQuery builder) {
        throw new ODataNotImplementedException();
    }

    @Override
    public Iterable<Object> getLinks(ODataClientQuery builder) {
        throw new ODataNotImplementedException();
    }

    @Override
    public Iterable<Object> getCollections(ODataClientQuery builder) {
        throw new ODataNotImplementedException();
    }

    @Override
    public Object createEntity(Map<String, String> requestProperties, Object entity) {
        BasicODataClientQuery query = buildQueryForEntity(entity);
        String entitySetName = query.getEdmEntityName();
        try {
            // encoding can be an issue due to tomcat's validation
            URL endpointUrl = new URL(getUrlToCall(entitySetName, false, null));
            String marshalledEntity = componentsProvider.getMarshaller().marshallEntity(entity, query);
            String createdEntity = componentsProvider.getEndpointCaller()
                    .doPostEntity(requestProperties, endpointUrl, marshalledEntity, ATOM_XML, ATOM_XML);
            return componentsProvider.getUnmarshaller().unmarshallEntity(createdEntity, query);
        } catch (ODataClientException e) {
            throw formFailedRequestException(e, entitySetName);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw formFailedUrlFormingException(e, entitySetName);
        }
    }

    @Override
    public Object updateEntity(Map<String, String> requestProperties, ODataIdAwareEntity entity) {
        BasicODataClientQuery query = buildQueryForEntity(entity);
        String entitySetName = query.getEdmEntityName();
        try {
            // encoding can be an issue due to tomcat's validation
            URL endpointUrl = new URL(getUrlToCall(entitySetName, true, entity.getId()));
            String marshalledEntity = componentsProvider.getMarshaller().marshallEntity(entity, query);
            String createdEntity = componentsProvider.getEndpointCaller()
                    .doPutEntity(requestProperties, endpointUrl, marshalledEntity, ATOM_XML);
            return componentsProvider.getUnmarshaller().unmarshallEntity(createdEntity, query);
        } catch (ODataClientException e) {
            throw formFailedRequestException(e, entitySetName);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw formFailedUrlFormingException(e, entitySetName);
        }
    }

    @Override
    public void deleteEntity(Map<String, String> requestProperties, ODataIdAwareEntity entity) {
        BasicODataClientQuery query = buildQueryForEntity(entity);
        String entitySetName = query.getEdmEntityName();
        try {
            URL endpointUrl = new URL(getUrlToCall(entitySetName, true, entity.getId()));
            componentsProvider.getEndpointCaller()
                    .doDeleteEntity(requestProperties, endpointUrl);
        } catch (ODataClientException e) {
            throw formFailedRequestException(e, entitySetName);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw formFailedUrlFormingException(e, entitySetName);
        }
    }

    @Override
    public InputStream getInputStream(Map<String, String> requestProperties, URL url) throws ODataClientException {
        return componentsProvider.getEndpointCaller().getInputStream(requestProperties, url);
    }

    private String getUrlToCall(String entitySetName, boolean includeId, String id) throws
            UnsupportedEncodingException {
        return componentsProvider.getWebServiceUrl().toString() + "/" +
                URLEncoder.encode(entitySetName, "UTF-8") + (includeId ? "('" + id + "')" : "");
    }

    private BasicODataClientQuery buildQueryForEntity(Object entity) {
        return new BasicODataClientQuery.Builder()
                .withEntityType(entity.getClass())
                .build();
    }

    private ODataClientRuntimeException formFailedRequestException(Throwable e, String entitySetName) {
        return new ODataClientRuntimeException(
                format("Unable to make POST request to OData service for \"{0}\" URL and service query \"/{1}\"",
                        componentsProvider.getWebServiceUrl().toString(), entitySetName),
                e);
    }

    private ODataClientRuntimeException formFailedUrlFormingException(Throwable e, String entitySetName) {
        return new ODataClientRuntimeException(
                format("Unable to form POST URL for OData service with \"{0}\" URL and service query \"/{1}\"",
                        componentsProvider.getWebServiceUrl().toString(), entitySetName),
                e);
    }
}
