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
package com.sdl.odata.util.edm;

import com.sdl.odata.util.PrimitiveEntityDataModel;
import org.junit.jupiter.api.Test;

import static com.sdl.odata.util.edm.EntityDataModelUtil.isCollection;
import static com.sdl.odata.util.edm.EntityDataModelUtil.pluralize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Entity Data model Util Test.
 */
public class EntityDataModelUtilTest {

    @Test
    public void testPluralize() {
        assertEquals("Buses", pluralize("Bus"));
        assertEquals("Hashes", pluralize("Hash"));
        assertEquals("Potatoes", pluralize("Potato"));
        assertEquals("Capabilities", pluralize("Capability"));
        assertEquals("Days", pluralize("Day"));
        assertEquals("WebApplications", pluralize("WebApplication"));
    }

    @Test
    public void testPluralizeNull() {
        assertThrows(IllegalArgumentException.class, () -> pluralize(null));
    }

    @Test
    public void testPrimitiveClassloading() {
        assertFalse(isCollection(new PrimitiveEntityDataModel(), "Edm.String"));
        assertTrue(isCollection(new PrimitiveEntityDataModel(), "com.sdl.odata.util.PrimitiveEntityDataModel"));
    }
}
