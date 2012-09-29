package com.kedzie.vbox.api.jaxb;

public enum CPUPropertyType {
    NULL("Null"),
    PAE("PAE"),
    SYNTHETIC("Synthetic");
    private final String value;
    public String toString() {
        return value;
    }
    CPUPropertyType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static CPUPropertyType fromValue(String v) {
        for (CPUPropertyType c: CPUPropertyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
