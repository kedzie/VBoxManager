







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum KeyboardHidType {

    
    NONE("None"),
    
    PS_2_KEYBOARD("PS2Keyboard"),
    
    USB_KEYBOARD("USBKeyboard"),
    
    COMBO_KEYBOARD("ComboKeyboard");
    private final String value;

    KeyboardHidType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeyboardHidType fromValue(String v) {
        for (KeyboardHidType c: KeyboardHidType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
