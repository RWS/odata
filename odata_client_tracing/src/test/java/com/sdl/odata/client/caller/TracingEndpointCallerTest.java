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

import com.sdl.odata.client.URLTestUtils;
import com.sdl.odata.client.api.caller.EndpointCaller;
import com.sdl.odata.client.api.exception.ODataClientException;
import com.sdl.odata.client.api.exception.ODataClientHttpError;
import com.sdl.odata.client.api.exception.ODataClientNotAuthorized;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.odata.client.api.exception.ODataClientTimeout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static com.sdl.odata.api.service.MediaType.JSON;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_HOST_NAME;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_PORT;
import static java.lang.System.lineSeparator;
import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Integration test for {@link TracingEndpointCaller}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@DirtiesContext
public class TracingEndpointCallerTest {

    private static final String RESPONSE = "/response.xml";

    private EndpointCaller caller = new TracingEndpointCaller(new Properties());

    @Value("${local.server.port}")
    private int port;

    /**
     * Used, to capture exceptions.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String basePath;

    @Before
    public void init() {
        basePath = "http://127.0.0.1:" + port;
    }

    @Test
    public void callEndpoint() throws ODataClientException, MalformedURLException {
        String response = caller.callEndpoint(singletonMap("Accept", JSON.getType()), new URL(basePath + RESPONSE));
        String expected = URLTestUtils.loadTextFile(RESPONSE);
        assertThat(response, equalTo(expected.substring(0, expected.lastIndexOf(lineSeparator()))));
    }

    @Test
    public void callEndpointWithoutResponse() throws ODataClientException, MalformedURLException {
        thrown.expect(ODataClientHttpError.class);
        caller.callEndpoint(singletonMap("Accept", JSON.getType()), new URL(basePath));
    }


    @Test
    public void getInputStream() throws ODataClientException, IOException {
        InputStream stream = caller.getInputStream(
                singletonMap("Accept", JSON.getType()), new URL(basePath + RESPONSE));
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

        EndpointCaller proxifiedCaller = new TracingEndpointCaller(properties);
        String response = proxifiedCaller.callEndpoint(emptyMap(), new URL(basePath + RESPONSE));
        String result = URLTestUtils.loadTextFile(RESPONSE);

        assertThat(response, equalTo(result.substring(0, result.lastIndexOf(lineSeparator()))));
    }

    @Test
    public void unauthorizedPostEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientNotAuthorized.class);
        caller.doPostEntity(emptyMap(), buildUrlToCall(HTTP_UNAUTHORIZED), "", JSON, JSON);
    }

    @Test
    public void forbiddenPostEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientHttpError.class);
        caller.doPostEntity(emptyMap(), buildUrlToCall(HTTP_FORBIDDEN), "", JSON, JSON);
    }

    @Test
    public void timeoutPostEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientTimeout.class);
        caller.doPostEntity(emptyMap(), buildUrlToCall(HTTP_CLIENT_TIMEOUT), "", JSON, JSON);
    }

    @Test
    public void okPutEntity() throws MalformedURLException, ODataClientException {
        String body = URLTestUtils.loadTextFile(RESPONSE);
        String result = caller.doPutEntity(emptyMap(), new URL(basePath + RESPONSE), body, JSON);
        assertThat(body.substring(0, body.lastIndexOf(lineSeparator())), equalTo(result));
    }

    @Test
    public void unauthorizedPutEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientNotAuthorized.class);
        caller.doPutEntity(emptyMap(), buildUrlToCall(HTTP_UNAUTHORIZED), "", JSON);
    }

    @Test
    public void forbiddenPutEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientHttpError.class);
        caller.doPutEntity(emptyMap(), buildUrlToCall(HTTP_FORBIDDEN), "", JSON);
    }

    @Test
    public void timeoutPutEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientTimeout.class);
        caller.doPutEntity(emptyMap(), buildUrlToCall(HTTP_CLIENT_TIMEOUT), "", JSON);
    }

    @Test
    public void unauthorizedDeleteEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientNotAuthorized.class);
        caller.doDeleteEntity(emptyMap(), buildUrlToCall(HTTP_UNAUTHORIZED));
    }

    @Test
    public void forbiddenDeleteEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientHttpError.class);
        caller.doDeleteEntity(emptyMap(), buildUrlToCall(HTTP_FORBIDDEN));
    }

    @Test
    public void timeoutDeleteEntity() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientTimeout.class);
        caller.doDeleteEntity(emptyMap(), buildUrlToCall(HTTP_CLIENT_TIMEOUT));
    }

    @Test
    public void unauthorizedGet() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientNotAuthorized.class);
        caller.callEndpoint(emptyMap(), buildUrlToCall(HTTP_UNAUTHORIZED));
    }

    @Test
    public void forbiddenGet() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientHttpError.class);
        caller.callEndpoint(emptyMap(), buildUrlToCall(HTTP_FORBIDDEN));
    }

    @Test
    public void timeoutGet() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientTimeout.class);
        caller.callEndpoint(emptyMap(), buildUrlToCall(HTTP_CLIENT_TIMEOUT));
    }

    @Test
    public void timeoutInputStream() throws MalformedURLException, ODataClientException {
        thrown.expect(ODataClientTimeout.class);
        caller.getInputStream(emptyMap(), buildUrlToCall(HTTP_CLIENT_TIMEOUT));
    }

    @Test
    public void testIOExceptionOnOpenConnection() throws IOException, ODataClientException {
        URL ioExceptionOnConnectUrl = URLTestUtils.getIOExceptionThrowingUrl(true);
        Properties properties = new Properties();
        properties.setProperty(CLIENT_SERVICE_PROXY_HOST_NAME, "localhost");
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, "9999");
        EndpointCaller proxifiedCaller = new TracingEndpointCaller(properties);
        thrown.expect(ODataClientRuntimeException.class);
        thrown.expectCause(isA(IOException.class));

        proxifiedCaller.callEndpoint(emptyMap(), ioExceptionOnConnectUrl);
    }

    private URL buildUrlToCall(int code) throws MalformedURLException {
        return new URL(basePath + "/" + code);
    }
}
