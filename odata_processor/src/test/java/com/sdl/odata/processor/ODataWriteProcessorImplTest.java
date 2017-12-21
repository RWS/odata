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
package com.sdl.odata.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.StringLiteral;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.HeaderNames;
import com.sdl.odata.api.service.ODataRequest;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.parser.ODataParserImpl;
import com.sdl.odata.processor.model.ODataAddress;
import com.sdl.odata.processor.model.ODataMobilePhone;
import com.sdl.odata.processor.model.ODataPerson;
import com.sdl.odata.test.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;

import static com.sdl.odata.api.service.ODataRequest.Method.DELETE;
import static com.sdl.odata.api.service.ODataRequest.Method.GET;
import static com.sdl.odata.api.service.ODataRequest.Method.PATCH;
import static com.sdl.odata.api.service.ODataRequest.Method.POST;
import static com.sdl.odata.api.service.ODataRequest.Method.PUT;
import static com.sdl.odata.api.service.ODataResponse.Status.CREATED;
import static com.sdl.odata.api.service.ODataResponse.Status.METHOD_NOT_ALLOWED;
import static com.sdl.odata.api.service.ODataResponse.Status.NO_CONTENT;
import static com.sdl.odata.api.service.ODataResponse.Status.OK;
import static com.sdl.odata.test.util.TestUtils.SERVICE_ROOT;
import static com.sdl.odata.test.util.TestUtils.createODataRequestContext;
import static com.sdl.odata.test.util.TestUtils.createODataUriEntityKeys;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * The OData Writer Processor Impl Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class ODataWriteProcessorImplTest {
    private EntityDataModel entityDataModel;
    private DataSource dataSource = mock(DataSource.class);
    private ODataRequestContext requestContext;
    private Object entity;
    private String entityKey;
    private String entityType;


    @Mock
    private DataSourceFactory dataSourceFactory;

    @InjectMocks
    private ODataWriteProcessorImpl oDataWriteProcessor;


    @Before
    public void setUp() throws Exception {
        setupEntity();

        entityDataModel = new AnnotationEntityDataModelFactory()
                .addClass(ODataPerson.class)
                .addClass(ODataAddress.class)
                .addClass(ODataMobilePhone.class)
                .buildEntityDataModel();
        initMocks(ODataWriteProcessorImpl.class);

        entityType = entity.getClass().getSimpleName();
        EdmEntity annotation = entity.getClass().getAnnotation(EdmEntity.class);
        if (annotation != null) {
            entityType = annotation.namespace() + "." + entity.getClass().getSimpleName();
        }
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        Field uriParserField = ReflectionUtils.findField(ODataWriteProcessorImpl.class, "uriParser");
        uriParserField.setAccessible(true);
        ReflectionUtils.setField(uriParserField, oDataWriteProcessor, new ODataParserImpl());
    }

    @Test
    public void testUnhandledMethod() throws ODataException, UnsupportedEncodingException {

        requestContext = createContextWithEntitySet(GET);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(METHOD_NOT_ALLOWED));
        assertThat(result.getData(), is(nullValue()));
    }

    @Test
    public void testPost() throws Exception {

        requestContext = createContextWithEntitySet(POST);
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        when(dataSource.create(requestContext.getUri(), entity, entityDataModel)).thenReturn(entity);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(CREATED));
        assertThat(result.getData(), is(entity));
        verify(dataSource, times(1)).create(requestContext.getUri(), entity, entityDataModel);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testPostWithNullEntity() throws Exception {
        requestContext = createContextWithEntitySet(POST);
        oDataWriteProcessor.write(requestContext, null);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testPostEntity() throws Exception {
        requestContext = createContextWithEntity(POST, false);
        oDataWriteProcessor.write(requestContext, entity);
    }

    @Test
    public void testDelete() throws Exception {

        requestContext = createContextForDelete();
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        ProcessorResult result = oDataWriteProcessor.write(requestContext, null);
        assertThat(result.getStatus(), is(NO_CONTENT));
        verify(dataSource, times(1)).delete(requestContext.getUri(), entityDataModel);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testDeleteWithoutNull() throws Exception {

        requestContext = createContextForDelete();

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(OK));
        assertThat(result.getData(), is(entity));
    }

    @Test(expected = ODataBadRequestException.class)
    public void testPutWithNullEntity() throws ODataException, UnsupportedEncodingException {
        requestContext = createContextWithEntitySet(PUT);
        oDataWriteProcessor.write(requestContext, null);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testPatchWithNullEntity() throws ODataException, UnsupportedEncodingException {
        requestContext = createContextWithEntitySet(PATCH);

        oDataWriteProcessor.write(requestContext, null);
    }

    @Test
    public void testWriteWithPut() throws Exception {
        requestContext = createContextWithEntity(PUT, false);
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        when(dataSource.update(requestContext.getUri(), entity, entityDataModel)).thenReturn(entity);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(OK));
        assertThat(result.getData(), is(entity));
        Map<String, String> headers = result.getHeaders();
        assertThat(headers.size(), is(1));
        assertThat(headers.get("Location"), is("http://localhost:8080/odata.svc/Persons('" + entityKey + "')"));

    }

    @Test(expected = ODataBadRequestException.class)
    public void testPutWithEntitySet() throws ODataException, UnsupportedEncodingException {
        requestContext = createContextWithEntitySet(PUT);
        oDataWriteProcessor.write(requestContext, entity);
    }

    @Test(expected = ODataBadRequestException.class)
    public void testPatchWithEntitySet() throws ODataException, UnsupportedEncodingException {
        requestContext = createContextWithEntitySet(PATCH);
        oDataWriteProcessor.write(requestContext, entity);
    }

    @Test
    public void testWriteWithPutReturnMinimal() throws Exception {
        requestContext = createContextWithEntity(PUT, true);
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        when(dataSource.update(requestContext.getUri(), entity, entityDataModel)).thenReturn(entity);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(NO_CONTENT));
        assertThat(result.getData(), is(nullValue()));
        Map<String, String> headers = result.getHeaders();
        assertThat(headers.size(), is(1));
        assertThat(headers.get("Location"), is("http://localhost:8080/odata.svc/Persons('" + entityKey + "')"));

    }

    @Test
    public void testWriteWithPatch() throws Exception {
        prepareStrategy();
        requestContext = createContextWithEntity(PATCH, false);
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        when(dataSource.update(eq(requestContext.getUri()), any(), eq(entityDataModel))).thenReturn(entity);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(OK));
        assertThat(result.getData(), notNullValue());
        Map<String, String> headers = result.getHeaders();
        assertThat(headers.size(), is(1));
        assertThat(headers.get("Location"), is("http://localhost:8080/odata.svc/Persons('" + entityKey + "')"));
    }

    @Test
    public void testWriteWithPatchReturnMinimal() throws Exception {
        prepareStrategy();
        requestContext = createContextWithEntity(PATCH, true);
        when(dataSourceFactory.getDataSource(requestContext,
                entityType)).thenReturn(dataSource);
        when(dataSource.update(eq(requestContext.getUri()), any(), eq(entityDataModel))).thenReturn(entity);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(NO_CONTENT));
        assertThat(result.getData(), is(nullValue()));
        Map<String, String> headers = result.getHeaders();
        assertThat(headers.size(), is(1));
        assertThat(headers.get("Location"), is("http://localhost:8080/odata.svc/Persons('" + entityKey + "')"));
    }

    @Test
    public void testWriteWithUnSupportedHttpMethod() throws ODataException, UnsupportedEncodingException {

        requestContext = createODataRequestContext(ODataRequest.Method.GET, entityDataModel);
        when(dataSourceFactory.getDataSource(requestContext,
                entity.getClass().getSimpleName())).thenReturn(dataSource);

        ProcessorResult result = oDataWriteProcessor.write(requestContext, entity);
        assertThat(result.getStatus(), is(METHOD_NOT_ALLOWED));
    }

    private ODataRequestContext createContextWithEntitySet(ODataRequest.Method method)
            throws UnsupportedEncodingException, ODataException {
        return createODataRequestContext(method, new ODataParserImpl().
                parseUri("http://localhost:8080/odata.svc/Persons", entityDataModel), entityDataModel);
    }

    private ODataRequestContext createContextWithEntity(ODataRequest.Method method, boolean withPrefer)
            throws UnsupportedEncodingException, ODataException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ODataRequest.Builder builder = new ODataRequest.Builder().setBodyText(objectMapper.writeValueAsString(entity),
                "UTF-8")
                .setUri(SERVICE_ROOT).setMethod(method);
        if (withPrefer) {
            builder.setHeader(HeaderNames.PREFER, "return=minimal");
        }
        return createODataRequestContext(builder.build(),
                new ODataParserImpl().parseUri("http://localhost:8080/odata.svc/Persons('" + entityKey + "')",
                        entityDataModel), entityDataModel);
    }

    private ODataRequestContext createContextForDelete() throws UnsupportedEncodingException {
        ODataUri oDataUri = createODataUriEntityKeys(SERVICE_ROOT, "Persons",
                new TestUtils.KeyValuePair("key", new StringLiteral("value")));
        return createODataRequestContext(DELETE, oDataUri, entityDataModel);
    }

    private void setupEntity() {
        ODataPerson person = new ODataPerson();
        person.setId("1");
        person.setFirstName("Bill");
        person.setFamilyName("Gates");
        person.setBirthDate(LocalDate.of(1955, 10, 28));

        ODataMobilePhone phone = new ODataMobilePhone();
        phone.setId(128008739L);
        phone.setModel("Windows Phone 8");
        phone.setPhoneNumber("1-800-123456");

        person.setPrimaryPhone(phone);
        person.setMobilePhones(Lists.newArrayList(phone));

        ODataAddress address = new ODataAddress();
        address.setStreetName("Woodstreet");
        address.setHouseNumber("13");
        address.setCityName("Timberville");
        address.setCountryName("Forestland");

        person.setPrimaryAddress(address);

        this.entity = person;
        this.entityKey = person.getId();

    }

    private void prepareStrategy() throws ODataException {
        QueryOperationStrategy queryOperationStrategy = mock(QueryOperationStrategy.class);
        QueryResult queryResult1 = QueryResult.from(entity);
        when(queryOperationStrategy.execute()).thenReturn(queryResult1);
        when(dataSourceFactory.getStrategy(any(), any(), any())).thenReturn(queryOperationStrategy);
    }
}
