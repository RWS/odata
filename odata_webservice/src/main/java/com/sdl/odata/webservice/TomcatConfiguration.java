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
package com.sdl.odata.webservice;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * Basic configuration for running embeded Tomcat with https support.
 */
@Configuration
public class TomcatConfiguration {
    private static final String HTTPS_SCHEME = "https";

    @Value("${https.enabled}")
    private String httpsModeFlag;

    @Value("${https.port}")
    private String httpsPort;

    @Value("${https.keystore-passwd}")
    private String keystorePasswd;

    @Value("${https.keystore-path}")
    private String keystorePath;

    @Value("${https.truststore-passwd}")
    private String truststorePasswd;

    @Value("${https.key-alias}")
    private String keyAlias;


    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        if (Boolean.parseBoolean(httpsModeFlag)) {
            tomcat.addAdditionalTomcatConnectors(createSslConnector());
        }
        return tomcat;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            File keystore = new ClassPathResource(keystorePath).getFile();
            connector.setScheme(HTTPS_SCHEME);
            connector.setSecure(true);
            connector.setPort(Integer.parseInt(httpsPort));
            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass(keystorePasswd);
            protocol.setTruststoreFile(keystore.getAbsolutePath());
            protocol.setTruststorePass(truststorePasswd);
            protocol.setKeyAlias(keyAlias);
            return connector;
        } catch (IOException ex) {
            throw new IllegalStateException("cant access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }
}
