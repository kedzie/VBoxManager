

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for APICMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="APICMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="APIC"/>
 *     &lt;enumeration value="X2APIC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum APICMode {

    DISABLED("Disabled"),
    APIC("APIC"),
    X_2_APIC("X2APIC");
    private final String value;

    APICMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static APICMode fromValue(String v) {
        for (APICMode c: APICMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
