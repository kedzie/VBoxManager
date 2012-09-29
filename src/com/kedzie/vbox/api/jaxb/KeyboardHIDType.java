package com.kedzie.vbox.api.jaxb;

public enum KeyboardHIDType {
    NONE("None"),
    PS_2_KEYBOARD("PS2Keyboard"),
    USB_KEYBOARD("USBKeyboard"),
    COMBO_KEYBOARD("ComboKeyboard");
    private final String value;
    public String toString() {
        return value;
    }
    KeyboardHIDType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static KeyboardHIDType fromValue(String v) {
        for (KeyboardHIDType c: KeyboardHIDType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
