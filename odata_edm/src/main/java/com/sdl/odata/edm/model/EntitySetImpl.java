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

import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.NavigationPropertyBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.EntitySet}.
 *
 */
public final class EntitySetImpl implements EntitySet {
    /**
     * The Entity Set Impl Builder.
     */
    public static final class Builder {
        private String name;
        private String typeName;
        private boolean isIncludedInServiceDocument;
        private final List<NavigationPropertyBinding> navigationBuilder = new ArrayList<>();

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

        public Builder setIsIncludedInServiceDocument(boolean isInServiceDocument) {
            this.isIncludedInServiceDocument = isInServiceDocument;
            return this;
        }

        public Builder addNavigationPropertyBinding(NavigationPropertyBinding navigationPropertyBinding) {
            this.navigationBuilder.add(navigationPropertyBinding);
            return this;
        }

        public Builder addNavigationPropertyBindings(Collection<NavigationPropertyBinding> navigationPropertyBindings) {
            this.navigationBuilder.addAll(navigationPropertyBindings);
            return this;
        }

        public EntitySetImpl build() {
            return new EntitySetImpl(this);
        }
    }

    private final String name;
    private final String typeName;
    private final boolean isIncludedInServiceDocument;
    private final List<NavigationPropertyBinding> navigationPropertyBindings;

    private EntitySetImpl(Builder builder) {
        this.name = builder.name;
        this.typeName = builder.typeName;
        this.isIncludedInServiceDocument = builder.isIncludedInServiceDocument;
        this.navigationPropertyBindings = Collections.unmodifiableList(builder.navigationBuilder);
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
    public boolean isIncludedInServiceDocument() {
        return isIncludedInServiceDocument;
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
