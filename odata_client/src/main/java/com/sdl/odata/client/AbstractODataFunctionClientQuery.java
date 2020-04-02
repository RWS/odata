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
package com.sdl.odata.client;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base class for functions calls to the OData service.
 */
public abstract class AbstractODataFunctionClientQuery extends AbstractODataClientQuery {

    private String functionName;
    private Map<String, String> functionParameterMap;

    public AbstractODataFunctionClientQuery(Class<?> entityType,
                                            String functionName,
                                            Map<String, String> functionParameterMap,
                                            boolean streaming) {
        checkNotNull(functionName, "FunctionName shouldn't be null");
        checkNotNull(entityType, "EntityType shouldn't be null");

        setEntityType(entityType);
        setStreamingSupport(streaming);
        this.functionName = functionName;
        this.functionParameterMap = functionParameterMap;
    }

    protected String appendFunctionPath() {
        return this.functionName;
    }

    protected String generateFunctionParameters() {
        if (this.functionParameterMap == null || this.functionParameterMap.isEmpty()) {
            return "";
        }

        return "(" + this.functionParameterMap.entrySet().stream()
                .filter(entity -> entity.getValue() != null && !entity.getValue().isEmpty())
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","))
                + ")";
    }

    protected String getFunctionName() {
        return functionName;
    }

    protected Map<String, String> getFunctionParameterMap() {
        return functionParameterMap;
    }
}
