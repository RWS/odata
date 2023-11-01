/**
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import java.util.Map;
import java.util.Properties;

import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_CONNECTION_TIMEOUT;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_HOST_NAME;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_PROXY_PORT;
import static com.sdl.odata.client.ODataClientConstants.WebService.CLIENT_SERVICE_URI;

/**
 * This builder constructs properties for the OData Client.
 */
public class ClientPropertiesBuilder {

    private Properties properties;

    public ClientPropertiesBuilder() {
        this.properties = new Properties();
    }

    public ClientPropertiesBuilder(Map<String, String> configValues) {
        this.properties = new Properties();

        String clientServiceUriValue = configValues.get(CLIENT_SERVICE_URI);
        String clientConnectionTimeout = configValues.get(CLIENT_CONNECTION_TIMEOUT);
        String clientServiceProxyHostName = configValues.get(CLIENT_SERVICE_PROXY_HOST_NAME);
        String clientServiceProxyPort = configValues.get(CLIENT_SERVICE_PROXY_PORT);

        if (clientServiceUriValue != null) {
            properties.setProperty(CLIENT_SERVICE_URI, clientServiceUriValue);
        }
        if (clientConnectionTimeout != null) {
            properties.setProperty(CLIENT_CONNECTION_TIMEOUT, clientConnectionTimeout);
        }
        if (clientServiceProxyHostName != null) {
            properties.setProperty(CLIENT_SERVICE_PROXY_HOST_NAME, clientServiceProxyHostName);
        }
        if (clientServiceProxyPort != null) {
            properties.setProperty(CLIENT_SERVICE_PROXY_PORT, clientServiceProxyPort);

        }
    }

    /**
     * Build the specified properties.
     *
     * @return the built properties.
     */
    public Properties build() {
        return properties;
    }

    /**
     * Specify the OData web service URI, e.g. ""http://www.example.com/odata.svc"
     *
     * @param serviceUri the OData web service URI
     * @return the properties builder
     */
    public ClientPropertiesBuilder withServiceUri(String serviceUri) {
        properties.setProperty(CLIENT_SERVICE_URI, serviceUri);
        return this;
    }

    /**
     * Specify a timeout period for the client (in milliseconds).
     *
     * @param timeout the client timeout period (in milliseconds)
     * @return the properties builder
     */
    public ClientPropertiesBuilder withClientTimeout(Integer timeout) {
        properties.setProperty(CLIENT_CONNECTION_TIMEOUT, timeout.toString());
        return this;
    }

    /**
     * If a proxy is used to access the OData web service this specifies it's host name / ip address.
     *
     * @param proxyHostName the proxy port number
     * @return the properties builder
     */
    public ClientPropertiesBuilder withProxyHostName(String proxyHostName) {
        properties.setProperty(CLIENT_SERVICE_PROXY_HOST_NAME, proxyHostName);
        return this;
    }

    /**
     * If a proxy is used to access the OData web service this specifies it's port number.
     *
     * @param proxyPortNumber the proxy port number
     * @return the properties builder
     */
    public ClientPropertiesBuilder withProxyPort(Integer proxyPortNumber) {
        properties.setProperty(CLIENT_SERVICE_PROXY_PORT, proxyPortNumber.toString());
        return this;
    }
}
