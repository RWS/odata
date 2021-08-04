/*
 * Copyright (c) 2021 All Rights Reserved by the RWS Group.
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

import com.sdl.odata.api.edm.annotations.EdmActionImport;
import com.sdl.odata.api.edm.model.ActionImport;
import com.sdl.odata.edm.model.ActionImportImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Annotation Action Import Factory.
 */
public class AnnotationActionImportFactory {

    private final List<ActionImportImpl.Builder> actionImportBuilders = new ArrayList<>();

    /**
     * Adds an action import to factory.
     *
     * @param cls The action import class.
     */
    public void addActionImport(Class<?> cls) {
        EdmActionImport actionImportAnnotation = cls.getAnnotation(EdmActionImport.class);
        ActionImportImpl.Builder actionImportBuilder = new ActionImportImpl.Builder()
                .setEntitySetName(actionImportAnnotation.entitySet())
                .setActionName(actionImportAnnotation.namespace() + "." + actionImportAnnotation.action())
                .setName(actionImportAnnotation.name())
                .setJavaClass(cls);

        actionImportBuilders.add(actionImportBuilder);
    }

    /**
     * Builds action import objects based on registered classes.
     *
     * @param lookup The factory lookup.
     * @return The list of action import instances.
     */
    public Iterable<ActionImport> build(FactoryLookup lookup) {
        List<ActionImport> actionImports = new ArrayList<>();
        for (ActionImportImpl.Builder actionImportBuilder : actionImportBuilders) {
            actionImportBuilder.setEntitySet(
                    lookup.getEntitySet(actionImportBuilder.getEntitySetName()));
            actionImportBuilder.setAction(
                    lookup.getAction(actionImportBuilder.getActionName()));
            actionImports.add(actionImportBuilder.build());
        }

        return Collections.unmodifiableList(actionImports);
    }

}
