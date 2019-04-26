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
package com.sdl.odata.test.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Primitive Types Sample test model.
 */
@EdmEntity(namespace = "ODataSample", key = {"ID" })
@EdmEntitySet
public class PrimitiveTypesSample {
    /**
     * EDM Max Length.
     */
    public static final int EDM_MAX_LENGTH = 80;

    @EdmProperty(name = "ID", nullable = false)
    private long id;

    @EdmProperty(name = "Name", nullable = false, maxLength = EDM_MAX_LENGTH)
    private String name;

    @EdmProperty(name = "NullProperty", nullable = true)
    private String nullProperty;

    @EdmProperty(name = "BinaryProperty", type = "Edm.Binary")
    private byte[] binaryProperty;

    @EdmProperty(name = "BooleanProperty", nullable = false)
    private boolean booleanProperty;

    @EdmProperty(name = "ByteProperty", type = "Edm.Byte", nullable = true)
    private byte byteProperty;

    @EdmProperty(name = "DateProperty")
    private LocalDate dateProperty;

    @EdmProperty(name = "DateTimeZoneProperty")
    private ZonedDateTime datetimeZoneProperty;

    @EdmProperty(name = "DateTimeOffsetProperty")
    private OffsetDateTime dateTimeOffsetProperty;

    @EdmProperty(name = "DurationProperty")
    private Period durationProperty;

    @EdmProperty(name = "TimeOfDayProperty")
    private LocalTime timeOfDayProperty;

    @EdmProperty(name = "DecimalValueProperty")
    private BigDecimal decimalValueProperty;

    @EdmProperty(name = "DoubleProperty", nullable = false)
    private double doubleProperty;

    @EdmProperty(name = "SingleProperty", nullable = false)
    private float singleProperty;

    @EdmProperty(name = "GuidProperty")
    private UUID guidProperty;

    @EdmProperty(name = "Int16Property", nullable = false)
    private short int16Property;

    @EdmProperty(name = "Int32Property", nullable = false)
    private int int32Property;

    @EdmProperty(name = "SByteProperty", type = "Edm.SByte")
    private byte sbyteProperty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNullProperty() {
        return nullProperty;
    }

    public void setNullProperty(String nullProperty) {
        this.nullProperty = nullProperty;
    }

    public byte[] getBinaryProperty() {
        return binaryProperty;
    }

    public void setBinaryProperty(byte[] binaryProperty) {
        this.binaryProperty = binaryProperty;
    }

    public boolean isBooleanProperty() {
        return booleanProperty;
    }

    public void setBooleanProperty(boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }

    public byte getByteProperty() {
        return byteProperty;
    }

    public void setByteProperty(byte byteProperty) {
        this.byteProperty = byteProperty;
    }

    public LocalDate getDateProperty() {
        return dateProperty;
    }

    public void setDateProperty(LocalDate dateProperty) {
        this.dateProperty = dateProperty;
    }

    public OffsetDateTime getDateTimeOffsetProperty() {
        return dateTimeOffsetProperty;
    }

    public void setDateTimeOffsetProperty(OffsetDateTime dateTimeOffsetProperty) {
        this.dateTimeOffsetProperty = dateTimeOffsetProperty;
    }

    public ZonedDateTime getDatetimeZoneProperty() {
        return datetimeZoneProperty;
    }

    public void setDatetimeZoneProperty(ZonedDateTime datetimeZoneProperty) {
        this.datetimeZoneProperty = datetimeZoneProperty;
    }

    public Period getDurationProperty() {
        return durationProperty;
    }

    public void setDurationProperty(Period durationProperty) {
        this.durationProperty = durationProperty;
    }

    public LocalTime getTimeOfDayProperty() {
        return timeOfDayProperty;
    }

    public void setTimeOfDayProperty(LocalTime timeOfDayProperty) {
        this.timeOfDayProperty = timeOfDayProperty;
    }

    public BigDecimal getDecimalValueProperty() {
        return decimalValueProperty;
    }

    public void setDecimalValueProperty(float decimalValueProperty) {
        this.decimalValueProperty = new BigDecimal(decimalValueProperty);
    }

    public double getDoubleProperty() {
        return doubleProperty;
    }

    public void setDoubleProperty(double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }

    public float getSingleProperty() {
        return singleProperty;
    }

    public void setSingleProperty(float singleProperty) {
        this.singleProperty = singleProperty;
    }

    public UUID getGuidProperty() {
        return guidProperty;
    }

    public void setGuidProperty(UUID guidProperty) {
        this.guidProperty = guidProperty;
    }

    public short getInt16Property() {
        return int16Property;
    }

    public void setInt16Property(short int16Property) {
        this.int16Property = int16Property;
    }

    public int getInt32Property() {
        return int32Property;
    }

    public void setInt32Property(int int32Property) {
        this.int32Property = int32Property;
    }

    public byte getSbyteProperty() {
        return sbyteProperty;
    }

    public void setSbyteProperty(byte sbyteProperty) {
        this.sbyteProperty = sbyteProperty;
    }
}
