/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmSingleton;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.api.edm.model.Singleton;
import com.sdl.odata.edm.model.SingletonImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Singleton Factory.
 */
final class AnnotationSingletonFactory extends AnnotationNavigationPropertyBindingFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationSingletonFactory.class);

    private final Map<String, SingletonImpl.Builder> builders = new LinkedHashMap<>();

    public void addEntityType(Class<?> cls, EntityType entityType) {
        EdmSingleton singletonAnno = cls.getAnnotation(EdmSingleton.class);
        if (singletonAnno == null) {
            return;
        }

        String singletonName = singletonAnno.name();
        if (isNullOrEmpty(singletonName)) {
            singletonName = singletonAnno.value();
            if (isNullOrEmpty(singletonName)) {
                // Use simple name of entity type if no name for the singleton is specified
                singletonName = entityType.getName();
            }
        }

        String entityTypeName = entityType.getFullyQualifiedName();
        LOG.debug("Creating Singleton named: {} for entity type: {}", singletonName, entityTypeName);

        builders.put(entityTypeName, new SingletonImpl.Builder()
                .setName(singletonName)
                .setTypeName(entityTypeName));
    }


    public String getSingletonName(String entityTypeName) {
        SingletonImpl.Builder builder = builders.get(entityTypeName);
        return builder != null ? builder.getName() : null;
    }

    public List<Singleton> build(FactoryLookup lookup) {
        List<Singleton> listBuilder =
                builders.entrySet().stream().map(entry -> entry.getValue().addNavigationPropertyBindings(
                        createNavigationPropertyBindings(entry.getKey(), lookup)).build()).collect(Collectors.toList());
        return Collections.unmodifiableList(listBuilder);
    }
}
