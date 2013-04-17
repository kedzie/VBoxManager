package com.kedzie.vbox.api.jaxb;

public enum CloneOptions implements java.io.Serializable{
    LINK("Link"),
    KEEP_ALL_MA_CS("KeepAllMACs"),
    KEEP_NATMA_CS("KeepNATMACs"),
    KEEP_DISK_NAMES("KeepDiskNames");
    private final String value;
    public String toString() {
        return value;
    }
    CloneOptions(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static CloneOptions fromValue(String v) {
        for (CloneOptions c: CloneOptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
