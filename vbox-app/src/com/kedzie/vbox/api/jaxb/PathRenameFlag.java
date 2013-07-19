package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum PathRenameFlag implements Serializable{
    NONE("None"),
    NO_REPLACE("NoReplace"),
    REPLACE("Replace"),
    NO_SYMLINKS("NoSymlinks");
    private final String value;
    public String toString() {
        return value;
    }
    PathRenameFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static PathRenameFlag fromValue(String v) {
        for (PathRenameFlag c: PathRenameFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
