package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum ProcessInputFlag implements Serializable{
    NONE("None"),
    END_OF_FILE("EndOfFile");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessInputFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessInputFlag fromValue(String v) {
        for (ProcessInputFlag c: ProcessInputFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
