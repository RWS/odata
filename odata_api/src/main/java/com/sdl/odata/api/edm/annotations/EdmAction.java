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
package com.sdl.odata.api.edm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the class is an Action of an entity data model.
 * <p>
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793961">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 12.1</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmAction {

    /**
     * The name of the Action. If not specified, the name of the class is used.
     * <p>
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793962">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.1.1</a>
     *
     * @return The name of the Action.
     */
    String name() default "";

    /**
     * The namespace of the schema that the action is in. If not specified, the name of the package that contains
     * the action class is used.
     *
     * @return The namespace of the schema that the action is in.
     */
    String namespace() default "";

    /**
     * The IsBound attribute of Action.
     * <p>
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793964">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.1.2</a>
     *
     * @return {@code true} if the Action is bound
     */
    boolean isBound() default false;

    /**
     * The entity set path. The first segment of the entity set path is the name of the binding parameter.
     * The remaining segments of the entity set path represent navigation segments or type casts.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793965">
     *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.1.3</a>
     *
     * @return the entity set path
     */
    String entitySetPath() default "";
}
