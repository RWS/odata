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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ODataClientQuery is responsible for building set of expectations
 * for ODataClient. With the help of ODataClientQuery we are to use
 * all possible options for search the entities we need.
 */
public class BasicODataClientQuery extends AbstractODataClientQuery {

    private Map<String, String> filterMap;
    private List<String> expandParameters;
    private static final String EXPAND_PREFIX = "$expand=";
    private static final String FILTER_PREFIX = "$filter=";

    public BasicODataClientQuery(Builder builder) {
        if (builder.entityType == null) {
            throw new IllegalArgumentException("EntityType shouldn't be null");
        }
        setEntityType(builder.entityType);
        setEntityKey(builder.entityKey);
        this.filterMap = builder.filteringMap;
        this.expandParameters = builder.expandParameters;
    }

    public String getQuery() {
        StringBuilder query = new StringBuilder();
        query.append(getEdmEntityName());
        if (!isSingletonEntity()) {
            query.append(generateParameters());
        }
        return query.toString();
    }

    /**
     * Returns a StringBuilder that is consisted of filtering and expanding parameters that in turn are appended
     * to the query string used for Odata Client.
     * An Odata Client query can have either one of filter or expand parameters (with multiple properties
     * if desired) or both.
     * <p>
     *
     * @return String Builder showing parameters appended to the query.
     * @see {@link com.sdl.odata.client.api.ODataClient}
     */
    private StringBuilder generateParameters() {
        StringBuilder parameters = new StringBuilder();
        if (filterMap == null && expandParameters == null) {
            return parameters;
        }
        parameters.append('?');
        int filterParameterCounter = 0;
        if (filterMap != null && !filterMap.isEmpty()) {
            parameters.append(FILTER_PREFIX);
            for (Map.Entry<String, String> filterEntry : filterMap.entrySet()) {
                parameters.append(String.format("%s eq '%s'", filterEntry.getKey(), filterEntry.getValue()));
                if (++filterParameterCounter < filterMap.size()) {
                    parameters.append(" and ");
                }
            }
            if (expandParameters != null) {
                parameters.append("&");
            }
        }

        if (expandParameters != null) {
            parameters.append(EXPAND_PREFIX);
            Iterator iterator = expandParameters.iterator();
            parameters.append(String.format("%s", iterator.next()));
            while (iterator.hasNext()) {
                parameters.append(String.format(",%s", iterator.next()));
            }
        }
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BasicODataClientQuery that = (BasicODataClientQuery) o;

        if (!getEntityType().equals(that.getEntityType())) {
            return false;
        }
        if (expandParameters == null ?
                that.expandParameters != null : !expandParameters.equals(that.expandParameters)) {
            return false;
        }

        if (filterMap == null ? that.filterMap != null : !filterMap.equals(that.filterMap)) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        int result = getEntityType().hashCode();
        result = HASH * result + (filterMap == null ? 0 : filterMap.hashCode());
        result = HASH * result + (expandParameters == null ? 0 : expandParameters.hashCode());
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
        private List<String> expandParameters;
        //Using LinkedHashMap to preserve filter parameters insertion order.
        private Map<String, String> filteringMap;
        private String entityKey;

        public Builder withEntityType(Class<?> clazz) {
            this.entityType = clazz;
            return this;
        }

        public Builder withFilterMap(String filterParameter, String filterValue) {
            if (this.filteringMap == null) {
                filteringMap = new LinkedHashMap<>();
            }
            this.filteringMap.put(filterParameter, filterValue);
            return this;
        }

        public Builder withExpandParameters(String expandParameter) {
            if (this.expandParameters == null) {
                expandParameters = new ArrayList<>();
            }
            this.expandParameters.add(expandParameter);
            return this;
        }

        public Builder withEntityKey(String newEntityKey) {
            this.entityKey = newEntityKey;
            return this;
        }

        public BasicODataClientQuery build() {
            return new BasicODataClientQuery(this);
        }
    }
}
