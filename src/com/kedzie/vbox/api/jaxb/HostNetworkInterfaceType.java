package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum HostNetworkInterfaceType implements Serializable{
    BRIDGED("Bridged"),
    HOST_ONLY("HostOnly");
    private final String value;
    public String toString() {
        return value;
    }
    HostNetworkInterfaceType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static HostNetworkInterfaceType fromValue(String v) {
        for (HostNetworkInterfaceType c: HostNetworkInterfaceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
