

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for USBConnectionSpeed.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="USBConnectionSpeed">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Low"/>
 *     &lt;enumeration value="Full"/>
 *     &lt;enumeration value="High"/>
 *     &lt;enumeration value="Super"/>
 *     &lt;enumeration value="SuperPlus"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum USBConnectionSpeed {

    NULL("Null"),
    LOW("Low"),
    FULL("Full"),
    HIGH("High"),
    SUPER("Super"),
    SUPER_PLUS("SuperPlus");
    private final String value;

    USBConnectionSpeed(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static USBConnectionSpeed fromValue(String v) {
        for (USBConnectionSpeed c: USBConnectionSpeed.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
