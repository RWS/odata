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

import com.sdl.odata.client.api.ODataActionClientQuery;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client query to execute unbound function imports.
 */
public final class ActionImportClientQuery extends AbstractODataClientQuery implements ODataActionClientQuery {

    private String actionName;
    private String actionRequestBody;

    private ActionImportClientQuery(Builder builder) {
        checkNotNull(builder.returnType, "Action return type should not be null");
        checkNotNull(builder.actionName, "Action name should not be null");

        setEntityType(builder.returnType);
        this.actionName = builder.actionName;
        actionRequestBody = builder.actionParameterMap == null || builder.actionParameterMap.isEmpty() ?
                "" : ("{" + builder.actionParameterMap.entrySet().stream()
                .map(entry -> String.format("\"%s\":%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","))
                + "}");
    }

    @Override
    public String getActionRequestBody() {
        return actionRequestBody;
    }

    @Override
    public String getQuery() {
        return actionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionImportClientQuery that = (ActionImportClientQuery) o;

        return getEntityType().equals(that.getEntityType()) && actionName.equals(that.actionName)
                && actionRequestBody.equals(that.actionRequestBody);
    }

    @Override
    public int hashCode() {
        int result = getEntityType().hashCode();
        result = HASH * result + actionName.hashCode();
        result = HASH * result + actionRequestBody.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("ActionImportClientQuery[%s]", getQuery());
    }

    /**
     * Builder for {@code ODataRequest} objects.
     */
    public static class Builder {

        private Class<?> returnType;
        private String actionName;
        private Map<String, String> actionParameterMap;

        public Builder withReturnType(Class<?> clazz) {
            returnType = clazz;
            return this;
        }

        public Builder withActionParameter(String actionParameterName, String actionParameterValue) {
            if (actionParameterMap == null) {
                actionParameterMap = new LinkedHashMap<>();
            }
            actionParameterMap.put(actionParameterName, actionParameterValue);
            return this;
        }

        public Builder withActionName(String name) {
            actionName = name;
            return this;
        }

        public ODataActionClientQuery build() {
            return new ActionImportClientQuery(this);
        }
    }
}
