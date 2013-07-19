package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum HostNetworkInterfaceMediumType implements Serializable{
    UNKNOWN("Unknown"),
    ETHERNET("Ethernet"),
    PPP("PPP"),
    SLIP("SLIP");
    private final String value;
    public String toString() {
        return value;
    }
    HostNetworkInterfaceMediumType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static HostNetworkInterfaceMediumType fromValue(String v) {
        for (HostNetworkInterfaceMediumType c: HostNetworkInterfaceMediumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
