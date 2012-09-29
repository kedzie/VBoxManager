package com.kedzie.vbox.api.jaxb;

public enum AdditionsRunLevelType {
    NONE("None"),
    SYSTEM("System"),
    USERLAND("Userland"),
    DESKTOP("Desktop");
    private final String value;
    public String toString() {
        return value;
    }
    AdditionsRunLevelType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AdditionsRunLevelType fromValue(String v) {
        for (AdditionsRunLevelType c: AdditionsRunLevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
