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

import java.util.UUID
import com.sdl.odata.api.edm.model.EnumMember
import java.time.{ZonedDateTime, LocalDate, LocalTime, Period}

sealed trait Literal

case object NullLiteral extends Literal

sealed trait BooleanLiteral extends Literal
case object TrueLiteral extends BooleanLiteral
case object FalseLiteral extends BooleanLiteral

case class NumberLiteral(value: BigDecimal) extends Literal

case class StringLiteral(value: String) extends Literal

case class EnumLiteral(enumTypeName: String, values: List[EnumMember]) extends Literal

case class LocalDateLiteral(date: LocalDate) extends Literal

case class LocalTimeLiteral(time: LocalTime) extends Literal

case class DateTimeLiteral(dateTime: ZonedDateTime) extends Literal

case class PeriodLiteral(period: Period) extends Literal

case class GuidLiteral(guid: UUID) extends Literal

case class BinaryLiteral(bytes: Array[Byte]) extends Literal

case object GeoLiteral extends Literal
