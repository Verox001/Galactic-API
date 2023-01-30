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

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The database API used to interact with a MySQL database.
 */
public class MySqlDb {
    private Connection connection;
    private String host;
    private int port;
    private String databaseName;
    private String username;
    private String password;
    private String parameters;


    /**
     * A constructor for the MySQL database that includes any extra queries you want to use.
     *
     * @param host         Host of the server.
     * @param port         Port of the database that is running on.
     * @param databaseName Name of the database in the database you want to interact with. Can be null or blank if you
     *                     don't want to interact just yet.
     * @param username     Username of the login details.
     * @param password     Password of the login details.
     * @param parameters   Any other attributes and values you want to use in the connection. Checkout
     *                     <a href="https://dev.mysql.com/doc/refman/8.0/en/connecting-using-uri-or-key-value-pairs.html" target="_blank">MySQL Dev website</a> for more info on the different parameters.
     */
    public MySqlDb(String host, int port, String databaseName, String username, String password, String parameters) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.parameters = parameters;
    }

    /**
     * The base constructor for the MySQL database that includes.
     *
     * @param host         Host of the server.
     * @param port         Port of MySQL that is running on.
     * @param databaseName Name of the database in the MySQL database you want to interact with. Can be null or blank
     *                     if you
     *                     don't want to interact just yet.
     * @param username     Username of the login details.
     * @param password     Password of the login details.
     */
    public MySqlDb(String host, int port, String databaseName, String username, String password) {
        this(host, port, databaseName, username, password, null);
    }

    /**
     * A constructor that takes in an already existing connection.
     *
     * @param connection The Java connection object.
     *                   <a href="https://www.javatpoint.com/example-to-connect-to-the-mysql-database" target="_blank">Here are some examples.</a>
     */
    public MySqlDb(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks whether the connection is null or isn't connected (Invalid).
     *
     * @param connection Java Connection object.
     * @return true or false.
     * @see Connection
     */
    public static boolean isInvalid(Connection connection) {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether there is a connection already established to the database.
     *
     * @return true or false.
     */
    public boolean isConnected() {
        try {
            return !MySqlDb.isInvalid(this.connection) && !this.connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The method that connects to MySQL.
     *
     * @return Current instance of the class.
     * @throws InvalidConnectionException If the program can't connect to MySQL.
     * @see MySqlDb
     */
    public MySqlDb connect() throws InvalidConnectionException {
        if (this.isConnected()) {
            throw new InvalidConnectionException("There is already a connection to the database.");
        }
        boolean parametersBlank = this.parameters == null || this.parameters.isEmpty() || parameters.matches("\\s");
        boolean databaseBlank = this.databaseName == null || this.databaseName.isEmpty() || databaseName.matches("\\s");
        String databaseQuery = databaseBlank ? "" : "/" + databaseName;
        String parameters = parametersBlank ? "" : "?" + this.parameters;
        String parameterString = databaseQuery + parameters;
        try {
            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d%s", host, port,
                    parameterString
            ), this.username, this.password);
        } catch (SQLException e) {
            throw new InvalidConnectionException("Can't connect to the database. Please check details and try again: "
                    + e.getMessage());
        }
        return this;
    }

    /**
     * Creates a database if you have the permission.
     *
     * @param databaseName Name of the database to create.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb createDatabase(String databaseName) {
        try (PreparedStatement stmt = this.connection.prepareStatement("CREATE DATABASE " + databaseName + ";")) {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
             throw new RuntimeException(e);
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Returns an array of the names of databases that exist.
     *
     * @return List&lt;String&gt; of the database names.
     */
    public List<String> getDatabases() {
        return this.getDatabases(null);
    }

    /**
     * Returns an array of the names of databases that exist, but with a certain pattern.
     *
     * @return List&lt;String&gt; of the database names.
     */
    public List<String> getDatabases(String pattern) {
        List<String> databaseNames = new ArrayList<>();
        String patternQuery = pattern == null || pattern.isEmpty() ? ";" : " WHERE " + pattern + ";";
        if (MySqlDb.isInvalid(this.connection)) {
            try {
                throw new InvalidConnectionException("Connection is invalid.");
            } catch (InvalidConnectionException e) {
                throw new RuntimeException(e);
            }
        }
        try (PreparedStatement stmt = this.connection.prepareStatement("SHOW DATABASES" + patternQuery);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int columnsCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnsCount; i++) {
                    databaseNames.add(resultSet.getObject(i).toString());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return databaseNames;
    }

    /**
     * Switches to the database specified.
     *
     * @param databaseName The name of the database to switch to.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb selectDatabase(String databaseName) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            }
            connection.setCatalog(databaseName);
            this.databaseName = databaseName;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Deletes, or drops, the database with the name specified.
     *
     * @param databaseName Name to delete.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb deleteDatabase(String databaseName) {
        try (PreparedStatement stmt = this.connection.prepareStatement("DROP DATABASE IF EXISTS " + databaseName + ";"
        )) {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * @param username Username of the user.
     * @param password Password of the user.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb createUser(String username, String password) {
        return this.createUser("", username, password);
    }

    /**
     * @param hostname The host to access it from. Put null or blank string if you want to be able to access it from
     *                 any host on the server.
     * @param username Username of the user.
     * @param password Password of the user.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb createUser(String hostname, String username, String password) {
        String userQuery = hostname == null || hostname.isEmpty() ? "'" + username + "'@'%'" :
                "'" + username + "'@'" + hostname + "'";
        try (PreparedStatement stmt = this.connection.prepareStatement("CREATE USER " + userQuery + " IDENTIFIED BY " +
                "'" + password + "';")) {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            }
            System.out.println("CREATE USER " + userQuery + " IDENTIFIED BY " +
                    "'" + password + "';");
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Deletes the user.
     *
     * @param accountNames A String varargs
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb deleteUser(String host, String... accountNames) {
        if (accountNames.length == 0) {
            throw new IllegalArgumentException("There needs to be at least one user in the varargs.");
        }
        String userHost = host == null || host.isEmpty() ? "%" : host;
        String usersQuery = Arrays.toString(accountNames)
                .replace("[", "'")
                .replace("]", "'@'" + userHost + "';")
                .replace(", ", "'@'" + userHost + "',");
        try (PreparedStatement stmt = this.connection.prepareStatement("DROP USER " + usersQuery)) {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            }
            // stmt.executeUpdate();
        } catch (SQLException | InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Returns an array of the names of users that exist, but with a certain pattern.
     *
     * @return List&lt;String&gt; of the database names.
     */
    public List<String> getUsers(String pattern) {
        List<String> databaseNames = new ArrayList<>();
        String patternQuery = pattern == null || pattern.isEmpty() ? ";" : " WHERE " + pattern + ";";
        if (MySqlDb.isInvalid(this.connection)) {
            try {
                throw new InvalidConnectionException("Connection is invalid.");
            } catch (InvalidConnectionException e) {
                throw new RuntimeException(e);
            }
        }
        try (PreparedStatement stmt = this.connection.prepareStatement("SELECT user FROM user" + patternQuery);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int columnsCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnsCount; i++) {
                    databaseNames.add(resultSet.getObject(i).toString());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return databaseNames;
    }

    /**
     * Returns an array of the names of users that exist.
     *
     * @return List&lt;String&gt; of the database names.
     */
    public List<String> getUsers() {
        return this.getUsers("");
    }

    /**
     * Changes a specific user's password. You don't need to specify a host because it defaults to % which kinda
     * means it is universal
     *
     * @param username    The username of the user who you want to change the password.
     * @param newPassword the new password.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb changeUserPassword(String username, String newPassword) {
        return this.changeUserPassword("%", username, newPassword);
    }

    /**
     * Changes a specific user's password.
     *
     * @param host        The hostname that appears after the @ in a query. Such as root@galactic-star.dev
     * @param username    The username of the user who you want to change the password.
     * @param newPassword the new password.
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb changeUserPassword(String host, String username, String newPassword) {
        String user = "'" + username + "'@'" + host + "'";

        try (PreparedStatement stmt =
                     this.connection.prepareStatement("ALTER USER " + user + " IDENTIFIED BY '" + newPassword + "';")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Method for inserting values into a MySQL table.
     *
     * @param table   Table name.
     * @param columns List of the column names.
     * @param values  List of the objects you want to insert into the columns specified.
     * @return Current class instance.
     * @see MySqlDb
     */
    public MySqlDb insert(String table, String[] columns, Object[] values) {

        String columnQuery = Arrays.toString(columns)
                .replace("[", "(")
                .replace("]", ")");
        String valueQuery = Arrays.toString(values)
                .replace("[", "('")
                .replace("]", "')")
                .replace(", ", "','");
        try (PreparedStatement stmt = this.connection.prepareStatement("INSERT INTO " + table + columnQuery + " " +
                "VALUES " + valueQuery + ";")) {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Getter for connection.
     *
     * @return Java Connection object.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Setter for the connection of the database.
     *
     * @param connection Java Connection object.
     */
    public void setConnection(Connection connection) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the connection when connection is invalid");
            }
            this.connection = connection;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the connection to the database.
     *
     * @return Current instance of MySqlDb.
     * @see MySqlDb
     */
    public MySqlDb close() throws InvalidConnectionException {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Connection is invalid.");
            } else {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Getter for connection.
     *
     * @return Java Connection object.
     */
    public String getHost() {
        return host;
    }

    /**
     * Setter for the database host.
     *
     * @param host String host.
     */
    public void setHost(String host) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the database host when connection is null or " +
                        "disconnected. Please disconnect and try again.");
            }
            this.host = host;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for the MySQL port its running on.
     *
     * @return Port of MySQL.
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter for the port of the database.
     *
     * @param port Integer port.
     */
    public void setPort(int port) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the port when connection is null or disconnected. "
                        + "Please disconnect and try again.");
            }
            this.port = port;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for database name.
     *
     * @return database name. It is null if one wasn't specified.
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Setter for database name.
     *
     * @param databaseName database name to set.
     */
    public void setDatabaseName(String databaseName) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the database name when connection is null or " +
                        "disconnected. Please disconnect and try again.");
            }
            this.databaseName = databaseName;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for MySQL username.
     *
     * @return Username as a String.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the login username.
     *
     * @param username Username as a String.
     */
    public void setUsername(String username) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the username when connection is null or " +
                        "disconnected. Please disconnect and try again.");
            }
            this.username = username;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for the MySQL password.
     *
     * @return Password as a String.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the login password.
     *
     * @param password Password to set.
     */
    public void setPassword(String password) {
        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the password when connection is null or " +
                        "disconnected. Please disconnect and try again.");
            }
            this.password = password;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for connection parameters.
     *
     * @return Parameters used in the connection.
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Setter for the connection parameters.
     *
     * @param parameters String parameters list.
     */
    public void setParameters(String parameters) {

        try {
            if (MySqlDb.isInvalid(this.connection)) {
                throw new InvalidConnectionException("Can't change the username when connection is null or " +
                        "disconnected. Please disconnect and try again.");
            }
            this.parameters = parameters;
        } catch (InvalidConnectionException e) {
            throw new RuntimeException(e);
        }
    }
}
