

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for DhcpOptEncoding.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DhcpOptEncoding">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Legacy"/>
 *     &lt;enumeration value="Hex"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum DhcpOptEncoding {

    LEGACY("Legacy"),
    HEX("Hex");
    private final String value;

    DhcpOptEncoding(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DhcpOptEncoding fromValue(String v) {
        for (DhcpOptEncoding c: DhcpOptEncoding.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
