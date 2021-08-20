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

import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.OnDeleteAction;
import com.sdl.odata.api.edm.model.ReferentialConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link com.sdl.odata.api.edm.model.NavigationProperty}.
 *
 */
public final class NavigationPropertyImpl extends StructuralPropertyImpl implements NavigationProperty {
    /**
     * Navigation Property Builder.
     */
    public static final class Builder extends StructuralPropertyImpl.Builder<Builder> {
        private String partnerName;
        private boolean containsTarget;
        private final List<ReferentialConstraint> constraintsBuilder = new ArrayList<>();
        private final List<OnDeleteAction> onDeleteActionsBuilder = new ArrayList<>();

        public Builder setPartnerName(String builderPartnerName) {
            this.partnerName = builderPartnerName;
            return this;
        }

        public Builder setContainsTarget(boolean builderContainsTarget) {
            this.containsTarget = builderContainsTarget;
            return this;
        }

        public Builder addReferentialConstraint(ReferentialConstraint referentialConstraint) {
            this.constraintsBuilder.add(referentialConstraint);
            return this;
        }

        public Builder addReferentialConstraints(Collection<ReferentialConstraint> referentialConstraints) {
            this.constraintsBuilder.addAll(referentialConstraints);
            return this;
        }

        public Builder addOnDeleteAction(OnDeleteAction onDeleteAction) {
            this.onDeleteActionsBuilder.add(onDeleteAction);
            return this;
        }

        public Builder addOnDeleteActions(Collection<OnDeleteAction> onDeleteActions) {
            this.onDeleteActionsBuilder.addAll(onDeleteActions);
            return this;
        }

        public NavigationPropertyImpl build() {
            return new NavigationPropertyImpl(this);
        }
    }

    private final String partnerName;
    private final boolean containsTarget;
    private final List<ReferentialConstraint> referentialConstraints;
    private final List<OnDeleteAction> onDeleteActions;

    private NavigationPropertyImpl(Builder builder) {
        super(builder);
        this.partnerName = builder.partnerName;
        this.containsTarget = builder.containsTarget;
        this.referentialConstraints = Collections.unmodifiableList(builder.constraintsBuilder);
        this.onDeleteActions = Collections.unmodifiableList(builder.onDeleteActionsBuilder);
    }

    @Override
    public String getPartnerName() {
        return partnerName;
    }

    @Override
    public boolean containsTarget() {
        return containsTarget;
    }

    @Override
    public List<ReferentialConstraint> getReferentialConstraints() {
        return referentialConstraints;
    }

    @Override
    public List<OnDeleteAction> getOnDeleteActions() {
        return onDeleteActions;
    }
}
