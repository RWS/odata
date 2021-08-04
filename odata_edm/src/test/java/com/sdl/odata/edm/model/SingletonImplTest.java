/*
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

        assertThat(singleton.getName(), is("singleton"));
        assertThat(singleton.toString(), is("singleton"));
        assertThat(singleton.getTypeName(), is("Singleton"));
        assertThat(singleton.getNavigationPropertyBindings().size(), is(3));

        for (NavigationPropertyBinding property : singleton.getNavigationPropertyBindings()) {
            assertThat(property.getPath(), is("path"));
            assertThat(property.getTarget(), is("target"));
        }
    }

}
