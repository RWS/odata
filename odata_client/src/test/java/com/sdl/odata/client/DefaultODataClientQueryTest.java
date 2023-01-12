/*
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
package com.sdl.odata.client;

import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.client.api.ODataClientQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Default OData Client Query Test.
 */
public class DefaultODataClientQueryTest {

    @Test
    public void createQueryWithoutWebServiceUri() {
        assertThrows(IllegalArgumentException.class, () ->
                new BasicODataClientQuery.Builder().build()
        );
    }

    @Test
    public void createSimpleQuery() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .build();

        assertEquals("EmptyEntities", query.getQuery());
    }

    @Test
    public void createQueryWithoutEntityType() {
        assertThrows(IllegalArgumentException.class, () ->
                new BasicODataClientQuery.Builder()
                        .withEntityType(null)
                        .build()
        );
    }

    @Test
    public void createQueryWithFilter() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFilterMap("Area", "Web")
                .build();
        String expectedToString = "EmptyEntities?$filter=Area eq 'Web'";
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createQueryWithMultipleFilters() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFilterMap("Area", "Web")
                .withFilterMap("Environment", "e1")
                .build();
        String expectedToString = "EmptyEntities?$filter=Area eq 'Web' and Environment eq 'e1'";
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createQueryWithExpand() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withExpandParameters("Using")
                .build();
        String expectedToString = "EmptyEntities?$expand=Using";
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createQueryWithMultipleExpands() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withExpandParameters("Using")
                .withExpandParameters("Imported")
                .build();
        String expectedToString = "EmptyEntities?$expand=Using,Imported";
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createQueryWithFilterAndExpand() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFilterMap("Area", "Web")
                .withExpandParameters("Using")
                .build();
        String expectedToString = "EmptyEntities?$filter=Area eq 'Web'&$expand=Using";
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createUnboundFunctionQuery() {
        ODataClientQuery query = new FunctionImportClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFunctionName("SampleFunction")
                .withFunctionParameter("ParamName", "ParamValue")
                .build();
        String expectedToString = "SampleFunction(ParamName=ParamValue)";
        assertEquals(expectedToString, query.getQuery());
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
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createUnboundFunctionQueryWithoutParams() {
        ODataClientQuery query = new FunctionImportClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .withFunctionName("SampleFunction")
                .build();
        String expectedToString = "SampleFunction";
        assertEquals(expectedToString, query.getQuery());
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
        assertEquals(expectedToString, query.getQuery());
    }

    @Test
    public void createUnboundFunctionQueryWithoutEntityName() {
        assertThrows(NullPointerException.class, () ->
                new FunctionImportClientQuery.Builder().withFunctionName("SampleFunction").build()
        );
    }

    @Test
    public void createUnboundFunctionQueryWithoutFunctionName() {
        assertThrows(NullPointerException.class, () ->
                new FunctionImportClientQuery.Builder().withEntityType(EmptyEntity.class).build()
        );
    }

    @Test
    public void createBoundFunctionQueryWithoutEntityName() {
        assertThrows(NullPointerException.class, () ->
                new BoundFunctionClientQuery.Builder().withFunctionName("SampleFunction").build()
        );
    }

    @Test
    public void createBoundFunctionQueryWithoutFunctionName() {
        assertThrows(NullPointerException.class, () ->
                new BoundFunctionClientQuery.Builder().withEntityType(EmptyEntity.class).build()
        );
    }

    @Test
    public void createBoundFunctionQueryWithoutBoundEntityName() {
        assertThrows(NullPointerException.class, () ->
                new BoundFunctionClientQuery.Builder()
                        .withNameSpace("Web.Sdl")
                        .withFunctionName("SampleFunction")
                        .withEntityType(EmptyEntity.class).build()
        );
    }

    @Test
    public void testToString() {
        ODataClientQuery query = new BasicODataClientQuery.Builder()
                .withEntityType(EmptyEntity.class)
                .build();
        String expectedToString = "ODataClientQuery[EmptyEntities]";
        assertEquals(expectedToString, query.toString());
    }

    /**
     * Empty Test Entity.
     */
    @EdmEntitySet("EmptyEntities")
    private static class EmptyEntity {
    }

}
