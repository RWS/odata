/**
 * Copyright (c) 2014 All Rights Reserved by the RWS Group.
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

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

/**
 * The model Adress.
 */
@EdmComplex(namespace = "ODataDemo")
public class Address {

    @EdmProperty(name = "Street", nullable = false, maxLength = EDM_MAX_LENGTH)
    private String street;

    @EdmProperty(nullable = false, maxLength = EDM_PROPERTY_LENGTH)
    private String houseNumber;

    @EdmProperty(nullable = false, maxLength = EDM_PROPERTY_LENGTH)
    private String postalCode;

    @EdmProperty(nullable = false, maxLength = EDM_MAX_LENGTH)
    private String city;

    @EdmProperty(nullable = false, maxLength = EDM_MAX_LENGTH)
    private String country;

    /**
     * Hash Number.
     */
    public static final int HASH_NUMBER = 31;

    /**
     * EDM Property Length.
     */
    public static final int EDM_PROPERTY_LENGTH = 20;

    /**
     * Edm Max Length.
     */
    public static final int EDM_MAX_LENGTH = 60;

    public Address() {
    }

    public String getStreet() {
        return street;
    }

    public Address setStreet(String adressStreet) {
        this.street = adressStreet;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public Address setHouseNumber(String houseNum) {
        this.houseNumber = houseNum;
        return this;
    }

    public String getPostalCode() {
        return postalCode;

    }

    public Address setPostalCode(String postal) {
        this.postalCode = postal;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address setCity(String adressCity) {
        this.city = adressCity;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Address setCountry(String addressCountry) {
        this.country = addressCountry;
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

        Address address = (Address) that;

        if (city != null ? !city.equals(address.city) : address.city != null) {
            return false;
        }
        if (country != null ? !country.equals(address.country) : address.country != null) {
            return false;
        }
        if (houseNumber != null ? !houseNumber.equals(address.houseNumber) : address.houseNumber != null) {
            return false;
        }
        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null) {
            return false;
        }
        if (street != null ? !street.equals(address.street) : address.street != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = street != null ? street.hashCode() : 0;
        result = HASH_NUMBER * result + (houseNumber != null ? houseNumber.hashCode() : 0);
        result = HASH_NUMBER * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = HASH_NUMBER * result + (city != null ? city.hashCode() : 0);
        result = HASH_NUMBER * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
