/**
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
package com.sdl.odata.api.edm.model;

/**
 * Interface represents OData FunctionImport.
 *
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793998">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 13.6</a>
 */
public interface FunctionImport {

    /**
     * Returns the name of the FunctionImport.
     *
     * @return the name of the FunctionImport.
     */
    String getName();

    /**
     * Returns the Function associated with the FunctionImport.
     *
     * @return Function associated with the FunctionImport
     */
    Function getFunction();

    /**
     * Returns the ReturnType of the function specified in the Function attribute.
     *
     * @return ReturnType of the function specified in the Function attribute.
     */
    EntitySet getEntitySet();

    /**
     * Returns the IncludeInServiceDocument of FunctionImport.
     *
     * @return IncludeInServiceDocument of FunctionImport
     */
    boolean isIncludeInServiceDocument();

    /**
     * Returns a Java Class associated with this function.
     *
     * @return a Java Class associated with this function.
     */
    Class<?> getJavaClass();
}
