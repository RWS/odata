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

import com.sdl.odata.api.service.MediaType;
import com.sdl.odata.client.api.caller.EndpointCaller;
import com.sdl.odata.client.api.exception.ODataClientException;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import static com.sdl.odata.api.service.MediaType.ATOM_XML;
import static com.sdl.odata.client.ODataClientConstants.DefaultValues.CLIENT_CONNECTION_MAX_RETRIES_DEFAULT;
import static com.sdl.odata.client.ODataClientConstants.DefaultValues.CLIENT_PROXY_PORT_DEFAULT;
import static com.sdl.odata.client.ODataClientConstants.DefaultValues.CLIENT_TIMEOUT_DEFAULT;
import static com.sdl.odata.client.ODataClientConstants.QUOTE;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_CONNECTION_MAX_RETRIES;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_CONNECTION_TIMEOUT;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_HOST_NAME;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_PORT;
import static com.sdl.odata.client.property.PropertyUtils.getIntegerProperty;
import static com.sdl.odata.client.property.PropertyUtils.getStringProperty;

/**
 * The basic implementation of Endpoint Caller.
 */
public class BasicEndpointCaller implements EndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(BasicEndpointCaller.class);

    private Integer timeout;
    private int proxyServerPort;
    private String proxyServerHostName;
    private int maxRetries;

    public BasicEndpointCaller(Properties properties) {
        LOG.trace("Starting to inject client with properties");

        proxyServerHostName = getStringProperty(properties, CLIENT_SERVICE_PROXY_HOST_NAME);
        timeout = getIntegerProperty(properties, CLIENT_CONNECTION_TIMEOUT, CLIENT_TIMEOUT_DEFAULT);
        Integer proxyPort = getIntegerProperty(properties, CLIENT_SERVICE_PROXY_PORT);
        proxyServerPort = proxyPort == null ? CLIENT_PROXY_PORT_DEFAULT : proxyPort;
        maxRetries = getIntegerProperty(properties,
                CLIENT_CONNECTION_MAX_RETRIES, CLIENT_CONNECTION_MAX_RETRIES_DEFAULT);

        logConfiguration();
    }

    @Override
    public String callEndpoint(Map<String, String> requestProperties, URL urlToCall) throws ODataClientException {
        LOG.debug("Preparing the call endpoint for given url: {}", urlToCall);
        URLConnection conn = getConnection(requestProperties, urlToCall);
        return getResponse(conn, urlToCall);
    }

    @Override
    public InputStream getInputStream(Map<String, String> requestProperties, URL url) throws ODataClientException {
        try {
            return getConnection(requestProperties, url).getInputStream();
        } catch (IOException e) {
            throw new ODataClientException("Unable to get connection input stream for url: " + url, e);
        }
    }

    private String sendRequest(Map<String, String> requestProperties, URL urlToCall, String postRequestBody,
                               String requestMethod, MediaType contentType, MediaType acceptType)
            throws ODataClientException {
        String result = null;
        DataOutputStream dataOutputStream = null;
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;

        Map<String, String> properties;
        // requestProperties may be immutable. It can be null or empty, so copying will take place if necessary.
        if (requestProperties == null || requestProperties.isEmpty()) {
            properties = new HashMap<>();
        } else {
            properties = new HashMap<>(requestProperties);
        }
        properties.put("Accept", acceptType.toString());
        properties.put("Content-Type", contentType.toString());
        properties.put("Content-Length", String.valueOf(postRequestBody.length()));

        HttpURLConnection httpConnection = (HttpURLConnection) getConnection(properties, urlToCall);

        try {
            httpConnection.setRequestMethod(requestMethod);

            // Send request
            httpConnection.setDoOutput(true);
            dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
            dataOutputStream.writeBytes(postRequestBody);
            dataOutputStream.flush();
            LOG.debug("{} request ended with {} status code", requestMethod, httpConnection.getResponseCode());

            inputStream = httpConnection.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine).append(System.lineSeparator());
            }
            result = response.toString();
        } catch (IOException e) {
            String errorMessage = "Unable to make " + requestMethod + " request to OData service";
            if (httpConnection.getErrorStream() != null) {
                try (Scanner errorScanner = new Scanner(httpConnection.getErrorStream())) {
                    if (errorScanner.useDelimiter("\\A").hasNext()) {
                        errorMessage = errorScanner.next();
                    }
                }
            }
            throw new ODataClientException(errorMessage, e);
        } finally {
            try {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOG.error("IOException when closing InputStream", e);
                    }
                }
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                throw new ODataClientException("Unable to close stream while " + requestMethod
                        + " request to OData client", e);
            }
        }
        return result;
    }

    @Override
    public String doPostEntity(Map<String, String> requestProperties, URL urlToCall, String postRequestBody,
                               MediaType contentType, MediaType acceptType)
            throws ODataClientException {
        return sendRequest(requestProperties, urlToCall, postRequestBody, "POST", contentType, acceptType);
    }

    @Override
    public String doPutEntity(Map<String, String> requestProperties, URL urlToCall, String putRequestBody,
                              MediaType type) throws ODataClientException {
        return sendRequest(requestProperties, urlToCall, putRequestBody, "PUT", type, type);
    }

    @Override
    public void doDeleteEntity(Map<String, String> requestProperties, URL urlToCall) throws ODataClientException {
        sendRequest(requestProperties, urlToCall, "", "DELETE", ATOM_XML, ATOM_XML);
    }

    private URLConnection getConnection(Map<String, String> requestProperties, URL url) throws ODataClientException {
        URLConnection urlConnection;
        try {
            if (proxyServerHostName == null) {
                urlConnection = url.openConnection();
            } else {
                InetSocketAddress proxySocketAddress =
                        new InetSocketAddress(proxyServerHostName, proxyServerPort);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, proxySocketAddress);
                urlConnection = url.openConnection(proxy);
            }
            if (timeout != null) {
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setReadTimeout(timeout);
            }
        } catch (IOException e) {
            throw processedException(e, url, " when getting connection to");
        }

        if (requestProperties != null && !requestProperties.isEmpty()) {
            requestProperties.entrySet().stream()
                    .forEach(entry -> urlConnection.setRequestProperty(entry.getKey(), entry.getValue()));
        }
        return urlConnection;
    }

    private String getResponse(URLConnection urlConnection, URL url) throws ODataClientException {
        urlConnection.setRequestProperty("Accept", "application/xml");

        StringBuilder response = new StringBuilder();
        InputStream inputStream = null;

        BufferedReader buf = null;
        try {
            inputStream = getInputStream(urlConnection);
            buf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = buf.readLine()) != null) {
                response.append(line).append(System.lineSeparator());
            }
        } catch (IOException | RuntimeException e) {
            throw processedException(e, url, " when accessing");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("IOException when closing InputStream", e);
                }
            }
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    LOG.error("IOException when closing BufferedReader", e);
                }
            }
        }
        return response.toString();
    }

    private InputStream getInputStream(URLConnection urlConnection) throws IOException, ODataClientException {
        int retryCounter = 1;

        while (retryCounter <= maxRetries) {
            try {
                synchronized (this) {
                    return urlConnection.getInputStream();
                }
            } catch (IOException | RuntimeException e) {
                if (e.getMessage().startsWith("Address already in use")) {
                    LOG.info("Error getting connection, will try again. Retry count = " + retryCounter++ + "," +
                            "Maximum retries = " + maxRetries);
                } else {
                    LOG.error("Exception when getting Input Stream", e);
                    throw e;
                }
            }
        }
        throw new ODataClientException("Could not get data ever after maximum retries");
    }

    private ODataClientException processedException(Throwable e, URL url, String message) throws ODataClientException {
        // TODO-SL: this exception builders should be refactored
        LOG.error("Exception when getting data from service endpoint", e);
        String proxyInfo = "";
        if (proxyServerHostName != null) {
            proxyInfo = " (proxy=" + QUOTE + proxyServerHostName + QUOTE + " port=" +
                    QUOTE + proxyServerPort + QUOTE + ")";
        }
        return new ODataClientException("Caught " + QUOTE + e.getClass().getSimpleName() +
                (e.getMessage() != null ? ":" + e.getMessage() : "") +
                QUOTE +
                message +
                " URL " + QUOTE + url.toString() + QUOTE
                + proxyInfo,
                e);
    }

    private void logConfiguration() {
        if (LOG.isDebugEnabled()) {
            StringBuilder configurationLog = new StringBuilder("Client is initialized with following parameters: ");
            configurationLog.append("timeout: ").append(timeout).append(", maxRetries: ").append(maxRetries);
            if (proxyServerHostName != null) {
                configurationLog.append(", proxyServerHostName: ").append(proxyServerHostName);
                configurationLog.append(", proxyServerPort: ").append(proxyServerPort);
            }
            LOG.debug(configurationLog.toString());
        }
    }
}
