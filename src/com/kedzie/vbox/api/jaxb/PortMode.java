package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum PortMode implements Serializable{
    DISCONNECTED("Disconnected"),
    HOST_PIPE("HostPipe"),
    HOST_DEVICE("HostDevice"),
    RAW_FILE("RawFile");
    private final String value;
    public String toString() {
        return value;
    }
    PortMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static PortMode fromValue(String v) {
        for (PortMode c: PortMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
