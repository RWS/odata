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

import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import org.slf4j.Logger;

import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Property Resolver Utils.
 * Support class for getting the properties.
 */
public final class PropertyUtils {

    private PropertyUtils() {
    }

    private static final Logger LOG = getLogger(PropertyUtils.class);

    /**
     * Get an integer property from the properties.
     *
     * @param properties the provided properties
     * @return the integer property
     */
    public static Integer getIntegerProperty(Properties properties, String key) {
        String property = getStringProperty(properties, key);
        if (property == null) {
            return null;
        }
        Integer value;
        try {
            value = Integer.parseInt(property);
        } catch (RuntimeException e) {
            throw new ODataClientRuntimeException("Unable to parse property. " + property, e);
        }
        return value;
    }

    public static Integer getIntegerProperty(Properties properties, String key, Integer defaultValue) {
        Integer property = getIntegerProperty(properties, key);
        return property == null ? defaultValue : property;
    }

    /**
     * Get a string property from the properties.
     *
     * @param properties the provided properties
     * @return the string property
     */
    public static String getStringProperty(Properties properties, String key) {
        String property = properties.getProperty(key);
        return property != null && property.trim().isEmpty() ? null : property;
    }

    /**
     * Get a long property from properties.
     *
     * @param property the property as string
     * @return The property as long or null if could not convert
     */
    public static Long getLongProperty(String property) {
        if (property == null) {
            return null;
        }
        Long propertyLong = null;
        try {
            propertyLong = Long.valueOf(property);
        } catch (NumberFormatException e) {
            LOG.warn("Cannot convert string value into number", e);
        }
        return propertyLong;
    }

}
