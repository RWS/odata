/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * Indicates that the class is an entity type in the OData entity data model.
 * <p>
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 8
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EdmEntity {

    /**
     * The name of the entity type. If not specified, the name of the class is used.
     *
     * @return The name of the entity type.
     */
    String name() default "";

    /**
     * The namespace of the schema that the entity type is in. If not specified, the name of the package that contains
     * the entity class is used.
     *
     * @return The namespace of the schema that the entity type is in.
     */
    String namespace() default "";

    /**
     * The key of the entity, consisting of an array of the names of properties that form the key. This attribute is
     * provided for convenience, which can be used instead of the more verbose {@code keyRef}. An entity must have a
     * key which consists of at least one of the properties of the entity; either {@code key} or {@code keyRef} must
     * be specified on an entity.
     *
     * @return The key of the entity.
     */
    String[] key() default { };

    /**
     * The key of the entity, consisting of an array of {@code EdmPropertyRef} instances. An entity must have a key
     * which consists of at least one of the properties in the entity; either {@code keyRef} or {@code key} must be
     * specified on an entity.
     *
     * @return The key of the entity.
     */
    EdmPropertyRef[] keyRef() default { };

    /**
     * Specifies whether the entity type is an open type.
     *
     * @return {@code true} if this is an open type, {@code false} otherwise.
     */
    boolean open() default false;

    /**
     * The container name that the entity type has. If not specified, the namespace of the entity type is used.
     * NOTE: The container name of the first entity class will be used for the whole entity data model. In case where
     * other entity classes have container name different than the first one, this container name will not be taken into
     * account.
     *
     * @return container name of the entity type.
     */
    String containerName() default "";
}
