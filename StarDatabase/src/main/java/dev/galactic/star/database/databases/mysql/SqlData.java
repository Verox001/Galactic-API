/*
 * Copyright 2022 Galactic Star Studios
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

package dev.galactic.star.database.databases.mysql;

import dev.galactic.star.database.impl.exceptions.InvalidConnectionException;

import java.sql.Connection;
import java.util.LinkedHashSet;

/**
 * The class that handles finding, and updating values.
 */
public class SqlData {
    private final LinkedHashSet<String> values = new LinkedHashSet<>();
    private final String table;
    private final Connection connection;

    /**
     * The main constructor that handles the connection and table setting.
     *
     * @param connection Your connection to the database.
     * @param table      Name of the table to search.
     */
    public SqlData(Connection connection, String table) {
        if (connection == null || MySqlDb.isInvalid(connection)) {
            try {
                throw new InvalidConnectionException("Connection is invalid.");
            } catch (InvalidConnectionException e) {
                throw new RuntimeException(e);
            }
        }
        this.connection = connection;
        this.table = table;
    }

    public SqlData append(String column, Object value, String type) {
        String stringToAdd = column + " = '" + value + "' ";
        if (this.values.size() > 0) {
            stringToAdd = type + " " + column + " = '" + value + "' ";
        }
        this.values.add(stringToAdd);
        return this;
    }

    public SqlData appendLessThan(String column, int value, String type) {
        String stringToAdd = column + " < " + value + " ";
        if (this.values.size() > 0) {
            stringToAdd = type + " " + column + " < " + value + " ";
        }
        this.values.add(stringToAdd);
        return this;
    }

    public SqlData appendGreaterThan(String column, int value, String type) {
        String stringToAdd = column + " > " + value + " ";
        if (this.values.size() > 0) {
            stringToAdd = type + " " + column + " > " + value + " ";
        }
        this.values.add(stringToAdd);
        return this;
    }

    private String buildWhereClause() {
        StringBuilder builder = new StringBuilder("WHERE ");
        for (String whereClause : this.values) {
            builder.append(whereClause);
        }
        return builder.toString();
    }

    public SqlData clearAll() {
        this.values.clear();
        return this;
    }
}
