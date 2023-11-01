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
package com.sdl.odata;

/**
 * OData JSON Constants according to OData spec v.4.
 *
 */
public final class JsonConstants {
    private JsonConstants() {
    }

    /**
     * Context.
     */
    public static final String CONTEXT = "@odata.context";
    /**
     * ID.
     */
    public static final String ID = "@odata.id";
    /**
     * Type.
     */
    public static final String TYPE = "@odata.type";
    /**
     * Type.
     */
    public static final String COUNT = "@odata.count";
    /**
     * Value.
     */
    public static final String VALUE = "value";
    /**
     * Metadata.
     */
    public static final String METADATA = "$metadata";
    /**
     * Collection.
     */
    public static final String COLLECTION = "#Collection";

    /**
     * Name.
     */
    public static final String NAME = "name";
    /**
     * Kind.
     */
    public static final String KIND = "kind";
    /**
     * URL.
     */
    public static final String URL = "url";
    /**
     * Singleton.
     */
    public static final String SINGLETON = "Singleton";
    /**
     * Entity Set.
     */
    public static final String ENTITY_SET = "EntitySet";
}
