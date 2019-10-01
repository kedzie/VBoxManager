

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for GuestUserState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GuestUserState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="LoggedIn"/>
 *     &lt;enumeration value="LoggedOut"/>
 *     &lt;enumeration value="Locked"/>
 *     &lt;enumeration value="Unlocked"/>
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="Idle"/>
 *     &lt;enumeration value="InUse"/>
 *     &lt;enumeration value="Created"/>
 *     &lt;enumeration value="Deleted"/>
 *     &lt;enumeration value="SessionChanged"/>
 *     &lt;enumeration value="CredentialsChanged"/>
 *     &lt;enumeration value="RoleChanged"/>
 *     &lt;enumeration value="GroupAdded"/>
 *     &lt;enumeration value="GroupRemoved"/>
 *     &lt;enumeration value="Elevated"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum GuestUserState {

    UNKNOWN("Unknown"),
    LOGGED_IN("LoggedIn"),
    LOGGED_OUT("LoggedOut"),
    LOCKED("Locked"),
    UNLOCKED("Unlocked"),
    DISABLED("Disabled"),
    IDLE("Idle"),
    IN_USE("InUse"),
    CREATED("Created"),
    DELETED("Deleted"),
    SESSION_CHANGED("SessionChanged"),
    CREDENTIALS_CHANGED("CredentialsChanged"),
    ROLE_CHANGED("RoleChanged"),
    GROUP_ADDED("GroupAdded"),
    GROUP_REMOVED("GroupRemoved"),
    ELEVATED("Elevated");
    private final String value;

    GuestUserState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GuestUserState fromValue(String v) {
        for (GuestUserState c: GuestUserState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
