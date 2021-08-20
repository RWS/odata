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
package com.sdl.odata.api.parser

import com.sdl.odata.api.service.MediaType

// OData URI
case class ODataUri(serviceRoot: String, relativeUri: RelativeUri)

sealed trait RelativeUri

// Service root URI
case class ServiceRootUri(format: Option[MediaType]) extends RelativeUri

// Metadata URI ('$metadata')
case class MetadataUri(format: Option[MediaType], context: Option[ContextFragment]) extends RelativeUri

// Batch URI ('$batch')
case object BatchUri extends RelativeUri

// Entity URI ('$entity')
case class EntityUri(derivedTypeName: Option[String], options: List[QueryOption]) extends RelativeUri with QueryOptions

// Resource path URI
case class ResourcePathUri(resourcePath: ResourcePath, options: List[QueryOption]) extends RelativeUri with QueryOptions
