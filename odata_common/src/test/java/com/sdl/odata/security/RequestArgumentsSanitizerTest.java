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
package com.sdl.odata.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static com.sdl.odata.security.RequestArgumentsSanitizer.getSanitizedCopy;
import static com.sdl.odata.security.RequestArgumentsSanitizer.sanitize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RequestArgumentsSanitizerTest {
    @Mock
    private Logger mockLogger;

    private static class ToSanitize {
        private String name;
        private int age;

        ToSanitize(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "ToSanitize{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @Test
    public void testSanitizeNull() {
        String sanitized = String.valueOf(sanitize(null));
        assertEquals("null", sanitized);
    }

    @Test
    public void testSanitizeSingleParam() {
        String sanitized = String.valueOf(sanitize(
                new ToSanitize("Robbie\nWilliams%0d%0A", 34)));
        assertEquals("ToSanitize{name='Robbie⬎Williams⤵⤵', age=34}", sanitized);
    }

    @Test
    public void testSanitizePrimitiveParam() {
        String sanitized = String.valueOf(sanitize("Robbie\tWilliams\r\n\r"));
        assertEquals("Robbie\tWilliams⬎⬎⬎", sanitized);
    }

    @Test(expected = IllegalStateException.class)
    public void testSanitizeCopyNull() {
        getSanitizedCopy(null);
    }

    @Test
    public void testSanitizeCopyLastElementNull() {
        Object[] sanitized = getSanitizedCopy(1, 2, null);
        assertArrayEquals(new String[] {"1", "2", "null"}, sanitized);
    }

    @Test
    public void testSanitizeCopyNullArg() {
        Object[] sanitized = getSanitizedCopy("Robbie\n", null, "Williams\r");
        assertArrayEquals(new String[] {"Robbie⬎", "null", "Williams⬎"}, sanitized);
    }

    @Test
    public void testSanitizeCopy() {
        Object[] sanitized = getSanitizedCopy("Robbie\n", 34, "Williams\r");
        assertArrayEquals(new String[] {"Robbie⬎", "34", "Williams⬎"}, sanitized);
    }
}
