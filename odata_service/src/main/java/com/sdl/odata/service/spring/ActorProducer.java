/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The ActorProducer is responsible for creating Akka actors that are using Spring dependency injection.
 */
@Component
public class ActorProducer {
    @Autowired
    private AkkaSpringExtension akkaSpringExtension;

    @Autowired
    private ActorSystem actorSystem;

    public void tell(String actorId, Object message) {
        Props props = create(actorId);
        actorSystem.actorOf(props).tell(message, null);
    }

    public void tell(String actorId, Object message, ActorRef self) {
        actorRef(actorId).tell(message, self);
    }

    public void tell(String actorId, Object message, ActorRef self, ActorContext context) {
        actorRef(actorId, context).tell(message, self);
    }

    public ActorRef actorRef(String actorId) {
        return actorSystem.actorOf(create(actorId));
    }

    public ActorRef actorRef(String actorId, ActorContext context) {
        return context.actorOf(create(actorId));
    }

    public Props create(String actorId) {
        return akkaSpringExtension.get(actorSystem).props(actorId);
    }
}
