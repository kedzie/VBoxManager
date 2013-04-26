package com.kedzie.vbox.api.jaxb;


public enum AuthType implements java.io.Serializable {
    NULL("Null"),
    EXTERNAL("External"),
    GUEST("Guest");
    private final String value;
    public String toString() {
        return value;
    }
    AuthType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AuthType fromValue(String v) {
        for (AuthType c: AuthType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
