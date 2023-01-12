/*
 * Copyright (c) 2014-2023 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.api.edm.model;

/**
 * OData on delete action.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.3
 *
 */
public enum OnDeleteAction {
    /**
     * Cascade action.
     */
    CASCADE("Cascade"),
    /**
     * Nothing for action.
     */
    NONE("None"),
    /**
     * Set NULL action.
     */
    SET_NULL("SetNull"),
    /**
     * Set Default value action.
     */
    SET_DEFAULT("SetDefault");

    private final String name;

    OnDeleteAction(String name) {
        this.name = name;
    }

    /**
     * Returns the {@code OnDeleteAction} for the specified name.
     *
     * @param name The name of the {@code OnDeleteAction} to find.
     * @return The {@code OnDeleteAction} for the specified name.
     * @throws java.lang.IllegalArgumentException If the name does not match an {@code OnDeleteAction}.
     */
    public static OnDeleteAction forName(String name) {
        for (OnDeleteAction onDeleteAction : OnDeleteAction.values()) {
            if (onDeleteAction.name.equals(name)) {
                return onDeleteAction;
            }
        }

        throw new IllegalArgumentException("Invalid on delete action name: " + name);
    }

    /**
     * Returns the name of this {@code OnDeleteAction}.
     *
     * @return The name of this {@code OnDeleteAction}.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
