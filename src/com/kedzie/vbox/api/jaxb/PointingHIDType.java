package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum PointingHIDType implements Serializable{
    NONE("None"),
    PS_2_MOUSE("PS2Mouse"),
    USB_MOUSE("USBMouse"),
    USB_TABLET("USBTablet"),
    COMBO_MOUSE("ComboMouse");
    private final String value;
    public String toString() {
        return value;
    }
    PointingHIDType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static PointingHIDType fromValue(String v) {
        for (PointingHIDType c: PointingHIDType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
