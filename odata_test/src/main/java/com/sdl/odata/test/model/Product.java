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
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.api.edm.annotations.EdmPropertyRef;

/**
 * The OData Product test model.
 */
@EdmEntity(namespace = "ODataDemo", keyRef = {@EdmPropertyRef(path = "id") })
@EdmEntitySet // Will automatically be named "Products"
public class Product {
    /**
     * EDM Entity Classes.
     */
    public static final int EDM_ENTITY_CLASS = 80;
    /**
     * HASH.
     */
    public static final int HASH = 37;
    @EdmProperty(nullable = false)
    private long id;

    @EdmProperty(nullable = false, maxLength = EDM_ENTITY_CLASS)
    private String name;

    @EdmProperty
    private Category category;

    public Product() {
    }

    public long getId() {
        return id;
    }

    public Product setId(long productId) {
        this.id = productId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String productName) {
        this.name = productName;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Product setCategory(Category productCategory) {
        this.category = productCategory;
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        Product product = (Product) that;
        if (id != product.getId()
                || name == null ? product.getName() != null : !name.equals(product.getName())
                || category == null ? product.getCategory() != null : !category.equals(product.getCategory())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Long.valueOf(id).intValue();
        result = HASH * result + (name != null ? name.hashCode() : 0);
        result = HASH * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
