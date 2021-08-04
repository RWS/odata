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
package com.sdl.odata.client;

import com.sdl.odata.client.api.ODataActionClientQuery;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link ActionImportClientQuery}.
 */
public class ActionImportClientQueryTest {

    @Test
    public void testActionImportClientQuery() {
        ODataActionClientQuery actionClientQuery = new ActionImportClientQuery.Builder()
                .withActionName("SomeCustomAction")
                .withReturnType(List.class)
                .withActionParameter("param1", "\"value1\"")
                .withActionParameter("param2", "\"value2\"")
                .withActionParameter("param3", "\"value3\"")
                .build();

        assertEquals("{\"param1\":\"value1\",\"param2\":\"value2\",\"param3\":\"value3\"}",
                actionClientQuery.getActionRequestBody());
        assertEquals(List.class, actionClientQuery.getEntityType());
        assertEquals("SomeCustomAction", actionClientQuery.getQuery());
    }
}
