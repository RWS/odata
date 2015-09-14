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
package com.sdl.odata.client.property;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertNull;

/**
 * Unit test for {@link PropertyUtils} class.
 */
public class PropertyUtilsTest {

    private static final String PROPERTY_NAME = "propertyName";

    @Test
    public void testGetLongPropertyFromString() {
        Assert.assertEquals(Long.valueOf(150L), PropertyUtils.getLongProperty("150"));
        assertNull(PropertyUtils.getLongProperty("150a"));
    }

    @Test
    public void testGetIntegerProperty() {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_NAME, "15");
        Assert.assertEquals(Integer.valueOf(15), PropertyUtils.getIntegerProperty(properties, PROPERTY_NAME));
        Assert.assertEquals(Integer.valueOf(15), PropertyUtils.getIntegerProperty(properties, PROPERTY_NAME, 10));
        Assert.assertEquals(Integer.valueOf(10), PropertyUtils.getIntegerProperty(new Properties(), PROPERTY_NAME, 10));
        assertNull(PropertyUtils.getIntegerProperty(new Properties(), PROPERTY_NAME));
    }

    @Test
    public void testGetStringProperty() {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_NAME, "someValue");
        Assert.assertEquals("someValue", PropertyUtils.getStringProperty(properties, PROPERTY_NAME));
        assertNull(PropertyUtils.getStringProperty(new Properties(), PROPERTY_NAME));
    }

}
