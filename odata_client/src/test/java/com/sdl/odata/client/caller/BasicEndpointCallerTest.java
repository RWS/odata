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
import com.sdl.odata.client.api.exception.ODataClientException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_HOST_NAME;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_PORT;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;

/**
 * Basic Endpoint Caller Test.
 */
public class BasicEndpointCallerTest {

    private static final String RESPONSE = "/response.xml";

    @Test
    public void basicTest() throws ODataClientException {
        URL urlForFile = this.getClass().getResource(RESPONSE);
        BasicEndpointCaller caller = new BasicEndpointCaller(new Properties());
        String response = caller.callEndpoint(emptyMap(), urlForFile);
        Assert.assertEquals(URLTestUtils.loadTextFile(RESPONSE), response);
    }

    @Test
    public void testIOExceptionOnOpenConnection() throws IOException, ODataClientException {
        URL ioExceptionOnConnectUrl = URLTestUtils.getIOExceptionThrowingUrl(true);
        Properties properties = new Properties();
        properties.setProperty(CLIENT_SERVICE_PROXY_HOST_NAME, "localhost");
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, "9999");
        BasicEndpointCaller caller = new BasicEndpointCaller(properties);

        try {
            caller.callEndpoint(emptyMap(), ioExceptionOnConnectUrl);
        } catch (ODataClientException e) {
            assertEquals("Caught 'IOException:Mock IOException from openConnection' when getting connection" +
                    " to URL 'http://mock.com://service:80' (proxy='localhost' port='9999')", e.getMessage());
            assertEquals("Mock IOException from openConnection", e.getCause().getMessage());
            assertEquals(IOException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testEmptyProxyPortNumber() throws ODataClientException {
        URL url = this.getClass().getResource(RESPONSE);
        Properties properties = new Properties();
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, "");

        BasicEndpointCaller caller = new BasicEndpointCaller(properties);
        String response = caller.callEndpoint(emptyMap(), url);

        Assert.assertEquals(URLTestUtils.loadTextFile(RESPONSE), response);
    }

    @Test
    public void testProxy() throws ODataClientException {
        URL urlForFile = this.getClass().getResource(RESPONSE);

        Properties properties = new Properties();
        properties.setProperty(CLIENT_SERVICE_PROXY_HOST_NAME, "localhost");
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, "0");

        BasicEndpointCaller nativeUrlAccessor = new BasicEndpointCaller(properties);
        String response = nativeUrlAccessor.callEndpoint(emptyMap(), urlForFile);

        Assert.assertEquals(URLTestUtils.loadTextFile(RESPONSE), response);
    }

}
