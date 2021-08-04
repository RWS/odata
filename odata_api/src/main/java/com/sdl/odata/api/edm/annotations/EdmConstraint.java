/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.api.edm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Edm Constraint.
 * <p>
 * The navigation property whose Type attributes specifies a single entity type.
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 7.2
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ })
public @interface EdmConstraint {

    /**
     * The property attribute specifies the property that takes part in the referential constraint
     * on the dependent entity type.
     *
     * @return property
     */
    String property();

    /**
     * The referencedProperty specified the corresponding property of the principal entity type.
     *
     * @return referencedProperty
     */
    String referencedProperty();
}
