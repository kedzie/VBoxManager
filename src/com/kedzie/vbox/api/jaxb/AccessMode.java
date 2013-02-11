package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum AccessMode implements Serializable {
    READ_ONLY("ReadOnly"),
    READ_WRITE("ReadWrite");
    private final String value;
    public String toString() {
        return value;
    }
    AccessMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AccessMode fromValue(String v) {
        for (AccessMode c: AccessMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
