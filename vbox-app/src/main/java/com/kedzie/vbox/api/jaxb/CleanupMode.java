

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for CleanupMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CleanupMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UnregisterOnly"/>
 *     &lt;enumeration value="DetachAllReturnNone"/>
 *     &lt;enumeration value="DetachAllReturnHardDisksOnly"/>
 *     &lt;enumeration value="Full"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum CleanupMode {

    UNREGISTER_ONLY("UnregisterOnly"),
    DETACH_ALL_RETURN_NONE("DetachAllReturnNone"),
    DETACH_ALL_RETURN_HARD_DISKS_ONLY("DetachAllReturnHardDisksOnly"),
    FULL("Full");
    private final String value;

    CleanupMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CleanupMode fromValue(String v) {
        for (CleanupMode c: CleanupMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
