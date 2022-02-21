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
package com.sdl.odata.test.model.complex;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

/**
 * OData data transfer object implementation.
 */
@EdmComplex(namespace = "ODataDemo")
public class ODataVersionPart {


    private static final int HASH_CODE_MULTIPLIER = 31;

    @EdmProperty(nullable = false)
    private int number = 0;

    @EdmProperty
    private String modifier;

    @EdmProperty(nullable = false)
    private int modifierNumber = 0;

    @EdmProperty(nullable = false)
    private int modifierPriority = 0;


    public ODataVersionPart() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public int getModifierNumber() {
        return modifierNumber;
    }

    public void setModifierNumber(int modifierNumber) {
        this.modifierNumber = modifierNumber;
    }

    public int getModifierPriority() {
        return modifierPriority;
    }

    public void setModifierPriority(int modifierPriority) {
        this.modifierPriority = modifierPriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            /**
             * 0 is equivalent to null in version part comparisons
             */
            return number == 0;
        }

        ODataVersionPart that = (ODataVersionPart) o;

        if (modifierNumber != that.modifierNumber) {
            return false;
        }
        if (modifierPriority != that.modifierPriority) {
            return false;
        }
        if (number != that.number) {
            return false;
        }
        if (modifier != null ? !modifier.equals(that.modifier) : that.modifier != null) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        int result = number;
        result = HASH_CODE_MULTIPLIER * result + (modifier != null ? modifier.hashCode() : 0);
        result = HASH_CODE_MULTIPLIER * result + modifierNumber;
        result = HASH_CODE_MULTIPLIER * result + modifierPriority;
        return result;
    }

    @Override
    public String toString() {
        return number
                + (modifier == null ? "" : (modifier + (modifierNumber > 0 ? String.valueOf(modifierNumber) : "")));
    }

}
