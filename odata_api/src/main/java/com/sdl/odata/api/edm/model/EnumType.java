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
package com.sdl.odata.api.edm.model;

import java.util.List;

/**
 * OData enumeration type.
 *
 * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), chapter 10
 *
 */
public interface EnumType extends Type {

    /**
     * Returns the underlying type of the enumeration type. This must be one of the following OData primitive types:
     * {@code Edm.Byte}, {@code Edm.SByte}, {@code Edm.Int16}, {@code Edm.Int32}, {@code Edm.Int64}.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 10.1.2
     *
     * @return The underlying type of the enumeration type.
     */
    PrimitiveType getUnderlyingType();

    /**
     * Returns {@code true} if the enumeration type represents a set of flags, {@code false} otherwise.
     *
     * Reference: OData Version 4.0 Part 3: Common Schema Definition Language (CSDL), paragraph 10.1.3
     *
     * @return {@code true} if the enumeration type represents a set of flags, {@code false} otherwise.
     */
    boolean isFlags();

    /**
     * Returns the members of the enumeration type.
     *
     * @return The members of the enumeration type.
     */
    List<EnumMember> getMembers();

    /**
     * Returns the enumeration member with the specified name.
     *
     * @param name The name of the member.
     * @return The enumeration member with the specified name or {@code null} if there is no member with this name.
     */
    EnumMember getMember(String name);

    /**
     * Returns the enumeration member with the specified value.
     *
     * @param value The value of the member.
     * @return The enumeration member with the specified value or {@code null} if there is no member with this value.
     */
    EnumMember getMember(long value);
}
