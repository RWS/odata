/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.edm.model;

import com.google.common.collect.ImmutableList;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.PropertyRef;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Entity Type Impl Test.
 */
public class EntityTypeImplTest {

    @Test
    public void testEntityTypeImpl() {
        EntityTypeImpl.Builder builder = new EntityTypeImpl.Builder();
        builder.setHasStream(true);
        builder.setIsReadOnly(false);
        PropertyRefImpl propertyRef = new PropertyRefImpl("path", "alias");
        ImmutableList<PropertyRef> propertyRefs = new ImmutableList.Builder<PropertyRef>().add(propertyRef).build();
        builder.setKey(new KeyImpl(propertyRefs));

        EntityTypeImpl entityTypeImpl = builder.build();
        assertNotNull(entityTypeImpl.getKey());
        assertTrue(entityTypeImpl.hasStream());
        assertFalse(entityTypeImpl.isReadOnly());
        assertEquals(MetaType.ENTITY, entityTypeImpl.getMetaType());
    }
}
