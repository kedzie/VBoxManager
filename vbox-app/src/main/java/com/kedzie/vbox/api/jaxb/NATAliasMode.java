

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for NATAliasMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NATAliasMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AliasLog"/>
 *     &lt;enumeration value="AliasProxyOnly"/>
 *     &lt;enumeration value="AliasUseSamePorts"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum NATAliasMode {

    ALIAS_LOG("AliasLog"),
    ALIAS_PROXY_ONLY("AliasProxyOnly"),
    ALIAS_USE_SAME_PORTS("AliasUseSamePorts");
    private final String value;

    NATAliasMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NATAliasMode fromValue(String v) {
        for (NATAliasMode c: NATAliasMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
