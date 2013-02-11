package com.kedzie.vbox.api.jaxb;

public enum FileSeekType implements java.io.Serializable{
    SET("Set"),
    CURRENT("Current");
    private final String value;
    public String toString() {
        return value;
    }
    FileSeekType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static FileSeekType fromValue(String v) {
        for (FileSeekType c: FileSeekType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
