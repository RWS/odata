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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * OData transfer object implementation.
 */
@EdmComplex(namespace = "ODataDemo")
public class ODataDemoClassification {

    @EdmProperty
    private List<String> classifications = new ArrayList<>();

    @EdmProperty
    private String key;

    public ODataDemoClassification() {
    }

    public ODataDemoClassification(String key, List<String> classifications) {
        this.classifications = new ArrayList<>(new LinkedHashSet<>());
        this.key = key;
    }

    public List<String> getClassifications() {
        return Collections.unmodifiableList(this.classifications);
    }

    public void setClassifications(List<String> classifications) {
        this.classifications = new ArrayList<>(classifications);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        return key;
    }
}
