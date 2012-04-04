







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum PointingHidType {

        NONE("None"),
        PS_2_MOUSE("PS2Mouse"),
        USB_MOUSE("USBMouse"),
        USB_TABLET("USBTablet"),
        COMBO_MOUSE("ComboMouse");
    private final String value;

    PointingHidType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PointingHidType fromValue(String v) {
        for (PointingHidType c: PointingHidType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
