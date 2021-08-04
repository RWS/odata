/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
 * Indicates that the class is an ActionImport of an entity data model.
 *
 * @see <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793994">
 * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.5</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmActionImport {

    /**
     * The name of the ActionImport. If not specified, the name of the class is used.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793995">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.5.1</a>
     *
     * @return The name of the ActionImport.
     */
    String name() default "";

    /**
     * The namespace of the schema that the action import is in. If not specified, the name of the package that contains
     * the action import class is used.
     *
     * @return The namespace of the schema that the action import is in.
     */
    String namespace() default "";

    /**
     * The qualified name for the Action attribute.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793996">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.5.2</a>
     *
     * @return The qualified name for the Action attribute
     */
    String action() default "";

    /**
     * Specifies the return type for Action attribute.
     *
     * @see
     * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793997">
     * OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 13.5.3</a>
     *
     * @return the return type for Action attribute
     */
    String entitySet() default "";
}
