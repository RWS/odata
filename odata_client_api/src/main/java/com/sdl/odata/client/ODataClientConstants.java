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
package com.sdl.odata.client;

/**
 * OData Client Constants.
 */
public final class ODataClientConstants {

    private ODataClientConstants() {
    }

    /**
     * Default values for client properties.
     */
    public static final class DefaultValues {

        private DefaultValues() {
        }

        /**
         * Client proxy default port number.
         */
        public static final Integer CLIENT_PROXY_PORT_DEFAULT = 8888;

        /**
         * Client default web service timeout.
         */
        public static final Integer CLIENT_TIMEOUT_DEFAULT = 30000;

        /**
         * Client configuration file name.
         */
        public static final String CONFIG_FILE_NAME = "odata_client_conf.xml";

        /**
         * Client default connection retries.
         */
        public static final int CLIENT_CONNECTION_MAX_RETRIES_DEFAULT = 10;
    }

    /**
     * Web service properties.
     */
    public static final class WebService {

        private WebService() {
        }

        /**
         * Web service URI to call property.
         */
        public static final String CLIENT_SERVICE_URI = "ServiceUri";

        /**
         * Web service connection timeout property.
         */
        public static final String CLIENT_CONNECTION_TIMEOUT = "ConnectionTimeout";

        /**
         * Web service proxy host name property.
         */
        public static final String CLIENT_SERVICE_PROXY_HOST_NAME = "ServiceProxyHostName";

        /**
         * Web service proxy port property.
         */
        public static final String CLIENT_SERVICE_PROXY_PORT = "ServiceProxyPort";

        /**
         * Web service maximum connection retry count property.
         */
        public static final String CLIENT_CONNECTION_MAX_RETRIES = "ConnectionMaxRetries";
    }

    /**
     * Quote constant, used in forming exception/error messaging, etc.
     */
    public static final char QUOTE = '\'';

}
