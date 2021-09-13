/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import java.util.Set;

/**
 * Interface represents an OData Action.
 *
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793960">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.1</a>
 */
public interface Action {

    /**
     * Returns the name of the Action.
     *
     * @return the name of the Action.
     */
    String getName();

    /**
     * Returns the namespace of the Action.
     *
     * @return The namespace of the Action.
     */
    String getNamespace();

    /**
     * Returns {@code boolean} value of bound attribute of the Action.
     *
     * @return {@code true} if the Action is bound
     */
    boolean isBound();

    /**
     * Returns EntitySet Path of the Action.
     *
     * @return EntitySet Path of the Action
     */
    String getEntitySetPath();

    /**
     * Returns Return Type attribute of the Action.
     *
     * @return ReturnType attribute of the Action
     */
    String getReturnType();

    /**
     * Returns set of parameters of the Action.
     *
     * @return set of parameters of the Action
     */
    Set<Parameter> getParameters();

    /**
     * Returns Java Class associated with this action.
     *
     * @return The Java Class associated with this action.
     */
    Class<?> getJavaClass();
}
