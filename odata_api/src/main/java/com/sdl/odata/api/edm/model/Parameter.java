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
package com.sdl.odata.api.edm.model;

import java.lang.reflect.Field;

/**
 * Interface represents an OData Parameter.
 *
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793975">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 12.4</a>
 */
public interface Parameter extends Facets {

    /**
     * Returns the name of the parameter.
     *
     * @return The name of the parameter.
     */
    String getName();

    /**
     * Returns the fully qualified name of the type of value that can be passed to the parameter.
     *
     * @return The fully qualified name of the type of value that can be passed to the parameter.
     */
    String getType();

    /**
     * Returns {@code true} if empty values are allowed for this parameter, {@code false} otherwise.
     *
     * @return {@code true} if empty values are allowed for this parameter, {@code false} otherwise.
     */
    boolean isNullable();

    /**
     * Returns the Java field which is associated with this parameter.
     *
     * @return The Java field which is associated with this parameter.
     */
    Field getJavaField();
}
