package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum HostNetworkInterfaceStatus implements Serializable{
    UNKNOWN("Unknown"),
    UP("Up"),
    DOWN("Down");
    private final String value;
    public String toString() {
        return value;
    }
    HostNetworkInterfaceStatus(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static HostNetworkInterfaceStatus fromValue(String v) {
        for (HostNetworkInterfaceStatus c: HostNetworkInterfaceStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
