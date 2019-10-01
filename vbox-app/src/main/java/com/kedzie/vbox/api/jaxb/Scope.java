

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for Scope.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Scope">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Global"/>
 *     &lt;enumeration value="Machine"/>
 *     &lt;enumeration value="Session"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum Scope {

    GLOBAL("Global"),
    MACHINE("Machine"),
    SESSION("Session");
    private final String value;

    Scope(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Scope fromValue(String v) {
        for (Scope c: Scope.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
