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

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.util.List;

/**
 * The Examplded Properties Sample test model.
 */
@EdmEntity(namespace = "ODataSample", key = {"ID" })
@EdmEntitySet
public class ExpandedPropertiesSample {
    /**
     * The EDM Property Length.
     */
    public static final int EDM_PROPERTY_LENGTH = 80;

    @EdmProperty(name = "ID", nullable = false)
    private long id;

    @EdmProperty(name = "Name", nullable = false, maxLength = EDM_PROPERTY_LENGTH)
    private String name;

    @EdmNavigationProperty(name = "ExpandedEntry", nullable = false)
    private IdNamePairSample expandedEntry;

    @EdmNavigationProperty(name = "ExpandedFeed", nullable = false)
    private List<IdNamePairSample> expandedFeed;

    @EdmNavigationProperty(name = "Entry", nullable = false)
    private IdNamePairSample entry;

    @EdmNavigationProperty(name = "Feed", nullable = false)
    private List<IdNamePairSample> feed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IdNamePairSample getExpandedEntry() {
        return expandedEntry;
    }

    public void setExpandedEntry(IdNamePairSample expandedEntry) {
        this.expandedEntry = expandedEntry;
    }

    public List<IdNamePairSample> getExpandedFeed() {
        return expandedFeed;
    }

    public void setExpandedFeed(List<IdNamePairSample> expandedFeed) {
        this.expandedFeed = expandedFeed;
    }

    public IdNamePairSample getEntry() {
        return entry;
    }

    public void setEntry(IdNamePairSample entry) {
        this.entry = entry;
    }

    public List<IdNamePairSample> getFeed() {
        return feed;
    }

    public void setFeed(List<IdNamePairSample> feed) {
        this.feed = feed;
    }
}
