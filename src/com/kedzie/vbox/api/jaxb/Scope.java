package com.kedzie.vbox.api.jaxb;

public enum Scope {
    GLOBAL("Global"),
    MACHINE("Machine"),
    SESSION("Session");
    private final String value;
    public String toString() {
        return value;
    }
    Scope(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static Scope fromValue(String v) {
        for (Scope c: Scope.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
