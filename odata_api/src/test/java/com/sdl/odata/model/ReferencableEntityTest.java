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
package com.sdl.odata.model;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ReferencableEntity}.
 */
public class ReferencableEntityTest {

    @Test
    public void testNoReferenceStringIsNotReference() {
        ReferencableEntity ref = new ReferencableEntity();
        ref.setReferenceString(null);

        assertFalse(ref.isReference());
    }

    @Test
    public void testEmptyStringNotReference() {
        ReferencableEntity ref = new ReferencableEntity();
        ref.setReferenceString("");

        assertFalse(ref.isReference());
    }

    @Test
    public void testNotEmptyStringReference() {
        ReferencableEntity ref = new ReferencableEntity();
        ref.setReferenceString("a");

        assertTrue(ref.isReference());
    }

}
