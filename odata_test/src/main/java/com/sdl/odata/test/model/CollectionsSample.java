/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * The Collections Sample.
 */
@EdmEntity(namespace = "ODataSample", key = {"ID" })
@EdmEntitySet
public class CollectionsSample {

    /**
     * The Edm Max Length.
     */
    public static final int EDM_MAX_LENGTH = 80;

    @EdmProperty(name = "ID", nullable = false)
    private long id;

    @EdmProperty(name = "Name", nullable = false, maxLength = EDM_MAX_LENGTH)
    private String name;

    @EdmProperty(name = "PrimitivesCollection")
    private List<String> primitivesCollection = new ArrayList<>();

    @EdmProperty(name = "EnumCollection")
    private List<EnumSample> enumCollection = new ArrayList<>();

    @EdmProperty(name = "IdNamePairCollection")
    private List<IdNamePairComplex> idNamePairCollection = new ArrayList<>();

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

    public List<String> getPrimitivesCollection() {
        return primitivesCollection;
    }

    public void setPrimitivesCollection(List<String> primitivesCollection) {
        this.primitivesCollection = primitivesCollection;
    }

    public List<EnumSample> getEnumCollection() {
        return enumCollection;
    }

    public void setEnumCollection(List<EnumSample> enumCollection) {
        this.enumCollection = enumCollection;
    }

    public List<IdNamePairComplex> getIdNamePairCollection() {
        return idNamePairCollection;
    }

    public void setIdNamePairCollection(List<IdNamePairComplex> idNamePairCollection) {
        this.idNamePairCollection = idNamePairCollection;
    }
}
