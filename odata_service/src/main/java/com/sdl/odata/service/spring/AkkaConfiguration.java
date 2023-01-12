/*
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
package com.sdl.odata.service.spring;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * The AkkaConfiguration initializes an Akka Spring configured ActorSystem using the Spring extension.
 */
@Component
public class AkkaConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AkkaConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AkkaSpringExtension akkaSpringExtension;

    @Bean(destroyMethod = "terminate")
    public ActorSystem actorSystem() {
        LOG.info("Creating actor system");
        ActorSystem system = ActorSystem.create("ODataAkkaSpringContext");
        akkaSpringExtension.get(system).initialize(applicationContext);
        return system;
    }
}
