/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

import com.sdl.odata.api.edm.annotations.EdmFunction;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;

/**
 * Example of unbound function sample.
 */
@EdmFunction(namespace = "ODataDemo", name = "UnboundODataDemoFunction", isBound = false)
@EdmReturnType(type = "Edm.String")
public class UnboundFunctionSample implements Operation<String> {

    @EdmParameter
    private String stringFunctionField;

    @EdmParameter
    private int intFunctionField;

    public String getStringFunctionField() {
        return stringFunctionField;
    }

    public void setStringFunctionField(String stringFunctionField) {
        this.stringFunctionField = stringFunctionField;
    }

    public int getIntFunctionField() {
        return intFunctionField;
    }

    public void setIntFunctionField(int intFunctionField) {
        this.intFunctionField = intFunctionField;
    }

    @Override
    public String doOperation(ODataRequestContext requestContext,
                              DataSourceFactory dataSourceFactory) {
        return stringFunctionField + intFunctionField;
    }
}
