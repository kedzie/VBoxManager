package com.kedzie.vbox.api.jaxb;

public enum DataType implements java.io.Serializable{
    INT_32("Int32"),
    INT_8("Int8"),
    STRING("String");
    private final String value;
    public String toString() {
        return value;
    }
    DataType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static DataType fromValue(String v) {
        for (DataType c: DataType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
