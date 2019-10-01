

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for LockType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LockType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Shared"/>
 *     &lt;enumeration value="Write"/>
 *     &lt;enumeration value="VM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum LockType {

    NULL("Null"),
    SHARED("Shared"),
    WRITE("Write"),
    VM("VM");
    private final String value;

    LockType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LockType fromValue(String v) {
        for (LockType c: LockType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
