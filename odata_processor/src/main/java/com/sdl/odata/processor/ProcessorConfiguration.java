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
package com.sdl.odata.processor;

import java.lang.invoke.MethodHandles;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Processor Configuration.
 */
@Configuration
@ComponentScan("com.sdl.odata.processor")
public class ProcessorConfiguration {

    private Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${odata.update.require-id:true}")
    private Boolean updateRequireId = true;

    public Boolean getUpdateRequireId() {
        return updateRequireId;
    }

    public void setUpdateRequireId(Boolean updateRequireId) {
        this.updateRequireId = updateRequireId;
    }

    @PostConstruct
    public void logIt()
    {
        LOG.info("{}: [{}]", this.getClass(), this.updateRequireId);
    }
}
