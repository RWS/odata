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

import java.util.List;

import static com.sdl.odata.util.ReferenceUtil.isNullOrEmpty;
import static com.sdl.odata.util.edm.EntityDataModelUtil.pluralize;
import static java.lang.String.format;

/**
 * Abstract implementation of ODataClientQuery.
 */
public abstract class AbstractODataClientQuery implements ODataClientQuery {

    /**
     * Hash.
     */
    public static final int HASH = 31;

    private Class<?> entityType;
    private String entityKey;
    private boolean isSingletonEntity;
    private boolean streaming = false;

    @Override
    public Class<?> getEntityType() {
        return entityType;
    }

    protected void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    protected void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    @Override
    public String getEdmEntityName() {
        if (entityType.getName().equals(List.class.getName())) {
            return entityType.getSimpleName();
        }

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
            return appendEntityKeySuffix(entitySetName);
        } else {
            // Check for Singleton entity in the container
            EdmSingleton singleton = entityType.getAnnotation(EdmSingleton.class);

            if (singleton == null) {
                throw new ODataClientRuntimeException(
                        "There is no an odata endpoint for provided class. " +
                                "@EdmEntitySet or @EdmSingleton annotation is not present on this type");
            }
            isSingletonEntity = true;
            String entityName = entityType.getSimpleName();

            if (isNullOrEmpty(entityName)) {
                entityName = singleton.value();
            }
            return entityName;
        }
    }

    private String appendEntityKeySuffix(String entityName) {
        return entityKey == null ? entityName : format("%s(%s)", entityName, entityKey);
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    protected boolean isSingletonEntity() {
        return isSingletonEntity;
    }

    @Override
    public String getCacheKey() {
        return getQuery();
    }

    @Override
    public boolean isStreamingSupport() {
        return streaming;
    }

    public void setStreamingSupport(boolean streamingSupport) {
        this.streaming = streamingSupport;
    }
}
