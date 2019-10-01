

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for SessionState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SessionState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Unlocked"/>
 *     &lt;enumeration value="Locked"/>
 *     &lt;enumeration value="Spawning"/>
 *     &lt;enumeration value="Unlocking"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum SessionState {

    NULL("Null"),
    UNLOCKED("Unlocked"),
    LOCKED("Locked"),
    SPAWNING("Spawning"),
    UNLOCKING("Unlocking");
    private final String value;

    SessionState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SessionState fromValue(String v) {
        for (SessionState c: SessionState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
