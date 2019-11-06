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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.NavigationPropertyBinding;
import com.sdl.odata.api.edm.model.Singleton;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Entity Container Impl Test.
 *
 */
public class EntityContainerImplTest {

    @Test
    public void testEntityContainerImpl() {

        EntityContainerImpl.Builder entityContainerBuilder = new EntityContainerImpl.Builder();
        entityContainerBuilder.setName("entityContainer");

        NavigationPropertyBinding navigation = new NavigationPropertyBindingImpl("path", "target");

        Singleton singleton = new SingletonImpl.Builder()
                .setName("singleton")
                .setTypeName("Singleton")
                .addNavigationPropertyBinding(navigation)
                .addNavigationPropertyBindings(Collections.singletonList(navigation))
                .build();
        entityContainerBuilder.addSingletons(Collections.singletonList(singleton));
        entityContainerBuilder.setBaseEntityContainerName("baseName");

        EntityContainerImpl entityContainer = entityContainerBuilder.build();

        assertThat(entityContainer.getName(), is(notNullValue()));
        assertThat(entityContainer.getName(), is("entityContainer"));
        assertThat(entityContainer.getSingletons().size(), is(1));
        assertThat(entityContainer.getSingleton("singleton"), is(notNullValue()));
        assertThat(entityContainer.getBaseEntityContainerName(), is("baseName"));
        assertThat(entityContainer.getEntitySet("singleton"), is(nullValue()));

    }
}
