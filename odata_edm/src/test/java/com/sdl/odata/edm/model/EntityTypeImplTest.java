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
package com.sdl.odata.edm.model;

import com.google.common.collect.ImmutableList;
import com.sdl.odata.api.edm.model.MetaType;
import com.sdl.odata.api.edm.model.PropertyRef;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
        assertThat(entityTypeImpl.getKey(), is(notNullValue()));
        assertThat(entityTypeImpl.hasStream(), is(true));
        assertThat(entityTypeImpl.isReadOnly(), is(false));
        assertThat(entityTypeImpl.getMetaType(), is(MetaType.ENTITY));
    }
}
