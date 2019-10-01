

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for MediumState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MediumState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NotCreated"/>
 *     &lt;enumeration value="Created"/>
 *     &lt;enumeration value="LockedRead"/>
 *     &lt;enumeration value="LockedWrite"/>
 *     &lt;enumeration value="Inaccessible"/>
 *     &lt;enumeration value="Creating"/>
 *     &lt;enumeration value="Deleting"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum MediumState {

    NOT_CREATED("NotCreated"),
    CREATED("Created"),
    LOCKED_READ("LockedRead"),
    LOCKED_WRITE("LockedWrite"),
    INACCESSIBLE("Inaccessible"),
    CREATING("Creating"),
    DELETING("Deleting");
    private final String value;

    MediumState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MediumState fromValue(String v) {
        for (MediumState c: MediumState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
