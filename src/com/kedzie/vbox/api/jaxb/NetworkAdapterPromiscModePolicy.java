package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum NetworkAdapterPromiscModePolicy implements Serializable{
    DENY("Deny"),
    ALLOW_NETWORK("AllowNetwork"),
    ALLOW_ALL("AllowAll");
    private final String value;
    public String toString() {
        return value;
    }
    NetworkAdapterPromiscModePolicy(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static NetworkAdapterPromiscModePolicy fromValue(String v) {
        for (NetworkAdapterPromiscModePolicy c: NetworkAdapterPromiscModePolicy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
