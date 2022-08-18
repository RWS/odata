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
package com.sdl.odata.edm.factory.annotations;

import com.sdl.odata.api.edm.model.EnumMember;
import com.sdl.odata.api.edm.model.EnumType;
import com.sdl.odata.api.edm.model.PrimitiveType;
import com.sdl.odata.test.model.Category;
import com.sdl.odata.test.model.ExampleFlags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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

        assertThat(categoryType.getMembers().size(), is(3));
        assertThat(categoryType.getUnderlyingType(), is(PrimitiveType.INT32));
        assertFalse(categoryType.isFlags());

        EnumMember booksMember = categoryType.getMember(Category.BOOKS.name());
        assertThat(booksMember.getName(), is(Category.BOOKS.name()));
        assertThat(booksMember.getValue(), is(0L));

        EnumMember electronicsMember = categoryType.getMember(Category.ELECTRONICS.name());
        assertThat(electronicsMember.getName(), is(Category.ELECTRONICS.name()));
        assertThat(electronicsMember.getValue(), is(1L));

        EnumMember householdMember = categoryType.getMember(Category.HOUSEHOLD.name());
        assertThat(householdMember.getName(), is(Category.HOUSEHOLD.name()));
        assertThat(householdMember.getValue(), is(2L));
    }

    @Test
    public void testBuildEnumTypeFlags() {
        EnumType flagsType = factory.build(ExampleFlags.class);

        assertThat(flagsType.getMembers().size(), is(3));
        assertThat(flagsType.getUnderlyingType(), is(PrimitiveType.INT16));
        assertTrue(flagsType.isFlags());

        EnumMember hasNameMember = flagsType.getMember(ExampleFlags.HAS_NAME.name());
        assertThat(hasNameMember.getName(), is(ExampleFlags.HAS_NAME.name()));
        assertThat(hasNameMember.getValue(), is(1L));

        EnumMember hasDescriptionMember = flagsType.getMember(ExampleFlags.HAS_DESCRIPTION.name());
        assertThat(hasDescriptionMember.getName(), is(ExampleFlags.HAS_DESCRIPTION.name()));
        assertThat(hasDescriptionMember.getValue(), is(2L));

        EnumMember isSpecialMember = flagsType.getMember(ExampleFlags.IS_SPECIAL.name());
        assertThat(isSpecialMember.getName(), is(ExampleFlags.IS_SPECIAL.name()));
        assertThat(isSpecialMember.getValue(), is(4L));
    }
}
