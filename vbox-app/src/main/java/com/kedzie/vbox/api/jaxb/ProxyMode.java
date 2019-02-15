

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for ProxyMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProxyMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="System"/>
 *     &lt;enumeration value="NoProxy"/>
 *     &lt;enumeration value="Manual"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum ProxyMode {

    SYSTEM("System"),
    NO_PROXY("NoProxy"),
    MANUAL("Manual");
    private final String value;

    ProxyMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProxyMode fromValue(String v) {
        for (ProxyMode c: ProxyMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
