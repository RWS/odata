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

import com.sdl.odata.client.api.ODataClientQuery;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client Query for function call to the OData service.
 */
public class BoundFunctionClientQuery extends AbstractODataFunctionClientQuery {

    private String boundEntityName;
    private String functionNameSpace;

    public BoundFunctionClientQuery(Builder builder) {
        super(builder.entityType, builder.functionName, builder.functionParameterMap, builder.streaming);

        checkNotNull(builder.boundEntityName, "Bound Entity Name shouldn't be null");
        this.boundEntityName = builder.boundEntityName;
        this.functionNameSpace = builder.functionNameSpace;
    }

    @Override
    public String getQuery() {
        return boundEntityName + "/" + functionNameSpace + "." + appendFunctionPath() + generateFunctionParameters();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BoundFunctionClientQuery that = (BoundFunctionClientQuery) o;

        if (!boundEntityName.equals(that.boundEntityName)) {
            return false;
        }
        if (!getFunctionName().equals(that.getFunctionName())) {
            return false;
        }
        if (!functionNameSpace.equals(that.functionNameSpace)) {
            return false;
        }
        if (getFunctionParameterMap() != null ? !getFunctionParameterMap().equals(that.getFunctionParameterMap())
                : that.getFunctionParameterMap() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = boundEntityName.hashCode();
        result = HASH * result + functionNameSpace.hashCode();
        result = HASH * result + getFunctionName().hashCode();
        result = HASH * result + getFunctionParameterMap().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("ODataClientQuery[%s]", getQuery());
    }

    /**
     * Builder for {@code ODataRequest} objects.
     */
    public static class Builder {

        private Class<?> entityType;
        private String functionName;
        private Map<String, String> functionParameterMap;
        private String boundEntityName;
        private String functionNameSpace;
        private boolean streaming;

        public Builder withBoundEntityName(String boundEntity) {
            this.boundEntityName = boundEntity;
            return this;
        }

        public Builder withNameSpace(String nameSpace) {
            this.functionNameSpace = nameSpace;
            return this;
        }

        public Builder withEntityType(Class<?> clazz) {
            this.entityType = clazz;
            return this;
        }

        public Builder withFunctionParameter(String functionParameterName, String functionParameterValue) {
            if (this.functionParameterMap == null) {
                functionParameterMap = new LinkedHashMap<>();
            }
            this.functionParameterMap.put(functionParameterName, functionParameterValue);
            return this;
        }

        public Builder withFunctionName(String name) {
            this.functionName = name;
            return this;
        }

        public Builder withStreamingSupport(boolean streamingSupport) {
            this.streaming = streamingSupport;
            return this;
        }

        public ODataClientQuery build() {
            return new BoundFunctionClientQuery(this);
        }
    }
}
