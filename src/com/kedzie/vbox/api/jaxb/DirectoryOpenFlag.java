package com.kedzie.vbox.api.jaxb;

public enum DirectoryOpenFlag {
    NONE("None"),
    NO_SYMLINKS("NoSymlinks");
    private final String value;
    public String toString() {
        return value;
    }
    DirectoryOpenFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static DirectoryOpenFlag fromValue(String v) {
        for (DirectoryOpenFlag c: DirectoryOpenFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
