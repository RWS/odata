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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.EntityType;
import com.sdl.odata.edm.model.EntitySetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static com.sdl.odata.util.edm.EntityDataModelUtil.pluralize;

/**
 * The Annotation Navigation Property Binding Factory.
 */
final class AnnotationEntitySetFactory extends AnnotationNavigationPropertyBindingFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEntitySetFactory.class);

    private final Map<String, EntitySetImpl.Builder> builders = new LinkedHashMap<>();

    public void addEntityType(Class<?> cls, EntityType entityType) {
        EdmEntitySet entitySetAnno = cls.getAnnotation(EdmEntitySet.class);
        if (entitySetAnno == null) {
            return;
        }

        String entitySetName = entitySetAnno.name();
        if (isNullOrEmpty(entitySetName)) {
            entitySetName = entitySetAnno.value();
            if (isNullOrEmpty(entitySetName)) {
                // Use automatically pluralized simple name of entity type if no name for the entity set is specified
                entitySetName = pluralize(entityType.getName());
            }
        }

        String entityTypeName = entityType.getFullyQualifiedName();
        LOG.trace("Creating EntitySet named: {} for entity type: {}", entitySetName, entityTypeName);

        builders.put(entityTypeName, new EntitySetImpl.Builder()
                .setName(entitySetName)
                .setTypeName(entityTypeName)
                .setIsIncludedInServiceDocument(entitySetAnno.includedInServiceDocument()));
    }

    public String getEntitySetName(String entityTypeName) {
        EntitySetImpl.Builder builder = builders.get(entityTypeName);
        return builder != null ? builder.getName() : null;
    }

    public List<EntitySet> build(FactoryLookup lookup) {
        List<EntitySet> listBuilder =
                builders.entrySet().stream().map(entry -> entry.getValue().addNavigationPropertyBindings(
                        createNavigationPropertyBindings(entry.getKey(), lookup)).build()).collect(Collectors.toList());
        return Collections.unmodifiableList(listBuilder);
    }
}
