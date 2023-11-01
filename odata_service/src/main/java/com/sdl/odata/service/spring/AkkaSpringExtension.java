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
package com.sdl.odata.service.spring;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The AkkaSpringExtension registers the Spring hook into the Akka extension system.
 */
@Component
public class AkkaSpringExtension extends AbstractExtensionId<AkkaSpringExtension.AkkaExtension> {

    @Override
    public AkkaExtension createExtension(ExtendedActorSystem system) {
        return new AkkaExtension();
    }

    /**
     * The Akka Extension.
     */
    public static class AkkaExtension implements Extension {
        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext ctx) {
            this.applicationContext = ctx;
        }

        public Props props(String actor) {
            return Props.create(AkkaSpringActorProducer.class, applicationContext, actor);
        }
    }
}
