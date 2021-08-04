/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.NavigationPropertyBinding;
import com.sdl.odata.api.edm.model.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.Singleton}.
 *
 */
public final class SingletonImpl implements Singleton {
    /**
     * Singleton Builder.
     */
    public static final class Builder {
        private String name;
        private String typeName;
        private final List<NavigationPropertyBinding> navPropertyBindBuilder = new ArrayList<>();

        public Builder setName(String builderName) {
            this.name = builderName;
            return this;
        }

        public String getName() {
            return name;
        }

        public Builder setTypeName(String builderTypeName) {
            this.typeName = builderTypeName;
            return this;
        }

        public Builder addNavigationPropertyBinding(NavigationPropertyBinding navigationPropertyBinding) {
            this.navPropertyBindBuilder.add(navigationPropertyBinding);
            return this;
        }

        public Builder addNavigationPropertyBindings(Collection<NavigationPropertyBinding> navigationPropertyBindings) {
            this.navPropertyBindBuilder.addAll(navigationPropertyBindings);
            return this;
        }

        public SingletonImpl build() {
            return new SingletonImpl(this);
        }
    }

    private final String name;
    private final String typeName;
    private final List<NavigationPropertyBinding> navigationPropertyBindings;

    private SingletonImpl(Builder builder) {
        this.name = builder.name;
        this.typeName = builder.typeName;
        this.navigationPropertyBindings = Collections.unmodifiableList(builder.navPropertyBindBuilder);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public List<NavigationPropertyBinding> getNavigationPropertyBindings() {
        return navigationPropertyBindings;
    }

    @Override
    public String toString() {
        return name;
    }
}
