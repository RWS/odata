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
package com.sdl.odata.edm.model;

import com.sdl.odata.api.edm.model.NavigationPropertyBinding;
import com.sdl.odata.api.edm.model.Singleton;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        assertNotNull(entityContainer.getName());
        assertEquals("entityContainer", entityContainer.getName());
        assertEquals(1, entityContainer.getSingletons().size());
        assertNotNull(entityContainer.getSingleton("singleton"));
        assertEquals("baseName", entityContainer.getBaseEntityContainerName());
        assertNull(entityContainer.getEntitySet("singleton"));
    }
}
