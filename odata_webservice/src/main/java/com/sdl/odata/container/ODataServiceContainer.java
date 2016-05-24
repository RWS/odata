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
package com.sdl.odata.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.springframework.boot.Banner.Mode.OFF;

/**
 * <p>
 * This is the main entry point to the OData Web Service.
 * </p>
 * <p>
 * Exclude automatic Hibernate configuration; we use the configuration of the CD data layer instead of the Spring
 * automatic configuration.
 * </p>
 */
@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class})
@ComponentScan({"com.sdl.odata.controller"})
public class ODataServiceContainer {
    private static final Logger LOG = LoggerFactory.getLogger(ODataServiceContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting Spring Application container");

        SpringApplication springApplication = new SpringApplication(ODataServiceContainer.class);
        springApplication.setBannerMode(OFF);
        springApplication.run(args);

        LOG.info("Spring application container started");
    }
}
