/*
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
package com.sdl.odata.edm.registry;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link com.sdl.odata.api.edm.registry.ODataEdmRegistry}.
 */
@Component
public class ODataEdmRegistryImpl implements ODataEdmRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(ODataEdmRegistryImpl.class);

    private final List<Class<?>> classes = new ArrayList<>();

    private EntityDataModel entityDataModel;

    @Override
    public synchronized void registerClasses(List<Class<?>> registerClasses) {
        LOG.debug("registerClasses: classes={}", registerClasses);
        this.classes.addAll(registerClasses);
        entityDataModel = null;
    }

    @Override
    public synchronized EntityDataModel getEntityDataModel() throws ODataException {
        if (entityDataModel == null) {
            AnnotationEntityDataModelFactory factory = new AnnotationEntityDataModelFactory();
            classes.forEach(factory::addClass);

            LOG.info("Building EntityDataModel");
            entityDataModel = factory.buildEntityDataModel();
        }

        return entityDataModel;
    }
}
