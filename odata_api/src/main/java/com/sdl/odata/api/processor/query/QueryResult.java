package com.sdl.odata.api.processor.query;

import com.sdl.odata.api.ODataException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of query operation.
 */
public class QueryResult {

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

        if (obj instanceof List<?>) {
            return new QueryResult(obj, ResultType.COLLECTION);
        }

        if (obj instanceof Long || obj instanceof Integer || obj instanceof String) {
            return new QueryResult(obj, ResultType.VALUE);
        }

        if (obj instanceof ODataException) {
            return new QueryResult(obj, ResultType.EXCEPTION);
        }

        return new QueryResult(obj, ResultType.ENTITY);
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

    public enum ResultType {
        COLLECTION,
        ENTITY,
        EXCEPTION,
        NOTHING,
        VALUE
    }
}
