

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for AccessMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccessMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ReadOnly"/>
 *     &lt;enumeration value="ReadWrite"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum AccessMode {

    READ_ONLY("ReadOnly"),
    READ_WRITE("ReadWrite");
    private final String value;

    AccessMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccessMode fromValue(String v) {
        for (AccessMode c: AccessMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
