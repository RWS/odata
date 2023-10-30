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
package com.sdl.odata.client;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.api.edm.annotations.EdmSingleton;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test to cover {@link BasicODataClientQuery}.
 */
public class BasicODataClientQueryTest {

    @Test
    public void testEntitySetQuery() {
        String resultQuery = new BasicODataClientQuery.Builder()
                .withEntityType(EntitySetSample.class)
                .withEntityKey("'someKey'")
                .withFilterMap("filterKey", "filterValue")
                .build().getQuery();
        assertEquals("EntitySetSamples('someKey')?$filter=filterKey eq 'filterValue'", resultQuery);
    }

    @Test
    public void testEntitySingletonQuery() {
        String resultQuery = new BasicODataClientQuery.Builder()
                .withEntityType(EntitySingletonSample.class)
                .withEntityKey("'someKey'")
                .withFilterMap("filterKey", "filterValue")
                .build().getQuery();
        assertEquals("EntitySingletonSample", resultQuery);
    }

    /**
     * Entity set helper class.
     */
    @EdmEntity
    @EdmEntitySet
    private class EntitySetSample {

        @EdmProperty
        private String field;
    }

    /**
     * Entity singleton helper class.
     */
    @EdmEntity
    @EdmSingleton
    private class EntitySingletonSample {

        @EdmProperty
        private String field;
    }
}
