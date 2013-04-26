package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum USBDeviceFilterAction implements Serializable{
    NULL("Null"),
    IGNORE("Ignore"),
    HOLD("Hold");
    private final String value;
    public String toString() {
        return value;
    }
    USBDeviceFilterAction(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static USBDeviceFilterAction fromValue(String v) {
        for (USBDeviceFilterAction c: USBDeviceFilterAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
