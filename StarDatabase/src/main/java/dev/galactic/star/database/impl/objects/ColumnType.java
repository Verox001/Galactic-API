package dev.galactic.star.database.impl.objects;

public enum ColumnType {
    INT("INT"),
    FLOAT("FLOAT"),
    BIT("BIT"),
    BOOL("BOOL"),
    DATE("DATE"),
    TIME("TIME"),
    DATE_TIME("DATETIME"),
    CHAR("CHAR"),
    VARCHAR("VARCHAR"),
    TEXT("TEXT"),
    BINARY("BINARY");

    private final String name;

    ColumnType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
