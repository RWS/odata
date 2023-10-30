/**
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
package com.sdl.odata.processor.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * OData Test Item (pretty similar to ConfigurationItem).
 */
@EdmEntity(namespace = "ODataTest", key = "id")
@EdmEntitySet("ODataTestItems")
public class ODataTestItem {

    @EdmProperty(nullable = false)
    private String id;

    @EdmNavigationProperty(nullable = true, partner = "configurationItems")
    private ODataTestArea oDataArea;

    @EdmNavigationProperty(nullable = false)
    private List<ODataTestValue> odataValues = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ODataTestArea getODataArea() {
        return oDataArea;
    }

    public void setODataArea(ODataTestArea area) {
        this.oDataArea = area;
    }

    public List<ODataTestValue> getOdataValues() {
        return odataValues;
    }

    public void setOdataValues(List<ODataTestValue> odataValues) {
        this.odataValues = odataValues;
    }
}

