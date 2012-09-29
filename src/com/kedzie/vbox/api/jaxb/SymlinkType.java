package com.kedzie.vbox.api.jaxb;

public enum SymlinkType {
    UNKNOWN("Unknown"),
    DIRECTORY("Directory"),
    FILE("File");
    private final String value;
    public String toString() {
        return value;
    }
    SymlinkType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static SymlinkType fromValue(String v) {
        for (SymlinkType c: SymlinkType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
