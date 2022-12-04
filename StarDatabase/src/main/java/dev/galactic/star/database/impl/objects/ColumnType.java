package dev.galactic.star.database.impl.objects;

import sun.jvm.hotspot.utilities.Bits;

import java.sql.Time;
import java.sql.Date;

public enum ColumnType {
    INT("INT", Integer.class, 11),
    FLOAT("FLOAT", Float.class, 0),
    BIT("BIT", Bits.class, 0),
    BOOL("BOOL", Boolean.class, 0),
    DATE("DATE", java.util.Date.class, 0),
    TIME("TIME", Time.class, 0),
    DATE_TIME("DATETIME", Date.class, 0),
    CHAR("CHAR", Character.class, 1),
    VARCHAR("VARCHAR", String.class, 255),
    TEXT("TEXT", String.class, 65535),
    BINARY("BINARY", Byte.class, 0);

    private final String name;
    private final Class<?> type;
    private final int defaultLength;

    ColumnType(String name, Class<?> type, int defaultLength) {
        this.name = name;
        this.type = type;
        this.defaultLength = defaultLength;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public int getDefaultLength() {
        return defaultLength;
    }
}
