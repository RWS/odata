/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.client.caller;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.github.kristofa.brave.httpclient.BraveHttpRequestInterceptor;
import com.github.kristofa.brave.httpclient.BraveHttpResponseInterceptor;
import com.sdl.odata.api.service.HeaderNames;
import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.client.api.caller.EndpointCaller;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.XML;
import static com.sdl.odata.client.ODataClientConstants.DefaultValues.CLIENT_PROXY_PORT_DEFAULT;
import static com.sdl.odata.client.ODataClientConstants.DefaultValues.CLIENT_TIMEOUT_DEFAULT;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_CONNECTION_TIMEOUT;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_HOST_NAME;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_PORT;
import static com.sdl.odata.client.property.PropertyUtils.getIntegerProperty;
import static com.sdl.odata.client.property.PropertyUtils.getStringProperty;
import static com.sdl.odata.client.util.ODataClientUtils.buildException;
import static com.sdl.odata.client.util.ODataClientUtils.closeIfNecessary;
import static com.sdl.odata.client.util.ODataClientUtils.populateRequestProperties;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.apache.http.util.TextUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Tracing implementation of {@link com.sdl.odata.client.api.caller.EndpointCaller}.
 * Using brave to generate and send spans into zipkin.
 */
public class TracingEndpointCaller implements EndpointCaller {

    private static final Logger LOG = getLogger(TracingEndpointCaller.class);

    private static final String WRONG_URL_MESSAGE = "The URL syntax is wrong";
    private static final String REQUEST_FAILED_MESSAGE = "Cannot make a request to URL: ";
    private static final String APPLICATION_PROPERTIES_FILE_NAME = "/config/application.properties";
    private static final String DEFAULT_ZIPKIN_HOSTNAME = "http://localhost:9411";

    private CloseableHttpClient closeableHttpClient;

    public TracingEndpointCaller(Properties properties) {
        Integer timeout = getIntegerProperty(properties, CLIENT_CONNECTION_TIMEOUT, CLIENT_TIMEOUT_DEFAULT);

        String proxyServerHostName = getStringProperty(properties, CLIENT_SERVICE_PROXY_HOST_NAME);
        Integer proxyPort = getIntegerProperty(properties, CLIENT_SERVICE_PROXY_PORT);
        Integer proxyServerPort = proxyPort == null ? CLIENT_PROXY_PORT_DEFAULT : proxyPort;

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(config);
        if (!isBlank(proxyServerHostName) && proxyServerPort > 0) {
            httpClientBuilder.setProxy(new HttpHost(proxyServerHostName, proxyServerPort));
        }

        // load application.properties to know zipkin host, service name and how often to collect spans
        Properties applicationProperties = new Properties();
        try (InputStream stream = this.getClass().getResourceAsStream(APPLICATION_PROPERTIES_FILE_NAME)) {
            if (stream != null) {
                applicationProperties.load(stream);
            }
        } catch (IOException e) {
            LOG.warn("'{}' file is not available in the classpath", APPLICATION_PROPERTIES_FILE_NAME);
        }
        Brave brave = new Brave.Builder(applicationProperties.getProperty("spring.application.name", "cil-call"))
                .traceSampler(Sampler.create(
                        Float.valueOf(applicationProperties.getProperty("spring.sleuth.sampler.percentage", "1.0"))))
                .spanCollector(HttpSpanCollector.create(
                        applicationProperties.getProperty("spring.zipkin.baseUrl", DEFAULT_ZIPKIN_HOSTNAME),
                        new EmptySpanCollectorMetricsHandler()))
                .build();

        closeableHttpClient = httpClientBuilder
                .addInterceptorFirst(new BraveHttpRequestInterceptor(brave.clientRequestInterceptor(),
                        new DefaultSpanNameProvider()))
                .addInterceptorFirst(new BraveHttpResponseInterceptor(brave.clientResponseInterceptor()))
                .build();
    }

    @Override
    public String callEndpoint(Map<String, String> requestProperties, URL url) throws ODataClientException {
        LOG.debug("Preparing the call endpoint for given url: {}", url);

        CloseableHttpResponse closeableResponse = null;
        try {
            RequestBuilder requestBuilder = RequestBuilder.get()
                    .setUri(url.toURI());
            getRequestHeaders(requestProperties, -1, null, XML).entrySet().stream()
                    .forEach(entry -> requestBuilder.addHeader(entry.getKey(), entry.getValue()));
            closeableResponse = closeableHttpClient.execute(requestBuilder.build());
            String response = EntityUtils.toString(closeableResponse.getEntity(), "UTF-8");

            if (closeableResponse.getStatusLine().getStatusCode() >= HTTP_BAD_REQUEST) {
                throw buildException(response, closeableResponse.getStatusLine().getStatusCode());
            }
            return response;
        } catch (URISyntaxException e) {
            throw new ODataClientException(WRONG_URL_MESSAGE, e);
        } catch (IOException e) {
            throw new ODataClientRuntimeException(REQUEST_FAILED_MESSAGE + url, e);
        } finally {
            closeIfNecessary(closeableResponse);
        }
    }

    @Override
    public InputStream getInputStream(Map<String, String> requestProperties, URL url) throws ODataClientException {
        LOG.debug("Preparing for getting an input stream by calling endpoint for given url: {}", url);

        try {
            RequestBuilder requestBuilder = RequestBuilder.get().setUri(url.toURI());
            requestProperties.entrySet().stream()
                    .forEach(entry -> requestBuilder.addHeader(entry.getKey(), entry.getValue()));
            CloseableHttpResponse closeableResponse = closeableHttpClient.execute(requestBuilder.build());

            if (closeableResponse.getStatusLine().getStatusCode() >= HTTP_BAD_REQUEST) {
                throw buildException(EntityUtils.toString(closeableResponse.getEntity(), "UTF-8"),
                        closeableResponse.getStatusLine().getStatusCode());
            }
            return closeableResponse.getEntity().getContent();
        } catch (URISyntaxException e) {
            throw new ODataClientException(WRONG_URL_MESSAGE, e);
        } catch (IOException e) {
            throw new ODataClientRuntimeException(REQUEST_FAILED_MESSAGE + url, e);
        }
    }

    @Override
    public String doPostEntity(Map<String, String> requestProperties, URL url, String body,
                               MediaType contentType, MediaType acceptType) throws ODataClientException {
        return sendRequest(getRequestHeaders(requestProperties, body.length(), contentType, acceptType),
                url, body, ODataRequest.Method.POST.name());
    }

    @Override
    public String doPutEntity(Map<String, String> requestProperties, URL url, String body, MediaType type)
            throws ODataClientException {
        return sendRequest(getRequestHeaders(requestProperties, body.length(), type, type),
                url, body, ODataRequest.Method.PUT.name());
    }

    @Override
    public void doDeleteEntity(Map<String, String> requestProperties, URL url) throws ODataClientException {
        sendRequest(getRequestHeaders(requestProperties, 0, ATOM_XML, ATOM_XML), url, "",
                ODataRequest.Method.DELETE.name());
    }

    private Map<String, String> getRequestHeaders(
            Map<String, String> requestProperties, int bodyLength, MediaType contentType, MediaType acceptType) {
        Map<String, String> headers = populateRequestProperties(requestProperties, bodyLength, contentType, acceptType);
        headers.remove(HeaderNames.CONTENT_LENGTH);
        return headers;
    }

    private String sendRequest(Map<String, String> properties, URL url, String body, String requestMethod)
            throws ODataClientException {
        LOG.debug("Preparing to make a {} request for given url: {}", requestMethod, url);

        CloseableHttpResponse closeableResponse = null;
        try {
            RequestBuilder requestBuilder = RequestBuilder.create(requestMethod)
                    .setUri(url.toURI())
                    .setEntity(new StringEntity(body));
            properties.entrySet().stream()
                    .forEach(entry -> requestBuilder.addHeader(entry.getKey(), entry.getValue()));
            closeableResponse = closeableHttpClient.execute(requestBuilder.build());
            String response = EntityUtils.toString(closeableResponse.getEntity(), "UTF-8");

            if (closeableResponse.getStatusLine().getStatusCode() >= HTTP_BAD_REQUEST) {
                throw buildException(response, closeableResponse.getStatusLine().getStatusCode());
            }
            return response;
        } catch (URISyntaxException e) {
            throw new ODataClientException(WRONG_URL_MESSAGE, e);
        } catch (IOException e) {
            throw new ODataClientRuntimeException(REQUEST_FAILED_MESSAGE + url, e);
        } finally {
            closeIfNecessary(closeableResponse);
        }
    }
}
