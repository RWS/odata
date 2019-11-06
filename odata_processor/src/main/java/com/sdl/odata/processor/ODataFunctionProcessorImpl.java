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

import com.sdl.odata.api.ODataBadRequestException;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.ODataEdmException;
import com.sdl.odata.api.edm.model.Function;
import com.sdl.odata.api.edm.model.FunctionImport;
import com.sdl.odata.api.edm.model.Operation;
import com.sdl.odata.api.edm.model.Parameter;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.util.ParameterTypeUtil;
import com.sdl.odata.api.processor.ODataFunctionProcessor;
import com.sdl.odata.api.processor.ProcessorResult;
import com.sdl.odata.api.processor.datasource.factory.DataSourceFactory;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.service.ODataResponse;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Option;
import scala.collection.JavaConverters;

import java.util.Map;
import java.util.Set;

import static com.sdl.odata.api.service.HeaderNames.TE;
import static com.sdl.odata.api.service.HeaderNames.X_ODATA_TE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation of {@link com.sdl.odata.api.processor.ODataFunctionProcessor}.
 */
@Component
public class ODataFunctionProcessorImpl implements ODataFunctionProcessor {

    private static final Logger LOG = getLogger(ODataFunctionProcessorImpl.class);

    private static final String TRANSFER_ENCODING_CHUNKED = "chunked";

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public ProcessorResult doFunction(ODataRequestContext requestContext) throws ODataException {
        LOG.debug("Building and executing a function or function import");
        Operation operation = getFunctionOrFunctionImportOperation(requestContext);
        Object result;

        try {
            // get the default http1.1 te header value
            String te = requestContext.getRequest().getHeader(TE);
            // get custom te header value that comes unchanged in http2 env
            String xte = requestContext.getRequest().getHeader(X_ODATA_TE);

            boolean isChunkedRequest = TRANSFER_ENCODING_CHUNKED.equals(te) || TRANSFER_ENCODING_CHUNKED.equals(xte);

            if (isChunkedRequest) {
                result = operation.doStreamOperation(requestContext, dataSourceFactory);
            } else {
                result = operation.doOperation(requestContext, dataSourceFactory);
            }
        } catch (Exception e) {
            LOG.error("Unexpected exception when executing a function.", e);
            throw e;
        }

        return result == null ? new ProcessorResult(ODataResponse.Status.NO_CONTENT) :
                new ProcessorResult(ODataResponse.Status.OK, QueryResult.from(result));
    }

    private Operation getFunctionOrFunctionImportOperation(ODataRequestContext requestContext)
            throws ODataException {
        Option<String> functionCallName = ODataUriUtil.getFunctionCallName(requestContext.getUri());
        if (functionCallName.isDefined()) {
            String functionName = functionCallName.get();
            int lastNamespaceIndex = functionName.lastIndexOf('.');
            String namespace = functionName.substring(0, lastNamespaceIndex);
            String simpleFunctionName = functionName.substring(lastNamespaceIndex + 1);
            Schema schema = requestContext.getEntityDataModel().getSchema(namespace);
            if (schema == null) {
                throw new IllegalArgumentException("Could not find schema with namespace: " + namespace);
            }
            Function function = schema.getFunction(simpleFunctionName);
            Operation functionOperation = (Operation) initializeFunctionObject(function);
            fillOperationParameters(functionOperation,
                    ODataUriUtil.getFunctionCallParameters(requestContext.getUri()), function.getParameters());

            return functionOperation;
        }
        Option<String> functionImportCallName = ODataUriUtil.getFunctionImportCallName(requestContext.getUri());
        if (functionImportCallName.isDefined()) {
            String functionImportName = functionImportCallName.get();
            FunctionImport functionImport = requestContext.getEntityDataModel().getEntityContainer()
                    .getFunctionImport(functionImportName);
            Operation functionImportOperation = (Operation) initializeFunctionObject(functionImport.getFunction());
            fillOperationParameters(functionImportOperation,
                    ODataUriUtil.getFunctionImportCallParameters(requestContext.getUri()),
                    functionImport.getFunction().getParameters());
            return functionImportOperation;
        }

        throw new ODataBadRequestException("The target function or function import cannot be determined from URI");
    }

    private Object initializeFunctionObject(Function function) throws ODataEdmException {
        Object functionOperationObject;
        try {
            functionOperationObject = function.getJavaClass().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ODataEdmException("Error during initialization of OData Function instance: " +
                    function.getName());
        }
        if (!(functionOperationObject instanceof Operation)) {
            throw new ODataEdmException("The initialized OData Function with name: " + function.getName() +
                    " does not implement Operation interface");
        }

        return functionOperationObject;
    }

    private void fillOperationParameters(Object functionOperationObject,
                                         Option<scala.collection.immutable.Map<String, String>> functionCallParameters,
                                         Set<Parameter> parameters)
            throws ODataUnmarshallingException {
        StringBuilder validationMessage = new StringBuilder();
        if (functionCallParameters.isDefined() && !functionCallParameters.get().isEmpty()) {
            Map<String, String> parametersMap = JavaConverters.mapAsJavaMap(functionCallParameters.get());
            validateAndSetParameters(functionOperationObject, parameters, parametersMap, validationMessage);
        } else {
            validateAndSetParameters(functionOperationObject, parameters, null, validationMessage);
        }
        if (!"".equals(validationMessage.toString())) {
            throwValidationException(validationMessage);
        }
    }

    private void validateAndSetParameters(Object functionOperationObject, Set<Parameter> parameters,
                                          Map<String, String> parametersMap, StringBuilder validationMessage)
            throws ODataUnmarshallingException {
        if (parametersMap == null) {
            parameters.stream().filter(parameter -> !parameter.isNullable()).
                    forEach(parameter -> validationMessage.append(parameter.getName() + ", "));
        } else {
            for (Parameter parameter : parameters) {
                String parameterName = parameter.getName();
                String parameterValue = parametersMap.get(parameterName);
                if (!parameter.isNullable() && parameterValue == null) {
                    validationMessage.append(parameterName + ", ");
                }
                if (parameterValue != null) {
                    ParameterTypeUtil.setParameter(functionOperationObject, parameter.getJavaField(),
                            parameterValue);
                }
            }
        }
    }

    private void throwValidationException(StringBuilder validationMessage)
            throws ODataUnmarshallingException {
        validationMessage.insert(0, "Cannot send null value for not nullable field(s) ");
        throw new ODataUnmarshallingException(validationMessage.delete(validationMessage.
                lastIndexOf(", "), validationMessage.length()).toString());
    }

}
