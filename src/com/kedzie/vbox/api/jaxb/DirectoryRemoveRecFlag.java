package com.kedzie.vbox.api.jaxb;

public enum DirectoryRemoveRecFlag {
    NONE("None"),
    CONTENT_AND_DIR("ContentAndDir"),
    CONTENT_ONLY("ContentOnly");
    private final String value;
    public String toString() {
        return value;
    }
    DirectoryRemoveRecFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static DirectoryRemoveRecFlag fromValue(String v) {
        for (DirectoryRemoveRecFlag c: DirectoryRemoveRecFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
