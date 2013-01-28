package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum NetworkAdapterType implements Serializable {
    NULL("Null"),
    AM_79_C_970_A("Am79C970A"),
    AM_79_C_973("Am79C973"),
    I_82540_EM("I82540EM"),
    I_82543_GC("I82543GC"),
    I_82545_EM("I82545EM"),
    VIRTIO("Virtio");
    private final String value;
    public String toString() {
        return value;
    }
    NetworkAdapterType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static NetworkAdapterType fromValue(String v) {
        for (NetworkAdapterType c: NetworkAdapterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
