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
package com.sdl.odata.unmarshaller.json.core;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.NavigationProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import com.sdl.odata.unmarshaller.json.ODataJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Json Nullable Validator
 * <p>
 * Support class. Main goal is to validate non nullable entities.
 */
public class JsonNullableValidator {
    private static final Logger LOG = LoggerFactory.getLogger(JsonNullableValidator.class);

    private final Map<String, Object> jsonObject;

    public JsonNullableValidator(Map<String, Object> jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Ensure that non nullable collection are present.
     *
     * @param entityType entityType
     * @throws ODataException If unable to ensure collection is present
     */
    public void ensureCollection(StructuredType entityType) throws ODataException {
        List<String> missingCollectionPropertyName = new ArrayList<>();
        Map<String, Object> fields = ODataJsonParser.objectProperties(jsonObject);

        entityType.getStructuralProperties().stream()
                .filter(property -> (property.isCollection())
                        && !(property instanceof NavigationProperty) && !property.isNullable()).forEach(property -> {
            LOG.debug("Validating non-nullable collection property : {}", property.getName());
            if (!fields.containsKey(property.getName())) {
                missingCollectionPropertyName.add(property.getName());
            }
        });
        if (missingCollectionPropertyName.size() != 0) {
            StringJoiner joiner = new StringJoiner(",");
            missingCollectionPropertyName.forEach(joiner::add);
            throw new ODataUnmarshallingException("The request does not specify the non-nullable collections: '"
                    + joiner.toString() + ".");
        }

    }

    /**
     * Ensure that non nullable navigation properties are present.
     *
     * @param entityType entityType
     * @throws ODataException If unable to ensure navigation properties are present
     */
    public void ensureNavigationProperties(StructuredType entityType) throws ODataException {
        List<String> missingNavigationPropertyNames = new ArrayList<>();
        Map<String, Object> links = ODataJsonParser.links(jsonObject);

        entityType.getStructuralProperties().stream().filter(property -> (property instanceof NavigationProperty)
                && !property.isNullable()).forEach(property -> {
            LOG.debug("Validating non-nullable NavigationProperty property : {}", property.getName());
            if (!links.containsKey(property.getName())) {
                missingNavigationPropertyNames.add(property.getName());
            }
        });
        if (missingNavigationPropertyNames.size() != 0) {
            StringJoiner joiner = new StringJoiner(",");
            missingNavigationPropertyNames.forEach(joiner::add);
            throw new ODataUnmarshallingException("The request does not specify the navigation links for '"
                    + joiner.toString() + ".");
        }
    }

}
