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
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SingletonImpl}.
 *
 */
public class SingletonImplTest {

    @Test
    public void testSingletonImpl() {
        NavigationPropertyBinding navigation = new NavigationPropertyBindingImpl("path", "target");

        SingletonImpl singleton = new SingletonImpl.Builder()
                .setName("singleton")
                .setTypeName("Singleton")
                .addNavigationPropertyBinding(navigation)
                .addNavigationPropertyBindings(Arrays.asList(navigation, navigation))
                .build();

        assertEquals("singleton", singleton.getName());
        assertEquals("singleton", singleton.toString());
        assertEquals("Singleton", singleton.getTypeName());
        assertEquals(3, singleton.getNavigationPropertyBindings().size());

        for (NavigationPropertyBinding property : singleton.getNavigationPropertyBindings()) {
            assertEquals("path", property.getPath());
            assertEquals("target", property.getTarget());
        }
    }

}
