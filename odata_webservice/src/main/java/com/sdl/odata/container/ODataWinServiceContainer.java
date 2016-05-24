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

import static org.springframework.boot.Banner.Mode.OFF;

/**
 * OData Windows Service Container.
 *
 * The special class for launching odata service container as a windows service
 * with the help of procrun (Apache Commons Deamon).
 */
public final class ODataWinServiceContainer {

    private ODataWinServiceContainer() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(ODataWinServiceContainer.class);

    /**
     * Starting the service container.
     *
     * @param args startCommand
     */
    public static void start(String[] args) {
        LOG.info("Starting Spring Application container");

        SpringApplication springApplication = new SpringApplication(ODataServiceContainer.class);
        springApplication.setBannerMode(OFF);
        springApplication.run(args);

        LOG.info("Spring application container started");
    }

    /**
     * Stopping the service container.
     *
     * @param args stopCommand
     */
    public static void stop(String[] args) {
        LOG.info("Stopping Spring Application container");
        System.exit(0);
    }

    public static void main(String[] args) {
        if ("start".equals(args[0])) {
            start(args);
        } else if ("stop".equals(args[0])) {
            stop(args);
        }
    }
}

