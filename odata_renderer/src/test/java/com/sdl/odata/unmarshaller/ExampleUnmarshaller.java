/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
package com.sdl.odata.unmarshaller;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.StructuralProperty;
import com.sdl.odata.api.edm.model.StructuredType;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshaller;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Example marshaller that shows how to use the {@code EntityDataModel} API to find information about the structure
 * of entities.
 */
public final class ExampleUnmarshaller implements ODataUnmarshaller {

    @Override
    public int score(ODataRequestContext requestContext) {
        return 100;
    }

    @Override
    public Object unmarshall(ODataRequestContext requestContext)
            throws ODataException {
        EntityDataModel entityDataModel = requestContext.getEntityDataModel();

        // This would normally come from the request body, parsed by the unmarshaller from whatever format
        // the unmarshaller handles
        String entityTypeName = "Com.Sdl.Cars.Car";

        // Get the OData type of the entity
        StructuredType type = (StructuredType) entityDataModel.getType(entityTypeName);

        // Get the corresponding Java type
        Class<?> javaType = type.getJavaType();

        // Instantiate it via reflection
        Object entity;
        try {
            entity = javaType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ODataUnmarshallingException("Cannot instantiate entity", e);
        }

        // Get the properties we have to set
        List<StructuralProperty> properties = getAllProperties(type, entityDataModel);

        // Set the values of all properties
        for (StructuralProperty property : properties) {
            // In a real unmarshaller, we would have to get the actual value from the request
            Object value = "example";

            // Get the field in the Java entity that corresponds to this property
            Field field = property.getJavaField();

            // Set the field value via reflection
            try {
                field.setAccessible(true);
                field.set(entity, value);
            } catch (IllegalAccessException e) {
                throw new ODataUnmarshallingException("Error while setting field value: " + field.toGenericString(), e);
            }
        }

        return ImmutableList.of(entity);
    }

    // Gets the properties of a structural type, including the properties of its base types (recursively)
    private List<StructuralProperty> getAllProperties(StructuredType structuredType, EntityDataModel entityDataModel)
            throws ODataException {
        List<StructuralProperty> properties = new ArrayList<>();

        String baseTypeName = structuredType.getBaseTypeName();
        if (!Strings.isNullOrEmpty(baseTypeName)) {
            StructuredType baseType = (StructuredType) entityDataModel.getType(baseTypeName);
            if (baseType == null) {
                throw new ODataUnmarshallingException("OData type not found: " + baseTypeName);
            }

            // Get the properties of the base type
            properties.addAll(getAllProperties(baseType, entityDataModel));
        }

        properties.addAll(structuredType.getStructuralProperties());

        return properties;
    }
}
