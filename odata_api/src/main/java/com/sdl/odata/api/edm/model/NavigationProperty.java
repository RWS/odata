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
package com.sdl.odata.api.edm.model;

import java.util.List;

/**
 * OData navigation property.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 7
 *
 */
public interface NavigationProperty extends StructuralProperty {

    /**
     * For bidirectional relations, returns the name of the property in the target type that refers back to the type
     * containing this navigation property.
     *
     * @return The name of the property in the target type that refers back to the type containing this navigation
     *      property.
     */
    String getPartnerName();

    /**
     * Returns {@code true} if this navigation property is a containment navigation property, {@code false} otherwise.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.1.5
     *
     * @return {@code true} if this navigation property is a containment navigation property, {@code false} otherwise.
     */
    boolean containsTarget();

    List<ReferentialConstraint> getReferentialConstraints();

    List<OnDeleteAction> getOnDeleteActions();
}
