/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
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
 * OData transfer object implementation.
 */
@EdmComplex(namespace = "ODataDemo")
public class ODataVersion {

    private static final int HASH_CODE_MULTIPLIER = 37;

    @EdmProperty
    private String name;

    @EdmProperty
    private ODataVersionPart majorVersionPart;

    @EdmProperty
    private ODataVersionPart minorVersionPart;

    @EdmProperty
    private ODataVersionPart incrementalVersionPart;

    /**
     * Accessor methods for OData framework.
     * @return The major version
     */
    public ODataVersionPart getMajorVersionPart() {
        return majorVersionPart;
    }

    public void setMajorVersionPart(ODataVersionPart majorVersionPart) {
        this.majorVersionPart = majorVersionPart;
    }

    public ODataVersionPart getMinorVersionPart() {
        return minorVersionPart;
    }

    public void setMinorVersionPart(ODataVersionPart minorVersionPart) {
        this.minorVersionPart = minorVersionPart;
    }

    public ODataVersionPart getIncrementalVersionPart() {
        return incrementalVersionPart;
    }

    public void setIncrementalVersionPart(ODataVersionPart incrementalVersionPart) {
        this.incrementalVersionPart = incrementalVersionPart;
    }

    /**
     * Create an instance of a version.
     */
    public ODataVersion() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    private void setName(String name) {
        /*
         * Name "0" and "" are equivalent and indicate
         * version 0.
         */
        if ("0".equals(name)) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    /**
     * @return the minorVersion
     */
    public int getMinorVersion() {
        return minorVersionPart == null ? 0 : minorVersionPart.getNumber();
    }

    public String getMinorVersionModifier() {
        return minorVersionPart == null ? null : minorVersionPart.getModifier();
    }

    public int getMinorVersionModifierNumber() {
        return minorVersionPart == null ? 0 : minorVersionPart.getModifierNumber();
    }

    public ODataVersionPart getMinorVersionNamePart() {
        return minorVersionPart;
    }

    /**
     * @return the majorVersion
     */
    public int getMajorVersion() {
        return majorVersionPart == null ? 0 : majorVersionPart.getNumber();
    }

    public String getMajorVersionModifier() {
        return majorVersionPart == null ? null : majorVersionPart.getModifier();
    }

    public int getMajorVersionModifierNumber() {
        return majorVersionPart == null ? 0 : majorVersionPart.getModifierNumber();
    }

    public ODataVersionPart getMajorVersionNamePart() {
        return majorVersionPart;
    }

    /**
     * @return the incrementalVersion
     */

    public int getIncrementalVersion() {
        return incrementalVersionPart == null ? 0 : incrementalVersionPart.getNumber();
    }

    public String getIncrementalVersionModifier() {
        return incrementalVersionPart == null ? null : incrementalVersionPart.getModifier();
    }

    public int getIncrementalVersionModifierNumber() {
        return incrementalVersionPart == null ? 0 : incrementalVersionPart.getModifierNumber();
    }


    public ODataVersionPart getIncrementalVersionNamePart() {
        return incrementalVersionPart;
    }


    private boolean isEqualToNull(ODataVersionPart versionPartToCompare) {
        if (versionPartToCompare == null) {
            return true;
        } else {
            if (versionPartToCompare.getNumber() == 0) {
                /*
                 * 0 is equivalent to not set - TCWD-386
                 */
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ODataVersion version = (ODataVersion) o;

        if (majorVersionPart == null ? !isEqualToNull(version.majorVersionPart)
                : !majorVersionPart.equals(version.majorVersionPart)) {
            return false;
        }
        if (minorVersionPart == null ? !isEqualToNull(version.minorVersionPart)
                : !minorVersionPart.equals(version.minorVersionPart)) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (incrementalVersionPart == null ? !isEqualToNull(version.incrementalVersionPart)
                : !incrementalVersionPart.equals(version.incrementalVersionPart)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = HASH_CODE_MULTIPLIER * result
                + (majorVersionPart != null ? majorVersionPart.hashCode()
                : 0);
        result = HASH_CODE_MULTIPLIER * result + (minorVersionPart != null ? minorVersionPart
                .hashCode() : 0);
        result = HASH_CODE_MULTIPLIER * result + (incrementalVersionPart != null
                ? incrementalVersionPart.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
