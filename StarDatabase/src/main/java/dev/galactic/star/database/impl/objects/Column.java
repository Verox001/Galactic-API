package dev.galactic.star.database.impl.objects;

/**
 * Class holding the Column's metadata.
 *
 * @author PrismoidNW
 */
public class Column {
    private String name;
    private ColumnType fieldType;
    private boolean canBeNull;
    private boolean autoIncrement;
    private int maxSize;

    /**
     * Default constructor used to set the different metadata of a column (Name, field type, autoincrement, etc).
     *
     * @param name          Database Name.
     * @param fieldType     Type of column.
     * @param canBeNull     Whether the Column can have null values.
     * @param autoIncrement Whether to auto increment the value.
     * @param maxSize       Max size of the value.
     */
    public Column(String name, ColumnType fieldType, boolean canBeNull, boolean autoIncrement,
                  int maxSize) {
        this.name = name;
        this.fieldType = fieldType;
        this.canBeNull = canBeNull;
        this.autoIncrement = autoIncrement;
        this.maxSize = maxSize;
    }

    /**
     * Gets the type of Column specified in ColumnType enum.
     *
     * @return ColumnType.
     */
    public ColumnType getFieldType() {
        return fieldType;
    }

    /**
     * Gets whether the column can have a null value.
     *
     * @return true/false stating whether the column can be null.
     */
    public boolean canBeNull() {
        return canBeNull;
    }

    /**
     * Gets the max size of the column value contents. From 0 - maxSize stated.
     *
     * @return int of the max size the value can be.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Gets whether the value can auto increment. NOTE: MUST BE A INTEGER TYPE.
     *
     * @return true/false.
     */
    public boolean autoIncrements() {
        return autoIncrement;
    }

    /**
     * Sets whether the value can auto increment. NOTE: MUST BE A INTEGER TYPE.
     *
     * @param autoIncrement true/false.
     * @return Column for method chaining.
     */
    public Column setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }

    /**
     * Gets the column name.
     *
     * @return String of the column name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a human-readable String for debugging purposes. It prints the column information.
     *
     * @return String of the column data.
     */
    @Override
    public String toString() {
        return "Column{" +
                "fieldType=" + fieldType.name() +
                ", canBeNull=" + canBeNull +
                ", autoIncrement=" + autoIncrement +
                ", maxSize=" + maxSize +
                '}';
    }
}
