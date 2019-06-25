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
package com.sdl.odata.test.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.model.ReferencableEntity;

import java.util.List;

/**
 * The order test model.
 */
@EdmEntity(namespace = "ODataDemo", key = {"id" })
@EdmEntitySet("Orders")
public class Order extends ReferencableEntity {

    @EdmProperty(nullable = false)
    private long id;

    @EdmNavigationProperty(nullable = false)
    private Customer customer;

    @EdmNavigationProperty
    private List<OrderLine> orderLines;

    public Order() {
    }

    public long getId() {
        return id;
    }

    public Order setId(long orderId) {
        this.id = orderId;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Order setCustomer(Customer orderCustomer) {
        this.customer = orderCustomer;
        return this;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public Order setOrderLines(List<OrderLine> lines) {
        this.orderLines = lines;
        return this;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", orderLines=" + orderLines +
                '}';
    }
}
