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
package com.sdl.odata.client;

import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.client.api.ODataClientQuery;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * The Default OData Client Query Test.
 */
public class DefaultODataClientQueryTest {

    @Test(expected = IllegalArgumentException.class)
    public void createQueryWithoutWebServiceUri() {
        new BasicODataClientQuery.Builder().build();
    }

    @Test
    public void createSimpleQuery() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .build();

        assertThat(query.getQuery(), is("EmptyEntities"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createQueryWithoutEntityType() {
        new BasicODataClientQuery.Builder()
                .withEntityType(null)
                .build();
    }

    @Test
    public void createQueryWithFilter() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFilterMap("Area", "Web")
                .build();
        String expectedToString = "EmptyEntities?$filter=Area eq 'Web'";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createQueryWithMultipleFilters() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFilterMap("Area", "Web")
                .withFilterMap("Environment", "e1")
                .build();
        String expectedToString = "EmptyEntities?$filter=Area eq 'Web' and Environment eq 'e1'";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createQueryWithExpand() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withExpandParameters("Using")
                .build();
        String expectedToString = "EmptyEntities?$expand=Using";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createQueryWithMultipleExpands() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withExpandParameters("Using")
                .withExpandParameters("Imported")
                .build();
        String expectedToString = "EmptyEntities?$expand=Using,Imported";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createQueryWithFilterAndExpand() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFilterMap("Area", "Web")
                .withExpandParameters("Using")
                .build();
        String expectedToString = "EmptyEntities?$filter=Area eq 'Web'&$expand=Using";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createUnboundFunctionQuery() {
        ODataClientQuery query = new FunctionImportClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFunctionName("SampleFunction")
                .withFunctionParameter("ParamName", "ParamValue")
                .build();
        String expectedToString = "SampleFunction(ParamName=ParamValue)";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createBoundFunctionQuery() {
        ODataClientQuery query = new BoundFunctionClientQuery.Builder()
                .withBoundEntityName("SampleEntitySet")
                .withNameSpace("Web.Sdl")
                .withEntityType(EmptyEntity.class)
                .withFunctionName("SampleFunction")
                .withFunctionParameter("ParamName", "ParamValue")
                .build();
        String expectedToString = "SampleEntitySet/Web.Sdl.SampleFunction(ParamName=ParamValue)";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createUnboundFunctionQueryWithoutParams() {
        ODataClientQuery query = new FunctionImportClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFunctionName("SampleFunction")
                .build();
        String expectedToString = "SampleFunction";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test
    public void createBoundFunctionQueryWithoutParams() {
        ODataClientQuery query = new BoundFunctionClientQuery.Builder()
                .withBoundEntityName("SampleEntitySet")
                .withNameSpace("Web.Sdl")
                .withEntityType(EmptyEntity.class)
                .withFunctionName("SampleFunction")
                .build();
        String expectedToString = "SampleEntitySet/Web.Sdl.SampleFunction";
        assertThat(query.getQuery(), is(expectedToString));
    }

    @Test(expected = NullPointerException.class)
    public void createUnboundFunctionQueryWithoutEntityName() {
        new FunctionImportClientQuery.Builder().withFunctionName("SampleFunction").build();
    }

    @Test(expected = NullPointerException.class)
    public void createUnboundFunctionQueryWithoutFunctionName() {
        new FunctionImportClientQuery.Builder().withEntityType(EmptyEntity.class).build();
    }

    @Test(expected = NullPointerException.class)
    public void createBoundFunctionQueryWithoutEntityName() {
        new BoundFunctionClientQuery.Builder().withFunctionName("SampleFunction").build();
    }

    @Test(expected = NullPointerException.class)
    public void createBoundFunctionQueryWithoutFunctionName() {
        new BoundFunctionClientQuery.Builder().withEntityType(EmptyEntity.class).build();
    }

    @Test(expected = NullPointerException.class)
    public void createBoundFunctionQueryWithoutBoundEntityName() {
        new BoundFunctionClientQuery.Builder()
                .withNameSpace("Web.Sdl")
                .withFunctionName("SampleFunction")
                .withEntityType(EmptyEntity.class).build();
    }

    @Test
    public void testToString() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .build();
        String expectedToString = "ODataClientQuery[EmptyEntities]";
        assertThat(query.toString(), is(expectedToString));
    }

    /**
     * Empty Test Entity.
     */
    @EdmEntitySet("EmptyEntities")
    private class EmptyEntity {
    }

}
