

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for HostNetworkInterfaceMediumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HostNetworkInterfaceMediumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Ethernet"/>
 *     &lt;enumeration value="PPP"/>
 *     &lt;enumeration value="SLIP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum HostNetworkInterfaceMediumType {

    UNKNOWN("Unknown"),
    ETHERNET("Ethernet"),
    PPP("PPP"),
    SLIP("SLIP");
    private final String value;

    HostNetworkInterfaceMediumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HostNetworkInterfaceMediumType fromValue(String v) {
        for (HostNetworkInterfaceMediumType c: HostNetworkInterfaceMediumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
