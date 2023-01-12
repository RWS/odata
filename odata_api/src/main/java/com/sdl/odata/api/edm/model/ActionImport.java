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
 * Interface represents OData ActionImport.
 *
 * @see
 * <a href="http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793994">
 *     OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 13.5</a>
 */
public interface ActionImport {

    /**
     * Returns the name of the ActionImport.
     *
     * @return the name of the ActionImport.
     */
    String getName();

    /**
     * Returns the Action associated with the ActionImport.
     *
     * @return Action associated with the ActionImport
     */
    Action getAction();

    /**
     * Returns the ReturnType of the action specified in the Action attribute.
     *
     * @return ReturnType of the action specified in the Action attribute
     */
    EntitySet getEntitySet();

    /**
     * Returns Java Class associated with this action import.
     *
     * @return The Java Class associated with this action import.
     */
    Class<?> getJavaClass();
}
