package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum ProcessPriority implements Serializable{
    INVALID("Invalid"),
    DEFAULT("Default");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessPriority(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessPriority fromValue(String v) {
        for (ProcessPriority c: ProcessPriority.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
