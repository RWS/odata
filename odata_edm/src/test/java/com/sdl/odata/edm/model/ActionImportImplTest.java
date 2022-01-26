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

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test for ActionImportImpl class.
 */
public class ActionImportImplTest {

    @Test
    public void testActionImportImpl() {
        ActionImportImpl.Builder builder = new ActionImportImpl.Builder();
        builder.setName("someActionImport")
                .setEntitySet(new EntitySetImpl.Builder().setName("someEntitySet").build())
                .setAction(new ActionImpl.Builder().setName("someActionName").build());

        ActionImportImpl actionImport = builder.build();
        assertThat(actionImport.getName(), is("someActionImport"));
        assertThat(actionImport.getEntitySet().getName(), is("someEntitySet"));
        assertThat(actionImport.getAction().getName(), is("someActionName"));
    }
}
