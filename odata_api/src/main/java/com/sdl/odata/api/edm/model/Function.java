/**
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

import java.util.Set;

/**
 * Interface represents an OData Function.
 *
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793966">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.2</a>
 */
public interface Function {

    /**
     * Returns the namespace of the Function.
     *
     * @return The namespace of the Function.
     */
    String getNamespace();

    /**
     * Returns the name of the Function.
     *
     * @return the name of the Function.
     */
    String getName();


    /**
     * Returns {@code boolean} value of IsBound attribute of the Function.
     *
     * @return {@code true} if the Function is bound
     */
    boolean isBound();


    /**
     * Returns {@code boolean} value of IsComposable attribute of the Function.
     *
     * @return {@code true} if the Function is composable
     */
    boolean isComposable();

    /**
     * Returns EntitySet Path of the Function.
     *
     * @return EntitySet Path of the Function
     */
    String getEntitySetPath();

    /**
     * Returns Return Type attribute of the Function.
     *
     * @return ReturnType attribute of the Function
     */
    String getReturnType();

    /**
     * Returns set of parameters of the Function.
     *
     * @return set of parameters of the Function
     */
    Set<Parameter> getParameters();

    /**
     * Returns a Java Class associated with this function.
     *
     * @return a Java Class associated with this function.
     */
    Class<?> getJavaClass();
}
