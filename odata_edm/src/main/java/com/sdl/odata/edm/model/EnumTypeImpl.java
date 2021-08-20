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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.EnumMember;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.PrimitiveType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.EnumType}.
 *
 */
public final class EnumTypeImpl extends TypeImpl implements EnumType {
    /**
     * Enum Type Impl Builder.
     */
    public static final class Builder extends TypeImpl.Builder<Builder> {
        private PrimitiveType underlyingType = PrimitiveType.INT32;
        private boolean isFlags;
        private final Map<String, EnumMember> memberBuilder = new LinkedHashMap<>();

        public Builder setUnderlyingType(PrimitiveType underlyingTypeForEnumType) {
            this.underlyingType = underlyingTypeForEnumType;
            return this;
        }

        public Builder setIsFlags(boolean isFlagsForEnumType) {
            this.isFlags = isFlagsForEnumType;
            return this;
        }

        public Builder addMember(EnumMember member) {
            this.memberBuilder.put(member.getName(), member);
            return this;
        }

        public Builder addMembers(Iterable<EnumMember> members) {
            for (EnumMember member : members) {
                addMember(member);
            }
            return this;
        }

        public EnumTypeImpl build() {
            return new EnumTypeImpl(this);
        }
    }

    private final PrimitiveType underlyingType;
    private final boolean isFlags;
    private final Map<String, EnumMember> members;

    private EnumTypeImpl(Builder builder) {
        super(builder);
        this.underlyingType = builder.underlyingType;
        this.isFlags = builder.isFlags;
        this.members = Collections.unmodifiableMap(builder.memberBuilder);
    }

    @Override
    public MetaType getMetaType() {
        return MetaType.ENUM;
    }

    @Override
    public PrimitiveType getUnderlyingType() {
        return underlyingType;
    }

    @Override
    public boolean isFlags() {
        return isFlags;
    }

    @Override
    public List<EnumMember> getMembers() {
        return new ArrayList<>(members.values());
    }

    @Override
    public EnumMember getMember(String name) {
        return members.get(name);
    }

    @Override
    public EnumMember getMember(long value) {
        for (EnumMember member : members.values()) {
            if (member.getValue() == value) {
                return member;
            }
        }
        return null;
    }
}
