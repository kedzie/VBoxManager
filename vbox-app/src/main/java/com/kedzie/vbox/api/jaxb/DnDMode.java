

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for DnDMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DnDMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="HostToGuest"/>
 *     &lt;enumeration value="GuestToHost"/>
 *     &lt;enumeration value="Bidirectional"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum DnDMode {

    DISABLED("Disabled"),
    HOST_TO_GUEST("HostToGuest"),
    GUEST_TO_HOST("GuestToHost"),
    BIDIRECTIONAL("Bidirectional");
    private final String value;

    DnDMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DnDMode fromValue(String v) {
        for (DnDMode c: DnDMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
