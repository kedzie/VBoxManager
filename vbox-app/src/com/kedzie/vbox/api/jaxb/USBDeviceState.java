package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum USBDeviceState implements Serializable{
    NOT_SUPPORTED("NotSupported"),
    UNAVAILABLE("Unavailable"),
    BUSY("Busy"),
    AVAILABLE("Available"),
    HELD("Held"),
    CAPTURED("Captured");
    private final String value;
    public String toString() {
        return value;
    }
    USBDeviceState(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static USBDeviceState fromValue(String v) {
        for (USBDeviceState c: USBDeviceState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
