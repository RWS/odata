/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.EntitySet;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.StructuredType;

/**
 * Factory Lookup.
 * It is used to look up information in the not-yet-built entity data model.
 */
interface FactoryLookup {

    /**
     * Returns found structured type by fully qualified type name.
     * @param fullyQualifiedTypeName    fully qualified type name
     * @return                          structured type
     */
    StructuredType getStructuredType(String fullyQualifiedTypeName);

    /**
     * Returns found entity set or singleton name by entity type name.
     * @param entityTypeName    entity type name
     * @return                  found entity set
     */
    String getEntitySetOrSingletonName(String entityTypeName);

    /**
     * Returns found function by specified function name.
     * @param functionName    function name
     * @return                found function
     */
    Function getFunction(String functionName);

    /**
     * Returns found action by specified action name.
     * @param actionName    action name
     * @return              found action
     */
    Action getAction(String actionName);

    /**
     * Returns found entity set by specified entity set name.
     * @param entitySetName    entity set name
     * @return                 found entity set
     */
    EntitySet getEntitySet(String entitySetName);
}
