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
package com.sdl.odata.processor.write;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.edm.factory.annotations.AnnotationEntityDataModelFactory;
import com.sdl.odata.processor.ODataWriteProcessorImpl;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import static com.sdl.odata.test.util.TestUtils.SERVICE_ROOT;
import static com.sdl.odata.test.util.TestUtils.createODataRequestContext;
import static com.sdl.odata.test.util.TestUtils.createODataUri;
import static com.sdl.odata.test.util.TestUtils.createODataUriWithSimpleKeyPredicate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import com.sdl.odata.api.service.ODataRequest.Method;
import com.sdl.odata.processor.ProcessorConfiguration;
import com.sdl.odata.processor.model.ODataAddress;
import com.sdl.odata.processor.model.ODataMobilePhone;
import com.sdl.odata.processor.model.ODataPerson;
import com.sdl.odata.processor.model.ODataPersonNamedKey;

/**
 *
 */
public abstract class MethodHandlerTest {
    protected DataSource dataSourceMock = mock(DataSource.class);
    protected DataSourceFactory dataSourceFactoryMock = mock(DataSourceFactory.class);
    protected ODataUri entitySetOdataURI;
    protected ODataUri entityOdataURI;
    protected ProcessorConfiguration processorConfiguration;


    protected void setup(String entitySetName) throws Exception {
        entitySetOdataURI = createODataUri(SERVICE_ROOT, entitySetName);
        entityOdataURI = createODataUriWithSimpleKeyPredicate(entitySetName);
        initMocks(ODataWriteProcessorImpl.class);
        processorConfiguration = new ProcessorConfiguration();
    }

    protected void stubForTesting(Object entity) throws ODataException {
        when(dataSourceFactoryMock.getDataSource(any(ODataRequestContext.class),
                eq(getEntityType(entity)))).thenReturn(dataSourceMock);
    }

    protected ODataRequestContext createRequestContext(Method method, boolean isEntitySetUri,
                                                       EntityDataModel entityDataModel)
            throws UnsupportedEncodingException {
        if (isEntitySetUri) {
            return createODataRequestContext(method, entitySetOdataURI, entityDataModel);
        }

        return createODataRequestContext(method, entityOdataURI, entityDataModel);
    }

    protected String getEntityType(Object entity) {
        String entityType = entity.getClass().getSimpleName();
        EdmEntity annotation = entity.getClass().getAnnotation(EdmEntity.class);
        if (annotation != null) {
            entityType = annotation.namespace() + "." + entity.getClass().getSimpleName();
        }

        return entityType;
    }

    protected EntityDataModel getEntityDataModel() throws ODataEdmException {
        return new AnnotationEntityDataModelFactory()
                .addClass(ODataAddress.class)
                .addClass(ODataMobilePhone.class)
                .addClass(ODataPerson.class)
                .buildEntityDataModel();
    }

    protected Object getEntity() {
        ODataPerson source = new ODataPerson();
        source.setId("1");
        source.setFirstName("Bill");
        source.setFamilyName("Gates");
        source.setBirthDate(LocalDate.of(1955, 10, 28));

        ODataMobilePhone phone = new ODataMobilePhone();
        phone.setId(43L);
        phone.setModel("Google Nexus 5");
        phone.setPhoneNumber("28623478600");
        source.setPrimaryPhone(phone);

        ODataAddress address = new ODataAddress();
        address.setStreetName("Woodstreet");
        address.setHouseNumber("13");
        address.setCityName("Timberville");
        address.setCountryName("Forestland");
        source.setPrimaryAddress(address);

        return source;
    }

    protected EntityDataModel getEntityDataModelForNamedKey() throws ODataEdmException {
        return new AnnotationEntityDataModelFactory()
                .addClass(ODataAddress.class)
                .addClass(ODataMobilePhone.class)
                .addClass(ODataPersonNamedKey.class)
                .buildEntityDataModel();
    }

    protected Object getEntityForNamedKey() {
        ODataPersonNamedKey source = new ODataPersonNamedKey();
        source.setId("1");
        source.setFirstName("Bill");
        source.setFamilyName("Gates");
        source.setBirthDate(LocalDate.of(1955, 10, 28));

        ODataMobilePhone phone = new ODataMobilePhone();
        phone.setId(43L);
        phone.setModel("Google Nexus 5");
        phone.setPhoneNumber("28623478600");
        source.setPrimaryPhone(phone);

        ODataAddress address = new ODataAddress();
        address.setStreetName("Woodstreet");
        address.setHouseNumber("13");
        address.setCityName("Timberville");
        address.setCountryName("Forestland");
        source.setPrimaryAddress(address);

        return source;
    }

}
