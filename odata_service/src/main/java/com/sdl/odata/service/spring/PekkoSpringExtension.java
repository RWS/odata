/**
 * Copyright (c) 2014-2025 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import org.apache.pekko.actor.AbstractExtensionId;
import org.apache.pekko.actor.ExtendedActorSystem;
import org.apache.pekko.actor.Extension;
import org.apache.pekko.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The PekkoSpringExtension registers the Spring hook into the Pekko extension system.
 */
@Component
public class PekkoSpringExtension extends AbstractExtensionId<PekkoSpringExtension.PekkoExtension> {

    @Override
    public PekkoExtension createExtension(ExtendedActorSystem system) {
        return new PekkoExtension();
    }

    /**
     * The Pekko Extension.
     */
    public static class PekkoExtension implements Extension {
        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext ctx) {
            this.applicationContext = ctx;
        }

        public Props props(String actor) {
            return Props.create(PekkoSpringActorProducer.class, applicationContext, actor);
        }
    }
}
