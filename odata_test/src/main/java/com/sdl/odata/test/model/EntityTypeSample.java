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
package com.sdl.odata.test.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * The Entity Type Sample.
 */
@EdmEntity(namespace = "ODataDemo", key = { "Id" })
@EdmEntitySet("EntityTypeSamples")
public class EntityTypeSample extends AbstractEntityTypeSample {

    @EdmProperty(name = "Id", nullable = false)
    private String id;

    @EdmProperty(name = "ComplexTypeProperty", nullable = true)
    private ComplexTypeSample complexTypeProperty;

    @EdmProperty(name = "ComplexTypeProperties", nullable = true)
    private List<ComplexTypeSample> complexTypeProperties = new ArrayList<>();

    @EdmProperty(name = "ComplexTypeListProperty", nullable = true)
    private ComplexTypeSampleList complexTypeListProperty;


    public String getId() {
        return id;
    }

    public EntityTypeSample setId(String entityTypeId) {
        this.id = entityTypeId;
        return this;
    }

    public ComplexTypeSample getComplexTypeProperty() {
        return complexTypeProperty;
    }

    public EntityTypeSample setComplexTypeProperty(ComplexTypeSample complexProperty) {
        this.complexTypeProperty = complexProperty;
        return this;
    }

    public List<ComplexTypeSample> getComplexTypeProperties() {
        return complexTypeProperties;
    }

    public void setComplexTypeProperties(List<ComplexTypeSample> complexTypeProperties) {
        this.complexTypeProperties = complexTypeProperties;
    }

    public ComplexTypeSampleList getComplexTypeListProperty() {
        return complexTypeListProperty;
    }

    public void setComplexTypeListProperty(ComplexTypeSampleList complexTypeListProperty) {
        this.complexTypeListProperty = complexTypeListProperty;
    }
}
