/*
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
package com.sdl.odata.test.model.complex;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * OData data transfer object implementation.
 */
@EdmComplex(namespace = "ODataDemo")
public class ODataDemoProperty {

    @EdmProperty
    private String propertyName;

    @EdmProperty(nullable = true)
    private ODataDemoPropertyType propertyType;

    @EdmProperty
    private ODataDemoPropertyValue defaultValue;

    @EdmProperty
    private List<String> requires = new ArrayList<>();

    @EdmProperty
    private List<String> implies = new ArrayList<>();

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public ODataDemoPropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(ODataDemoPropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public ODataDemoPropertyValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(ODataDemoPropertyValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getRequires() {
        return requires;
    }

    public void setRequires(List<String> requires) {
        this.requires = requires;
    }

    public List<String> getImplies() {
        return implies;
    }

    public void setImplies(List<String> implies) {
        this.implies = implies;
    }
}
