/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.client.URLTestUtils;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientHttpError;
import com.sdl.odata.client.api.exception.ODataClientNotAuthorized;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.exception.ODataClientTimeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_HOST_NAME;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_PORT;
import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Integration test for {@link BasicEndpointCaller}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@DirtiesContext
public class BasicEndpointCallerTest {

    private static final String RESPONSE = "/response.xml";
    private final BasicEndpointCaller caller = new BasicEndpointCaller(new Properties());

    @Value("${local.server.port}")
    private int port;

    private String basePath;

    @BeforeEach
    public void init() {
        basePath = "http://127.0.0.1:" + port;
    }

    @Test
    public void callEndpoint() throws ODataClientException, MalformedURLException {
        String response = caller.callEndpoint(singletonMap("Accept", APPLICATION_JSON_VALUE),
                new URL(basePath + RESPONSE));
        assertThat(response, equalTo(URLTestUtils.loadTextFile(RESPONSE)));
    }

    @Test
    public void callEndpointWithoutResponse() {
        assertThrows(ODataClientHttpError.class, () ->
                        caller.callEndpoint(singletonMap("Accept", APPLICATION_JSON_VALUE), new URL(basePath)),
                "Unable to get response from OData service: No Response"
        );
    }

    @Test
    public void getInputStream() throws IOException {
        URL url = new URL(basePath + RESPONSE);
        InputStream stream = caller.getInputStream(singletonMap("Accept", APPLICATION_JSON_VALUE), url);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        assertThat(sb.toString(), equalTo(URLTestUtils.loadTextFile(RESPONSE)));
    }

    @Test
    public void testEmptyProxyPortNumber() throws ODataClientException, MalformedURLException {
        Properties properties = new Properties();
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, "");

        BasicEndpointCaller proxifiedCaller = new BasicEndpointCaller(properties);
        String response = proxifiedCaller.callEndpoint(emptyMap(), new URL(basePath + RESPONSE));

        assertThat(response, equalTo(URLTestUtils.loadTextFile(RESPONSE)));
    }

    @Test
    public void unauthorizedPostEntity() {
        assertThrows(ODataClientNotAuthorized.class, () ->
                caller.doPostEntity(new HashMap<>(), buildUrlToCall(HTTP_UNAUTHORIZED), "", JSON, JSON)
        );
    }

    @Test
    public void forbiddenPostEntity() {
        assertThrows(ODataClientHttpError.class, () ->
                caller.doPostEntity(new HashMap<>(), buildUrlToCall(HTTP_FORBIDDEN), "", JSON, JSON)
        );
    }

    @Test
    public void timeoutPostEntity() {
        assertThrows(ODataClientTimeout.class, () ->
                caller.doPostEntity(new HashMap<>(), buildUrlToCall(HTTP_CLIENT_TIMEOUT), "", JSON, JSON)
        );
    }

    @Test
    public void unauthorizedPutEntity() {
        assertThrows(ODataClientNotAuthorized.class, () ->
                caller.doPutEntity(new HashMap<>(), buildUrlToCall(HTTP_UNAUTHORIZED), "", JSON)
        );
    }

    @Test
    public void forbiddenPutEntity() {
        assertThrows(ODataClientHttpError.class, () ->
                caller.doPutEntity(new HashMap<>(), buildUrlToCall(HTTP_FORBIDDEN), "", JSON)
        );
    }

    @Test
    public void timeoutPutEntity() {
        assertThrows(ODataClientTimeout.class, () ->
                caller.doPutEntity(new HashMap<>(), buildUrlToCall(HTTP_CLIENT_TIMEOUT), "", JSON)
        );
    }

    @Test
    public void unauthorizedDeleteEntity() {
        assertThrows(ODataClientNotAuthorized.class, () ->
                caller.doDeleteEntity(new HashMap<>(), buildUrlToCall(HTTP_UNAUTHORIZED))
        );
    }

    @Test
    public void forbiddenDeleteEntity() {
        assertThrows(ODataClientHttpError.class, () ->
                caller.doDeleteEntity(new HashMap<>(), buildUrlToCall(HTTP_FORBIDDEN))
        );
    }

    @Test
    public void timeoutDeleteEntity() {
        assertThrows(ODataClientTimeout.class, () ->
                caller.doDeleteEntity(new HashMap<>(), buildUrlToCall(HTTP_CLIENT_TIMEOUT))
        );
    }

    @Test
    public void unauthorizedGet() {
        assertThrows(ODataClientNotAuthorized.class, () ->
                caller.callEndpoint(new HashMap<>(), buildUrlToCall(HTTP_UNAUTHORIZED))
        );
    }

    @Test
    public void forbiddenGet() {
        assertThrows(ODataClientHttpError.class, () ->
                caller.callEndpoint(new HashMap<>(), buildUrlToCall(HTTP_FORBIDDEN))
        );
    }

    @Test
    public void timeoutGet() {
        assertThrows(ODataClientTimeout.class, () ->
                caller.callEndpoint(new HashMap<>(), buildUrlToCall(HTTP_CLIENT_TIMEOUT))
        );
    }

    @Test
    public void testIOExceptionOnOpenConnection() throws IOException {
        URL ioExceptionOnConnectUrl = URLTestUtils.getIOExceptionThrowingUrl(true);
        Properties properties = new Properties();
        properties.setProperty(CLIENT_SERVICE_PROXY_HOST_NAME, "localhost");
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, "9999");
        BasicEndpointCaller proxifiedCaller = new BasicEndpointCaller(properties);

        assertThrows(ODataClientRuntimeException.class, () ->
                        proxifiedCaller.callEndpoint(emptyMap(), ioExceptionOnConnectUrl),
                "Could not open connection to the service endpoint."
        );
    }

    private URL buildUrlToCall(int code) throws MalformedURLException {
        return new URL(basePath + "/" + code);
    }
}
