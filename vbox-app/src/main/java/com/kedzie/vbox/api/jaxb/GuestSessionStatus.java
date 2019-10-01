

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for GuestSessionStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GuestSessionStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Undefined"/>
 *     &lt;enumeration value="Starting"/>
 *     &lt;enumeration value="Started"/>
 *     &lt;enumeration value="Terminating"/>
 *     &lt;enumeration value="Terminated"/>
 *     &lt;enumeration value="TimedOutKilled"/>
 *     &lt;enumeration value="TimedOutAbnormally"/>
 *     &lt;enumeration value="Down"/>
 *     &lt;enumeration value="Error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum GuestSessionStatus {

    UNDEFINED("Undefined"),
    STARTING("Starting"),
    STARTED("Started"),
    TERMINATING("Terminating"),
    TERMINATED("Terminated"),
    TIMED_OUT_KILLED("TimedOutKilled"),
    TIMED_OUT_ABNORMALLY("TimedOutAbnormally"),
    DOWN("Down"),
    ERROR("Error");
    private final String value;

    GuestSessionStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GuestSessionStatus fromValue(String v) {
        for (GuestSessionStatus c: GuestSessionStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
