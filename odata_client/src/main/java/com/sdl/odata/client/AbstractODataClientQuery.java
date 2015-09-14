/**
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
package com.sdl.odata.client;

import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmSingleton;
import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.odata.client.api.exception.ODataClientRuntimeException;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static com.sdl.odata.util.edm.EntityDataModelUtil.pluralize;

/**
 * Abstract implementation of ODataClientQuery.
 */
public abstract class AbstractODataClientQuery implements ODataClientQuery {

    /**
     * Hash.
     */
    public static final int HASH = 31;

    private Class<?> entityType;

    public Class<?> getEntityType() {
        return entityType;
    }

    protected void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public String getEdmEntityName() {
        EdmEntitySet edmEntitySet = entityType.getAnnotation(EdmEntitySet.class);

        if (edmEntitySet != null) {
            String entitySetName = edmEntitySet.name();
            if (isNullOrEmpty(entitySetName)) {
                entitySetName = edmEntitySet.value();
                if (isNullOrEmpty(entitySetName)) {
                    // Use automatically pluralized simple name of entity type if
                    // no name for the entity set is specified
                    entitySetName = pluralize(entityType.getSimpleName());
                }
            }
            return entitySetName;
        } else {
            // Check for Singleton entity in the container
            EdmSingleton singleton = entityType.getAnnotation(EdmSingleton.class);

            if (singleton == null) {
                throw new ODataClientRuntimeException(
                        "There is no an odata endpoint for provided class. " +
                                "@EdmEntitySet or @EdmSingleton annotation is not present on this type");
            }

            String entityName = entityType.getSimpleName();

            if (isNullOrEmpty(entityName)) {
                entityName = singleton.value();
            }
            return entityName;
        }
    }

    // extracted Guava code

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

}
