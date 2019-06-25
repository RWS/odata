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
package com.sdl.odata.parser;

import java.lang.invoke.MethodHandles;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Parser Configuration.
 */
@Configuration
@ComponentScan("com.sdl.odata.parser")
public class ParserConfiguration {

    private Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Default base path which anything that ends with .svc .
     */
    public static final String DEFAULT_BASE_PATH = "(?i)^.*?\\.svc";

    @Value("${odata.base-path:(?i)^.*?\\.svc}")
    private String basePath = DEFAULT_BASE_PATH;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @PostConstruct
    public void logIt() {
        logger.info("{}: [{}]", this.getClass(), this.getBasePath());
    }
}
