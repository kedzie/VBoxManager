

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for ParavirtProvider.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ParavirtProvider">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Default"/>
 *     &lt;enumeration value="Legacy"/>
 *     &lt;enumeration value="Minimal"/>
 *     &lt;enumeration value="HyperV"/>
 *     &lt;enumeration value="KVM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum ParavirtProvider {

    NONE("None"),
    DEFAULT("Default"),
    LEGACY("Legacy"),
    MINIMAL("Minimal"),
    HYPER_V("HyperV"),
    KVM("KVM");
    private final String value;

    ParavirtProvider(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParavirtProvider fromValue(String v) {
        for (ParavirtProvider c: ParavirtProvider.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
