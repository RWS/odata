/*
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
package com.sdl.odata.api.processor.link

import com.sdl.odata.api.edm.model.{NavigationProperty, EntityType}

import scala.beans.BeanProperty

/**
 * A link from a navigation property in an entity to another entity.
 *
 * @param fromEntityType The entity type of the entity that contains the navigation property.
 * @param fromNavigationProperty The navigation property that holds the link.
 * @param fromEntityKey The key of the entity (an instance of `fromEntityType`) that holds the link.
 * @param toEntityKey The key of the entity to link to.
 */
case class ODataLink(@BeanProperty fromEntityType: EntityType,
                     @BeanProperty fromNavigationProperty: NavigationProperty,
                     @BeanProperty fromEntityKey: Map[String, AnyRef],
                     @BeanProperty toEntityKey: Map[String, AnyRef])
