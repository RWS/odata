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
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.sdl.odata.model.ReferencableEntity;

/**
 * The Bank Account.
 */
@EdmEntity(namespace = "ODataDemo", key = {"iban" })
@EdmEntitySet("BankAccounts")
public class BankAccount extends ReferencableEntity {
    /**
     * EDM Entity Max Length.
     */
    public static final int EDM_MAX_LENGTH = 28;

    @EdmProperty(nullable = false, maxLength = EDM_MAX_LENGTH)
    private String iban;

    public String getIban() {
        return iban;
    }

    public BankAccount setIban(String accountIban) {
        this.iban = accountIban;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BankAccount that = (BankAccount) o;

        if (!iban.equals(that.iban)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return iban.hashCode();
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "iban='" + iban + '\'' +
                '}';
    }
}
