/**
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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.model.EnumMember;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.ExampleFlags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AnnotationEnumTypeFactory}.
 *
 */
public class AnnotationEnumTypeFactoryTest {

    private AnnotationEnumTypeFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new AnnotationEnumTypeFactory();
    }

    @Test
    public void testBuildEnumType() {
        EnumType categoryType = factory.build(Category.class);

        assertEquals(3, categoryType.getMembers().size());
        assertEquals(PrimitiveType.INT32, categoryType.getUnderlyingType());
        assertFalse(categoryType.isFlags());

        EnumMember booksMember = categoryType.getMember(Category.BOOKS.name());
        assertEquals(Category.BOOKS.name(), booksMember.getName());
        assertEquals(0L, booksMember.getValue());

        EnumMember electronicsMember = categoryType.getMember(Category.ELECTRONICS.name());
        assertEquals(Category.ELECTRONICS.name(), electronicsMember.getName());
        assertEquals(1L, electronicsMember.getValue());

        EnumMember householdMember = categoryType.getMember(Category.HOUSEHOLD.name());
        assertEquals(Category.HOUSEHOLD.name(), householdMember.getName());
        assertEquals(2L, householdMember.getValue());
    }

    @Test
    public void testBuildEnumTypeFlags() {
        EnumType flagsType = factory.build(ExampleFlags.class);

        assertEquals(3, flagsType.getMembers().size());
        assertEquals(PrimitiveType.INT16, flagsType.getUnderlyingType());
        assertTrue(flagsType.isFlags());

        EnumMember hasNameMember = flagsType.getMember(ExampleFlags.HAS_NAME.name());
        assertEquals(ExampleFlags.HAS_NAME.name(), hasNameMember.getName());
        assertEquals(1L, hasNameMember.getValue());

        EnumMember hasDescriptionMember = flagsType.getMember(ExampleFlags.HAS_DESCRIPTION.name());
        assertEquals(ExampleFlags.HAS_DESCRIPTION.name(), hasDescriptionMember.getName());
        assertEquals(2L, hasDescriptionMember.getValue());

        EnumMember isSpecialMember = flagsType.getMember(ExampleFlags.IS_SPECIAL.name());
        assertEquals(ExampleFlags.IS_SPECIAL.name(), isSpecialMember.getName());
        assertEquals(4L, isSpecialMember.getValue());
    }
}
