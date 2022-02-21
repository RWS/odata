/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * Indicates that the class is an FunctionImport of an entity data model.
 *
 * @see <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793998">
 * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.6</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmFunctionImport {

    /**
     * The name of the FunctionImport. If not specified, the name of the class is used.
     *
     * @return The name of the FunctionImport.
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793999">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.6.1</a>
     */
    String name() default "";

    /**
     * The qualified name for the Function attribute.
     *
     * @return The qualified name for the Function attribute
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372794000">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.6.2</a>
     */
    String function() default "";

    /**
     * Specifies the return type for Function attribute.
     *
     * @return the return type for Function attribute
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372794001">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.6.3</a>
     */
    String entitySet() default "";

    /**
     * Indicates whether the FunctionImport is advertised in the service document.
     *
     * @return {@code true} if the FunctionImport is advertised in the service document
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372794002">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.6.4</a>
     */
    boolean includeInServiceDocument() default false;

    /**
     * The namespace of the schema that the function import is in. If not specified, the name of the package that
     * contains the function import class is used.
     *
     * @return The namespace of the schema that the function import is in.
     */
    String namespace() default "";
}
