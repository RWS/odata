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
package com.sdl.odata.client.caller;

import static com.sdl.odata.api.service.HeaderNames.ODATA_CHUNKED_ERROR_MESSAGE_PROPERTY;
import static com.sdl.odata.api.service.HeaderNames.TRANSFER_ENCODING;
import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.api.service.MediaType.JSON_FULL_METADATA;
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

import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.client.api.caller.EndpointCaller;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.exception.ODataClientSocketException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of Endpoint Caller.
 */
public class BasicEndpointCaller implements EndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(BasicEndpointCaller.class);

    private static final Pattern ODATA_CHUNKED_ERROR_MESSAGE_PATTERN = Pattern
            .compile(ODATA_CHUNKED_ERROR_MESSAGE_PROPERTY + ":(.*)");

    private Integer timeout;
    private int proxyServerPort;
    private String proxyServerHostName;

    public BasicEndpointCaller(Properties properties) {
        LOG.trace("Starting to inject client with properties");

        proxyServerHostName = getStringProperty(properties, CLIENT_SERVICE_PROXY_HOST_NAME);
        timeout = getIntegerProperty(properties, CLIENT_CONNECTION_TIMEOUT, CLIENT_TIMEOUT_DEFAULT);
        Integer proxyPort = getIntegerProperty(properties, CLIENT_SERVICE_PROXY_PORT);
        proxyServerPort = proxyPort == null ? CLIENT_PROXY_PORT_DEFAULT : proxyPort;

        logConfiguration();
    }

    @Override
    public String callEndpoint(Map<String, String> requestProperties, URL urlToCall) throws ODataClientException {
        LOG.debug("Preparing the call endpoint for given url: {}", urlToCall);
        HttpURLConnection conn = getConnection(populateRequestProperties(requestProperties,
            -1, null, JSON_FULL_METADATA), urlToCall);
        return getResponse(conn);
    }

    @Override
    public InputStream getInputStream(Map<String, String> requestProperties, URL url)
            throws ODataClientRuntimeException {
        HttpURLConnection conn = getConnection(requestProperties, url);
        try {
            return conn.getInputStream();
        } catch (IOException e) {
            conn.disconnect();
            throw new ODataClientRuntimeException("Unable to get connection input stream for url: " + url, e);
        }
    }

    @Override
    public String doPostEntity(Map<String, String> requestProperties, URL urlToCall, String body,
                               MediaType contentType, MediaType acceptType)
            throws ODataClientException {
        return sendRequest(populateRequestProperties(requestProperties, body.length(), contentType, acceptType),
                urlToCall, body, ODataRequest.Method.POST.name());
    }

    @Override
    public String doPutEntity(Map<String, String> requestProperties, URL urlToCall, String body,
                              MediaType type) throws ODataClientException {
        return sendRequest(populateRequestProperties(requestProperties, body.length(), type, type),
                urlToCall, body, ODataRequest.Method.PUT.name());
    }

    @Override
    public void doDeleteEntity(Map<String, String> requestProperties, URL urlToCall) throws ODataClientException {
        sendRequest(populateRequestProperties(requestProperties, 0, ATOM_XML, ATOM_XML), urlToCall, "",
                ODataRequest.Method.DELETE.name());
    }

    private String sendRequest(Map<String, String> properties, URL urlToCall, String body, String requestMethod)
            throws ODataClientException {
        HttpURLConnection httpConnection = getConnection(properties, urlToCall);
        httpConnection.setDoOutput(true);

        DataOutputStream dataOutputStream = null;
        BufferedWriter writer = null;
        try {
            httpConnection.setRequestMethod(requestMethod);
            // Sending request
            dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
            writer = new BufferedWriter(new OutputStreamWriter(dataOutputStream, "UTF-8"));
            writer.write(body);

            writer.flush();
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new ODataClientException("Could not perform " + requestMethod + " request.", e);
        } finally {
            closeIfNecessary(writer);
            closeIfNecessary(dataOutputStream);
        }

        return getResponse(httpConnection);
    }

    private HttpURLConnection getConnection(Map<String, String> requestProperties, URL url)
            throws ODataClientRuntimeException {
        HttpURLConnection urlConnection;
        try {
            if (proxyServerHostName == null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            } else {
                InetSocketAddress proxySocketAddress =
                        new InetSocketAddress(proxyServerHostName, proxyServerPort);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, proxySocketAddress);
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            }
        } catch (IOException e) {
            LOG.error("Could not open connection to the service endpoint.", e);
            if (proxyServerHostName != null) {
                LOG.info("Using proxy host='{}' port='{}'", proxyServerHostName, proxyServerPort);
            }
            throw new ODataClientRuntimeException("Could not open connection to the service endpoint.", e);
        }

        if (timeout != null) {
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
        }

        if (requestProperties != null && !requestProperties.isEmpty()) {
            requestProperties.entrySet().stream()
                    .forEach(entry -> urlConnection.setRequestProperty(entry.getKey(), entry.getValue()));
        }
        return urlConnection;
    }

    private String getResponse(HttpURLConnection httpConnection) throws ODataClientException {
        BufferedReader bufferedReader = null;
        try {
            int responseCode = httpConnection.getResponseCode();
            boolean isError = responseCode >= HttpURLConnection.HTTP_BAD_REQUEST;
            InputStream inputStream = isError
                    ? httpConnection.getErrorStream()
                    : httpConnection.getInputStream();
            LOG.debug("Request ended with {} status code.", responseCode);

            StringBuilder response = new StringBuilder(isError ? "Unable to get response from OData service: " : "");

            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine).append(System.lineSeparator());
                }
            } else {
                response.append("No Response.");
            }
            String resultResponse = response.toString();
            if ("chunked".equals(httpConnection.getHeaderField(TRANSFER_ENCODING))) {
                String[] responseLines = resultResponse.split(System.lineSeparator());
                Matcher matcher = ODATA_CHUNKED_ERROR_MESSAGE_PATTERN.matcher(responseLines[responseLines.length - 1]);
                if (matcher.matches()) {
                    String errorMessage = matcher.group(1);
                    resultResponse = "Unable to get response from OData service: " + errorMessage;
                    isError = true;
                }
            }

            if (isError) {
                throw buildException(resultResponse, responseCode);
            }

            return resultResponse;
        } catch (SocketException e) {
            throw new ODataClientSocketException("Could not initiate connection to the endpoint.", e);
        } catch (IOException e) {
            throw new ODataClientException("Unable to process response from OData service.", e);
        } finally {
            closeIfNecessary(bufferedReader);
            httpConnection.disconnect();
        }
    }

    private void logConfiguration() {
        if (LOG.isDebugEnabled()) {
            StringBuilder configLog = new StringBuilder("Client is initialized with following parameters: timeout = ")
                    .append(timeout);
            if (proxyServerHostName != null) {
                configLog.append(", proxyServerHostName = '").append(proxyServerHostName)
                        .append("', proxyServerPort = ").append(proxyServerPort);
            }
            LOG.debug(configLog.toString());
        }
    }
}
