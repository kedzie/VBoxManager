

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for GuestMonitorStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GuestMonitorStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="Enabled"/>
 *     &lt;enumeration value="Blank"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum GuestMonitorStatus {

    DISABLED("Disabled"),
    ENABLED("Enabled"),
    BLANK("Blank");
    private final String value;

    GuestMonitorStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GuestMonitorStatus fromValue(String v) {
        for (GuestMonitorStatus c: GuestMonitorStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
