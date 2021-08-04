/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

/**
 * The Complex Type Sample model.
 */
@EdmComplex(namespace = "ODataDemo")
public class ComplexTypeSample extends AbstractComplexTypeSample {

    @EdmProperty(name = "SimpleProperty", nullable = true)
    private String simpleProperty;

    public String getSimpleProperty() {
        return simpleProperty;
    }

    public ComplexTypeSample setSimpleProperty(String property) {
        this.simpleProperty = property;
        return this;
    }
}
