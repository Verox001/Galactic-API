package dev.galactic.star.database.impl.manager;

import dev.galactic.star.database.databases.mysql.MySqlDb;
import dev.galactic.star.database.impl.annotations.Table;
import dev.galactic.star.database.impl.annotations.TableColumn;
import dev.galactic.star.database.impl.exceptions.AnnotationNotFoundException;
import dev.galactic.star.database.impl.exceptions.PrimaryKeyNotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * The TableManager is a static Builder Class, which provides useful
 * methods for managing classes, annotating Table.
 * For more information on how to use, view the Builder Design Pattern.
 *
 * @author Verox
 */
public class TableManager {
    private MySqlDb connection;

    public TableManager(MySqlDb connection) {
        this.connection = connection;
    }

    /**
     * Will insert the given data for the table.
     *
     * @param table needs to annotate Table
     */
    public void update(Object table) throws AnnotationNotFoundException, IllegalAccessException, PrimaryKeyNotFoundException {
        Class<?> clazz = table.getClass();

        if (!clazz.isAnnotationPresent(Table.class))
            throw new AnnotationNotFoundException("This table is not annotating from Table.");

        Table anno = clazz.getAnnotation(Table.class);

        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        String comparableColumn = "";
        String comparableValue = "";

        for (Field field : clazz.getFields()) {
            if (!field.isAnnotationPresent(TableColumn.class)) continue;
            TableColumn column = field.getAnnotation(TableColumn.class);

            columns.add(column.name());
            values.add(field.get(table).toString());

            // Check if field is a PK
            if (column.primaryKey()) {
                comparableColumn = column.name();
                comparableValue = field.get(table).toString();
            }
        }

        if (comparableColumn.isEmpty()) throw new PrimaryKeyNotFoundException("This table has no primary key");

        connection.update(
                anno.table_name(),
                columns.toArray(new String[0]),
                values.toArray(new String[0]),
                comparableColumn,
                comparableValue
        );
    }
}
