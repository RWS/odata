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

import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.StructuredType}.
 *
 */
public abstract class StructuredTypeImpl extends DerivableTypeImpl implements StructuredType {
    /**
     * Structured Type Builder.
     * @param <B> builder
     */
    public abstract static class Builder<B extends Builder> extends DerivableTypeImpl.Builder<B> {
        private final B self;

        private final Map<String, StructuralProperty> structuralPropBuilder = new LinkedHashMap<>();

        private boolean isOpen;

        protected Builder() {
            this.self = (B) this;
        }

        public B addStructuralProperty(StructuralProperty structuralProperty) {
            this.structuralPropBuilder.put(structuralProperty.getName(), structuralProperty);
            return self;
        }

        public B addStructuralProperties(Iterable<? extends StructuralProperty> structuralProperties) {
            for (StructuralProperty structuralProperty : structuralProperties) {
                addStructuralProperty(structuralProperty);
            }
            return self;
        }

        public B setIsOpen(boolean isOpenForType) {
            this.isOpen = isOpenForType;
            return self;
        }
    }

    private final Map<String, StructuralProperty> structuralProperties;
    private final boolean isOpen;

    protected StructuredTypeImpl(Builder builder) {
        super(builder);
        this.structuralProperties = Collections.unmodifiableMap(builder.structuralPropBuilder);
        this.isOpen = builder.isOpen;
    }

    @Override
    public List<StructuralProperty> getStructuralProperties() {
        return new ArrayList<>(structuralProperties.values());
    }

    @Override
    public StructuralProperty getStructuralProperty(String name) {
        return structuralProperties.get(name);
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }
}
