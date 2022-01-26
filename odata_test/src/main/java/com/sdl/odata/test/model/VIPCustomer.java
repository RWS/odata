/*
 * Copyright (c) 2014-2022 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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

/**
 * The VIP Customer.
 */
@EdmEntity(namespace = "ODataDemo", key = {"id" })
@EdmEntitySet("VIPCustomer")
public class VIPCustomer extends Customer {
    @EdmProperty(nullable = false, name = "vip_id")
    private long vipId;

    @EdmProperty(nullable = false, name = "vip_address")
    private Address vipAddress;

    public long getVipId() {
        return vipId;
    }

    public VIPCustomer setVipId(long theVipId) {
        this.vipId = theVipId;
        return this;
    }

    public Address getVipAddress() {
        return vipAddress;
    }

    public VIPCustomer setVipAddress(Address theVipAddress) {
        this.vipAddress = theVipAddress;
        return this;
    }
}
