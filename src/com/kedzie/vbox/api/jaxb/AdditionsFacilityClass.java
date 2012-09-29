package com.kedzie.vbox.api.jaxb;

public enum AdditionsFacilityClass {
    NONE("None"),
    DRIVER("Driver"),
    SERVICE("Service"),
    PROGRAM("Program"),
    FEATURE("Feature"),
    THIRD_PARTY("ThirdParty"),
    ALL("All");
    private final String value;
    public String toString() {
        return value;
    }
    AdditionsFacilityClass(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AdditionsFacilityClass fromValue(String v) {
        for (AdditionsFacilityClass c: AdditionsFacilityClass.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
