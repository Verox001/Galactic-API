/*
 * Copyright 2022-2022 Galactic Star Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.galactic.star.database.impl.objects;

/**
 * The enum with all the supported Column types the database can use.
 * @author PrismoidNW
 */
public enum ColumnType {
    /**
     * Integer datatype of the database.
     */
    INT("INT"),
    /**
     * Float datatype of the database.
     */
    FLOAT("FLOAT"),
    /**
     * Bit datatype of the database.
     */
    BIT("BIT"),
    /**
     * Boolean datatype of the database.
     */
    BOOL("BOOL"),
    /**
     * Date datatype of the database.
     */
    DATE("DATE"),
    /**
     * Time datatype of the database.
     */
    TIME("TIME"),
    /**
     * DateTime datatype of the database.
     */
    DATE_TIME("DATETIME"),
    /**
     * Character datatype of the database.
     */
    CHAR("CHAR"),
    /**
     * Varchar datatype of the database.
     */
    VARCHAR("VARCHAR"),
    /**
     * Text datatype of the database.
     */
    TEXT("TEXT"),
    /**
     * Binary datatype of the database.
     */
    BINARY("BINARY");

    /**
     * The datatype's name.
     */
    private final String name;

    /**
     * The constructor used to set the name of the datatype to be used in the queries.
     *
     * @param name String
     */
    ColumnType(String name) {
        this.name = name;
    }

    /**
     * Gets the datatype's name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }
}
