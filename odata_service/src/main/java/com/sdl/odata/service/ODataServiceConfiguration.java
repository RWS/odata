/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.service;

import com.sdl.odata.edm.EdmConfiguration;
import com.sdl.odata.parser.ParserConfiguration;
import com.sdl.odata.processor.ProcessorConfiguration;
import com.sdl.odata.renderer.RendererConfiguration;
import com.sdl.odata.service.actor.ODataBatchProcessorActor;
import com.sdl.odata.service.actor.ODataBatchRendererActor;
import com.sdl.odata.service.actor.ODataParserActor;
import com.sdl.odata.service.actor.ODataQueryProcessorActor;
import com.sdl.odata.service.actor.ODataRendererActor;
import com.sdl.odata.service.actor.ODataRequestProcessorActor;
import com.sdl.odata.service.actor.ODataUnmarshallerActor;
import com.sdl.odata.service.actor.ODataWriteProcessorActor;
import com.sdl.odata.service.protocol.BatchOperation;
import com.sdl.odata.service.protocol.BatchOperationResult;
import com.sdl.odata.service.protocol.ErrorMessage;
import com.sdl.odata.service.protocol.OperationResult;
import com.sdl.odata.service.protocol.ParseResult;
import com.sdl.odata.service.protocol.ReadOperation;
import com.sdl.odata.service.protocol.Render;
import com.sdl.odata.service.protocol.ServiceRequest;
import com.sdl.odata.service.protocol.Unmarshall;
import com.sdl.odata.service.protocol.UnmarshallResult;
import com.sdl.odata.service.protocol.WriteOperation;
import com.sdl.odata.service.spring.ActorProducer;
import com.sdl.odata.unmarshaller.UnmarshallerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PostConstruct;

import static com.sdl.odata.service.util.AkkaUtil.registerRoute;

/**
 * The OData Service Configuration.
 */
@Configuration
@ComponentScan("com.sdl.odata.service")
@Import({
        EdmConfiguration.class, ParserConfiguration.class, ProcessorConfiguration.class, RendererConfiguration.class,
        UnmarshallerConfiguration.class
})
@ImportResource({"classpath*:/META-INF/*/odata-*.xml"})
public class ODataServiceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ODataServiceConfiguration.class);

    @Autowired
    private ActorProducer actorProducer;

    @PostConstruct
    public void intializeService() {
        LOG.info("Initializing OData service routing");

        // register parsers
        registerRoute(ServiceRequest.class, ODataParserActor.class, actorProducer);
        // register unmarshallers
        registerRoute(Unmarshall.class, ODataUnmarshallerActor.class, actorProducer);
        // register processors
        registerRoute(ParseResult.class, ODataRequestProcessorActor.class, actorProducer);
        registerRoute(UnmarshallResult.class, ODataRequestProcessorActor.class, actorProducer);
        registerRoute(ReadOperation.class, ODataQueryProcessorActor.class, actorProducer);
        registerRoute(WriteOperation.class, ODataWriteProcessorActor.class, actorProducer);
        registerRoute(BatchOperation.class, ODataBatchProcessorActor.class, actorProducer);
        registerRoute(OperationResult.class, ODataRequestProcessorActor.class, actorProducer);
        // register renderers
        registerRoute(Render.class, ODataRendererActor.class, actorProducer);
        registerRoute(BatchOperationResult.class, ODataBatchRendererActor.class, actorProducer);
        registerRoute(ErrorMessage.class, ODataRendererActor.class, actorProducer);
    }
}
