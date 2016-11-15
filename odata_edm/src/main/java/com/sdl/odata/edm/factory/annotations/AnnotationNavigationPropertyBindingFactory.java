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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.NavigationPropertyBinding;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.edm.model.NavigationPropertyBindingImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;

/**
 * The Annotation Navigation Property Binding Factory.
 */
abstract class AnnotationNavigationPropertyBindingFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationNavigationPropertyBindingFactory.class);

    public List<NavigationPropertyBinding> createNavigationPropertyBindings(String typeName, FactoryLookup lookup) {
        List<NavigationPropertyBinding> result = new ArrayList<>();
        createNavigationPropertyBindings("", typeName, lookup, result);
        return result;
    }

    private void createNavigationPropertyBindings(String pathPrefix, String typeName, FactoryLookup lookup,
                                                  List<NavigationPropertyBinding> result) {
        StructuredType structuredType = lookup.getStructuredType(typeName);
        if (structuredType != null) {
            for (StructuralProperty property : structuredType.getStructuralProperties()) {
                String propertyTypeName = property.isCollection() ?
                        property.getElementTypeName() : property.getTypeName();

                if (property instanceof NavigationProperty) {
                    String path = pathPrefix + property.getName();
                    String target = lookup.getEntitySetOrSingletonName(propertyTypeName);

                    if (!isNullOrEmpty(target)) {
                        result.add(new NavigationPropertyBindingImpl(path, target));
                    } else {
                        LOG.debug("Target not found for navigation property binding in: {}, path={}", typeName, path);
                    }
                } else {
                    // Non-navigation property. The type that this property refers to must not be an entity type
                    // (because in that case a navigation property should have been used).
                    final StructuredType propertyType = lookup.getStructuredType(propertyTypeName);
                    if (propertyType != null && propertyType.getMetaType() == MetaType.ENTITY) {
                        throw new IllegalArgumentException("Found a non-navigation property that refers to an " +
                                "entity type. This is not allowed. Properties that refer to an entity type must be " +
                                "navigation properties: " + property + " in " + typeName);
                    }

                    // Recursively create navigation property bindings for nested structured types
                    createNavigationPropertyBindings(property.getName() + "/", propertyTypeName, lookup, result);
                }
            }
        }
    }
}
