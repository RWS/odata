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
import com.sdl.odata.api.edm.model.OnDeleteAction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The Customer model.
 */
@EdmEntity(namespace = "ODataDemo", key = {"id" }, containerName = "ODataDemoContainer")
@EdmEntitySet("Customers")
public class Customer {
    /**
     * EDM MAX Length.
     */
    public static final int EDM_MAX_LENGTH = 80;
    /**
     * Phone.
     */
    public static final String PHONE = "Phone";
    /**
     * Hash.
     */
    public static final int HASH = 31;
    // NOTE: Factory will auto automatically determine that the OData type Edm.Int64 should be used.
    @EdmProperty(nullable = false)
    private long id;

    // NOTE: Factory will automatically determine that the OData type Edm.String should be used.
    @EdmProperty(nullable = false, maxLength = EDM_MAX_LENGTH)
    private String name;

    @EdmProperty(nullable = true)
    private EnumSample enumSample;

    @EdmProperty(nullable = true, name = PHONE)
    private List<String> phoneNumbers = new ArrayList<>();

    // NOTE: Factory will automatically lookup the OData type of Address. There will be an initialization order problem,
    // and it is possible to specify circular dependencies which will be an unsolvable problem.
    // This is an embedded ComplexType instance.
    @EdmProperty(nullable = false)
    private List<Address> address = new ArrayList<>();

    @EdmProperty(nullable = false, name = "date")
    private ZonedDateTime dateTime;

    @EdmNavigationProperty(name = "Orders", nullable = false, partner = "customer", onDelete = OnDeleteAction.NONE)
    private List<Order> orders = new ArrayList<>();

    @EdmNavigationProperty(name = "BankAccount", nullable = true, onDelete = OnDeleteAction.NONE)
    private BankAccount bankAccount;

    public Customer() {
    }

    public long getId() {
        return id;
    }

    public Customer setId(long customerId) {
        this.id = customerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setName(String customerName) {
        this.name = customerName;
        return this;
    }

    public List<Address> getAddress() {
        return address;
    }

    public Customer setAddress(List<Address> customerAddress) {
        this.address = customerAddress;
        return this;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Customer setOrders(List<Order> customerOrders) {
        this.orders = customerOrders;
        return this;
    }

    public Customer setPhoneNumbers(List<String> numbers) {
        this.phoneNumbers = numbers;
        return this;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public Customer setDateTime(ZonedDateTime dt) {
        this.dateTime = dt;
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

        Customer customer = (Customer) that;

        if (id != customer.id) {
            return false;
        }
        if (address != null ? !address.equals(customer.address) : customer.address != null) {
            return false;
        }
        if (!areDatesEqual(customer.dateTime)) {
            return false;
        }
        if (name != null ? !name.equals(customer.name) : customer.name != null) {
            return false;
        }
        if (phoneNumbers != null ? !phoneNumbers.equals(customer.phoneNumbers) : customer.phoneNumbers != null) {
            return false;
        }
        if (bankAccount != null ? !bankAccount.equals(customer.bankAccount) : customer.bankAccount != null) {
            return false;
        }

        return true;
    }

    private boolean areDatesEqual(ZonedDateTime that) {
        if (dateTime == null && that == null) {
            return true;
        }

        if (dateTime != null && that != null) {
            return dateTime.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> HASH + 1));
        result = HASH * result + (name != null ? name.hashCode() : 0);
        result = HASH * result + (phoneNumbers != null ? phoneNumbers.hashCode() : 0);
        result = HASH * result + (address != null ? address.hashCode() : 0);
        result = HASH * result + (dateTime != null ? dateTime.hashCode() : 0);
        result = HASH * result + (orders != null ? orders.hashCode() : 0);
        result = HASH * result + (bankAccount != null ? bankAccount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                ", address=" + address +
                ", dateTime=" + dateTime +
                ", orders=" + orders +
                ", bankAccount=" + bankAccount +
                '}';
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public Customer setBankAccount(BankAccount account) {
        this.bankAccount = account;
        return this;
    }

    public EnumSample getEnumSample() {
        return enumSample;
    }

    public Customer setEnumSample(EnumSample enumSample) {
        this.enumSample = enumSample;
        return this;
    }
}
