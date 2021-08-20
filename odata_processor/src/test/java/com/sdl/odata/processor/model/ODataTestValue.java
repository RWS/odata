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
package com.sdl.odata.processor.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

/**
 * The OData Test Values.
 */
@EdmEntity(namespace = "ODataTest", key = "id")
@EdmEntitySet("ODataTestValues")
public class ODataTestValue {

    @EdmProperty(nullable = false)
    private String id;

    @EdmNavigationProperty(nullable = true, partner = "configurationValues")
    private ODataTestItem oDataItem;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ODataTestItem getoDataItem() {
        return oDataItem;
    }

    public void setoDataItem(ODataTestItem oDataItem) {
        this.oDataItem = oDataItem;
    }
}
