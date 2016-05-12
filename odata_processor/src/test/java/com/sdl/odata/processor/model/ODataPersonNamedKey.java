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
package com.sdl.odata.processor.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The OData Person model test. Key is identified by property name.
 */
@EdmEntity(namespace = "ODataTest", key = "ID")
@EdmEntitySet("Persons")
public class ODataPersonNamedKey {

    @EdmProperty(name = "ID", nullable = false)
    private String id;

    // NOTE: Deliberately has a property name different than the field name, to test if this works correctly.
    @EdmProperty(name = "SurName", nullable = false, maxLength = 60)
    private String familyName;

    @EdmProperty(nullable = false, maxLength = 40)
    private String firstName;

    @EdmProperty
    private LocalDate birthDate;

    // Single entity, not nullable
    @EdmNavigationProperty(nullable = false)
    private ODataMobilePhone primaryPhone;

    // Collection of entities
    @EdmNavigationProperty(nullable = false)
    private List<ODataMobilePhone> mobilePhones = new ArrayList<>();

    // Single complex type, not nullable
    @EdmProperty(nullable = false)
    private ODataAddress primaryAddress;

    // Collection of complex objects
    @EdmProperty
    private List<ODataAddress> addresses = new ArrayList<>();

    public ODataPersonNamedKey() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public ODataMobilePhone getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(ODataMobilePhone primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public List<ODataMobilePhone> getMobilePhones() {
        return mobilePhones;
    }

    public void setMobilePhones(List<ODataMobilePhone> mobilePhones) {
        this.mobilePhones = mobilePhones;
    }

    public ODataAddress getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(ODataAddress primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public List<ODataAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ODataAddress> addresses) {
        this.addresses = addresses;
    }
}
