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
package com.sdl.odata.api.processor.query;

import com.sdl.odata.api.ODataException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Result of query operation.
 */
public final class QueryResult {

    private final ResultType type;
    private final Object data;
    private final Map<String, Object> metadata;

    private QueryResult(Object data, ResultType type) {
        this.data = data;
        this.type = type;
        this.metadata = new HashMap<>();
    }

    public static QueryResult from(Object obj) {
        if (obj == null) {
            return new QueryResult(null, ResultType.NOTHING);
        }

        if (obj instanceof ODataException) {
            return new QueryResult(obj, ResultType.EXCEPTION);
        }

        if (obj instanceof List<?>) {
            return new QueryResult(obj, ResultType.COLLECTION);
        }

        // returns raw json
        if (obj instanceof String) {
            return new QueryResult(obj, ResultType.RAW_JSON);
        }

        if (obj instanceof Stream) {
            return new QueryResult(obj, ResultType.STREAM);
        }

        return new QueryResult(obj, ResultType.OBJECT);
    }

    public QueryResult addMeta(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    public QueryResult withCount(long count) {
        return addMeta("count", count);
    }

    @Override
    public String toString() {
        return type.name() + ": " + data.toString();
    }

    public Object getData() {
        return data;
    }

    public ResultType getType() {
        return type;
    }

    public Map<String, Object> getMeta() {
        return metadata;
    }

    /**
     * Description of what is stored inside of {@link QueryResult}.
     */
    public enum ResultType {
        /**
         * Query returns Entity collection.
         */
        COLLECTION,
        /**
         * Exception happened during query processing.
         * {@link ODataException} is stored in data.
         */
        EXCEPTION,
        /**
         * Query returns nothing, null.
         */
        NOTHING,
        /**
         * Query returns value object.
         * Data can simple or entity types.
         */
        OBJECT,
        /**
         * Query returns raw json object.
         */
        RAW_JSON,
        /**
         * Query returns {@link java.util.stream.Stream} for chunked requests.
         */
        STREAM
    }
}
