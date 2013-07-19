package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum SymlinkReadFlag implements Serializable{
    NONE("None"),
    NO_SYMLINKS("NoSymlinks");
    private final String value;
    public String toString() {
        return value;
    }
    SymlinkReadFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static SymlinkReadFlag fromValue(String v) {
        for (SymlinkReadFlag c: SymlinkReadFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
