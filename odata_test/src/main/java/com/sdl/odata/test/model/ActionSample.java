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
package com.sdl.odata.test.model;

import com.sdl.odata.api.edm.annotations.EdmAction;
import com.sdl.odata.api.edm.annotations.EdmParameter;
import com.sdl.odata.api.edm.annotations.EdmReturnType;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;

import java.util.List;
import java.util.Map;

/**
 * The sample action class.
 */
@EdmAction(namespace = "ODataDemo", name = "ODataDemoAction", entitySetPath = "ODataDemoEntitySetPath",
        isBound = true)
@EdmReturnType(type = "Customers")
public class ActionSample implements Operation<Customer> {

    @EdmParameter(name = "StringParameter")
    private String stringParameter;

    @EdmParameter(name = "NumberParameter")
    private Long number;

    @EdmParameter
    private int intNumber;

    @EdmParameter
    private Map<String, String> parametersMap;

    @EdmParameter
    private List<Map<String, String>> parametersMapList;

    @Override
    public Customer doOperation(ODataRequestContext requestContext,
                                DataSourceFactory dataSourceFactory) {
        // Doesn't return anything because there is no possibility to implement it without datasource.
        return null;
    }

    public String getStringParameter() {
        return stringParameter;
    }

    public Long getNumber() {
        return number;
    }

    public int getIntNumber() {
        return intNumber;
    }

    public Map<String, String> getParameters() {
        return parametersMap;
    }

    public List<Map<String, String>> getParametersList() {
        return parametersMapList;
    }
}
