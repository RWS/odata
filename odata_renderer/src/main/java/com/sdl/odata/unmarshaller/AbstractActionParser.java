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
package com.sdl.odata.unmarshaller;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.model.Action;
import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.model.Parameter;
import com.sdl.odata.api.edm.model.Schema;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.util.ParameterTypeUtil;
import com.sdl.odata.api.service.ODataRequestContext;
import com.sdl.odata.api.unmarshaller.ODataUnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static com.sdl.odata.ODataRendererUtils.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Abstract Action Parser.
 */
public abstract class AbstractActionParser {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractActionParser.class);

    private ODataRequestContext requestContext;
    private EntityDataModel entityDataModel;
    private ODataUri odataUri;

    public AbstractActionParser(ODataRequestContext newRequestContext) {
        this.requestContext = checkNotNull(newRequestContext);
        this.entityDataModel = checkNotNull(requestContext.getEntityDataModel());
        this.odataUri = checkNotNull(requestContext.getUri());
    }

    /**
     * Returns the instance of Action with all parameters provided by request.
     *
     * @return The instance of Action with all parameters provided by request.
     * @throws ODataException If unable to get action
     */
    public Object getAction() throws ODataException {
        Option<String> actionNameOption = ODataUriUtil.getActionCallName(odataUri);
        if (actionNameOption.isDefined()) {
            LOG.debug("The operation is supposed to be an action");
            String actionName = actionNameOption.get();
            return parseAction(actionName);
        }

        Option<String> actionImportNameOption = ODataUriUtil.getActionImportCallName(odataUri);
        if (actionImportNameOption.isDefined()) {
            LOG.debug("The operation is supposed to be an action import");
            String actionImportName = actionImportNameOption.get();
            return parseActionImport(actionImportName);
        }

        throw new ODataUnmarshallingException("Not able to parse action / action import");
    }

    private Object parseAction(String fullyQualifiedName) throws ODataException {

        int delimiterIndex = fullyQualifiedName.lastIndexOf(".");
        if (delimiterIndex < 0) {
            throw new ODataUnmarshallingException("The action should have a fully qualified name");
        }
        String namespace = fullyQualifiedName.substring(0, delimiterIndex);
        String actionSimpleName = fullyQualifiedName.substring(delimiterIndex + 1);

        Schema schema = checkNotNull(entityDataModel.getSchema(namespace),
                "There is no schema with name: {}", namespace);

        Action action = checkNotNull(schema.getAction(actionSimpleName), "There is no action {} within schema {}",
                actionSimpleName, namespace);
        Object actionObject;
        try {
            actionObject = action.getJavaClass().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ODataUnmarshallingException("Error during instantiation of action: " + action.getName());
        }

        Set<Parameter> actionParameters = action.getParameters();
        Map<String, Object> bodyParameters;
        try {
            bodyParameters = parseRequestBody(requestContext.getRequest().getBodyText(UTF_8.name()));
        } catch (IOException e) {
            throw new ODataUnmarshallingException("Error has occurred during parameter parsing", e);
        }

        assignParametersToAction(bodyParameters, actionParameters, actionObject);

        return actionObject;
    }

    /**
     * The method is parsing request body into a map of action parameters.
     *
     * @param body The body of POST request
     * @return The map of action parameters.
     * @throws IOException If unable to parse request body
     * @throws ODataException If unable to parse request body
     */
    public abstract Map<String, Object> parseRequestBody(String body) throws IOException, ODataException;

    private void assignParametersToAction(Map<String, Object> bodyParameters, Set<Parameter> actionParameters,
                                          Object actionObject) throws ODataUnmarshallingException {
        for (Parameter parameter : actionParameters) {
            Object bodyParameter = bodyParameters.get(parameter.getName());

            if (bodyParameter == null && !parameter.isNullable()) {
                LOG.error("Error during setting a parameter {} to action object field", parameter);
                throw new ODataUnmarshallingException("Assigning null to non nullable parameter " + parameter);
            }
            Field javaField = parameter.getJavaField();

            ParameterTypeUtil.setParameter(actionObject, javaField, bodyParameter);
        }
    }

    private Object parseActionImport(String actionImportName) throws ODataException {
        ActionImport actionImport = checkNotNull(entityDataModel.getEntityContainer().getActionImport(actionImportName),
                "Not able to get the action import {} from entity container", actionImportName);

        Action action = actionImport.getAction();

        Object actionObject;
        try {
            actionObject = action.getJavaClass().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ODataUnmarshallingException("Error during instantiation of action: " + action.getName());
        }

        Set<Parameter> actionParameters = action.getParameters();
        Map<String, Object> bodyParameters;
        try {
            bodyParameters = parseRequestBody(requestContext.getRequest().getBodyText(UTF_8.name()));
        } catch (IOException e) {
            throw new ODataUnmarshallingException("Error during request body parsing", e);
        }
        assignParametersToAction(bodyParameters, actionParameters, actionObject);

        return actionObject;
    }
}
