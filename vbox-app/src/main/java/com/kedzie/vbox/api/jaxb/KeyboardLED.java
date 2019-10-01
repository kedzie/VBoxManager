

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for KeyboardLED.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KeyboardLED">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NumLock"/>
 *     &lt;enumeration value="CapsLock"/>
 *     &lt;enumeration value="ScrollLock"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum KeyboardLED {

    NUM_LOCK("NumLock"),
    CAPS_LOCK("CapsLock"),
    SCROLL_LOCK("ScrollLock");
    private final String value;

    KeyboardLED(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeyboardLED fromValue(String v) {
        for (KeyboardLED c: KeyboardLED.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
