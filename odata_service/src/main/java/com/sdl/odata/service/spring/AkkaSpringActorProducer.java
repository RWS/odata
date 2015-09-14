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
package com.sdl.odata.service.spring;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * The AkkaSpringActorProducer has a reference to the spring context and is responsible
 * for retrieving the actor based Bean from the Spring context.
 */
public class AkkaSpringActorProducer implements IndirectActorProducer {
    private ApplicationContext applicationContext;
    private String beanName;

    public AkkaSpringActorProducer(ApplicationContext applicationContext, String beanName) {
        this.applicationContext = applicationContext;
        this.beanName = beanName;
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(beanName);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return applicationContext.getType(beanName).asSubclass(Actor.class);
    }
}
