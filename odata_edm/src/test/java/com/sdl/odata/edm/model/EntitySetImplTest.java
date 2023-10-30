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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * The Entity Set Impl Test.
 */
public class EntitySetImplTest {
    @Test
    public void testEntitySetImpl() {
        EntitySetImpl.Builder builder = new EntitySetImpl.Builder();

        builder.setName("name");
        builder.setTypeName("typeName");
        builder.setIsIncludedInServiceDocument(true);

        NavigationPropertyBindingImpl propertyBinding = new NavigationPropertyBindingImpl("navPath", "navTarget");

        builder.addNavigationPropertyBinding(propertyBinding);

        EntitySetImpl entitySet = builder.build();
        assertThat(entitySet.getName(), is("name"));
        assertThat(entitySet.getTypeName(), is("typeName"));
        assertThat(entitySet.isIncludedInServiceDocument(), is(true));
        assertThat(entitySet.getNavigationPropertyBindings(), is(notNullValue()));
        assertThat(entitySet.getNavigationPropertyBindings().size(), is(1));
        assertThat(entitySet.toString(), is("name"));
    }

}
