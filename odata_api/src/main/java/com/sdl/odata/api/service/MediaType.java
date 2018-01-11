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
package com.sdl.odata.api.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Media type.
 */
public final class MediaType {
    /**
     * Media Type XML.
     */
    public static final MediaType XML = new MediaType("application", "xml");
    /**
     * Media Type Atom XML.
     */
    public static final MediaType ATOM_XML = new MediaType("application", "atom+xml");
    /**
     * Media Type Multipart-Mainly used in Batch Requests.
     */
    public static final MediaType MULTIPART = new MediaType("multipart", "mixed");
    /**
     * Media Type HTTP used in Batch Requests.
     */
    public static final MediaType HTTP = new MediaType("multipart", "application/http");
    /**
     * Media Type AtomSVC + XML.
     */
    public static final MediaType ATOM_SVC_XML = new MediaType("application", "atomsvc+xml");
    /**
     * Media Type JSON.
     */
    public static final MediaType JSON = new MediaType("application", "json");
    /**
     * Media Type JSON with full metadata.
     */
    public static final MediaType JSON_FULL_METADATA = new MediaType("application", "json",
        new HashMap<String, String>() {{
            put(METADATA_PPARAMETER, METADATA_FULL); }});
    /**
     * Media Type Text/Plain.
     */
    public static final MediaType TEXT = new MediaType("text", "plain");
    /**
     * Media Type Wildcard.
     */
    public static final MediaType WILDCARD_ANY = new MediaType("*", "*");

    /**
     * Hash for hashcode() computing.
     */
    public static final int HASH = 31;
    /**
     * Group matcher index.
     */
    public static final int GROUP_INDEX = 4;
    /**
     * Type matcher index.
     */
    public static final int TYPE_INDEX = 2;
    /**
     * Subtype matcher index.
     */
    public static final int SUBTYPE_INDEX = 3;
    /**
     * Group Matcher index.
     */
    public static final int GROUP_MATCHER_INDEX = 5;

    private static final Pattern MEDIA_TYPE_PATTERN = Pattern.compile("(([^/]+)/([^/;]+)|(\\*))(.*)");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(";\\s*([^;=]+)=([^;=]+)");

    /**
     * Metadata parameter.
     */
    public static final String METADATA_PPARAMETER = "odata.metadata";
    /**
     * Metadata full.
     */
    public static final String METADATA_FULL = "full";
    /**
     * Metadata minimal.
     */
    public static final String METADATA_MINIMAL = "minimal";
    /**
     * Metadata none.
     */
    public static final String METADATA_NONE = "none";

    private final String type;
    private final String subType;
    private final Map<String, String> parameters;

    /**
     * Constructor.
     *
     * @param type       Type, for example "text" or "application".
     * @param subType    Subtype, for example "html" or "xml".
     * @param parameters Parameters (may be {@code null}).
     */
    public MediaType(String type, String subType, Map<String, String> parameters) {
        this.type = type;
        this.subType = subType;
        this.parameters = parameters != null ?
                Collections.unmodifiableMap(parameters) : Collections.<String, String>emptyMap();
    }

    /**
     * Constructor.
     *
     * @param type    Type, for example "text" or "application".
     * @param subType Subtype, for example "html" or "xml".
     */
    public MediaType(String type, String subType) {
        this(type, subType, null);
    }

    /**
     * Creates a {@code MediaType} by parsing the specified string. The string must look like a standard MIME type
     * string, for example "text/html" or "application/xml". There can optionally be parameters present, for example
     * "text/html; encoding=UTF-8" or "application/xml; q=0.8".
     *
     * @param text A string representing a media type.
     * @return A {@code MediaType} object.
     * @throws java.lang.IllegalArgumentException If the string is not a valid media type string.
     */
    public static MediaType fromString(String text) {
        Matcher matcher = MEDIA_TYPE_PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid media type string: " + text);
        }

        String type;
        String subType;
        if (matcher.group(GROUP_INDEX) != null) {
            type = matcher.group(GROUP_INDEX);
            subType = matcher.group(GROUP_INDEX);
        } else {
            type = matcher.group(TYPE_INDEX);
            subType = matcher.group(SUBTYPE_INDEX);
        }

        Map<String, String> parametersBuilder = new HashMap<>();
        Matcher parametersMatcher = PARAMETER_PATTERN.matcher(matcher.group(GROUP_MATCHER_INDEX));
        while (parametersMatcher.find()) {
            parametersBuilder.put(parametersMatcher.group(1), parametersMatcher.group(2));
        }
        return new MediaType(type, subType, Collections.unmodifiableMap(parametersBuilder));
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public boolean matches(MediaType that) {
        return (this.type.equals("*") || that.type.equals("*") || this.type.equalsIgnoreCase(that.type)) &&
                (this.subType.equals("*") || that.subType.equals("*") || this.subType.equalsIgnoreCase(that.subType));
    }

    public boolean isWildCardMediaType() {
        return WILDCARD_ANY.getType().equals(this.type) && WILDCARD_ANY.getSubType().equals(this.subType);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        MediaType mediaType = (MediaType) that;

        if (parameters != null ? !parameters.equals(mediaType.parameters) : mediaType.parameters != null) {
            return false;
        }
        if (!subType.equals(mediaType.subType)) {
            return false;
        }
        if (!type.equals(mediaType.type)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = HASH * result + subType.hashCode();
        result = HASH * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append('/').append(subType);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            sb.append(';').append(parameter.getKey()).append('=').append(parameter.getValue());
        }
        return sb.toString();
    }
}
