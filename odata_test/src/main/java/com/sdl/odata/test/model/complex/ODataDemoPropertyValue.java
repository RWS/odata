/*
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
package com.sdl.odata.test.model.complex;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * OData transfer object implementation.
 */
@EdmComplex(namespace = "ODataDemo")
public class ODataDemoPropertyValue {

    @EdmProperty
    private Boolean booleanValue;

    @EdmProperty
    private Integer integerValue;

    @EdmProperty
    private Double floatValue;

    @EdmProperty
    private Set<String> setValue = new HashSet<>();

    @EdmProperty
    private String stringValue;

    @EdmProperty
    private ODataVersion versionValue;

    @EdmProperty
    private ODataDemoClassification pathValue;

    @EdmProperty
    private ODataDemoPropertyType type;

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Double getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Double floatValue) {
        this.floatValue = floatValue;
    }

    public Set<String> getSetValue() {
        return setValue;
    }

    public void setSetValue(Set<String> setValue) {
        this.setValue = setValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public ODataVersion getVersionValue() {
        return versionValue;
    }

    public void setVersionValue(ODataVersion versionValue) {
        this.versionValue = versionValue;
    }

    public ODataDemoClassification getPathValue() {
        return pathValue;
    }

    public void setPathValue(ODataDemoClassification pathValue) {
        this.pathValue = pathValue;
    }

    public ODataDemoPropertyType getType() {
        return type;
    }

    public void setType(ODataDemoPropertyType type) {
        this.type = type;
    }
}
