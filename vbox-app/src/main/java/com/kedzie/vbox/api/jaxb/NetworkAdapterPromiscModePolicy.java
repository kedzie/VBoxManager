

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for NetworkAdapterPromiscModePolicy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NetworkAdapterPromiscModePolicy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Deny"/>
 *     &lt;enumeration value="AllowNetwork"/>
 *     &lt;enumeration value="AllowAll"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum NetworkAdapterPromiscModePolicy {

    DENY("Deny"),
    ALLOW_NETWORK("AllowNetwork"),
    ALLOW_ALL("AllowAll");
    private final String value;

    NetworkAdapterPromiscModePolicy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NetworkAdapterPromiscModePolicy fromValue(String v) {
        for (NetworkAdapterPromiscModePolicy c: NetworkAdapterPromiscModePolicy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
