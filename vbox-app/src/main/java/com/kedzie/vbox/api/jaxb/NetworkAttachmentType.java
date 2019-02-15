

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for NetworkAttachmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NetworkAttachmentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="NAT"/>
 *     &lt;enumeration value="Bridged"/>
 *     &lt;enumeration value="Internal"/>
 *     &lt;enumeration value="HostOnly"/>
 *     &lt;enumeration value="Generic"/>
 *     &lt;enumeration value="NATNetwork"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum NetworkAttachmentType {

    NULL("Null"),
    NAT("NAT"),
    BRIDGED("Bridged"),
    INTERNAL("Internal"),
    HOST_ONLY("HostOnly"),
    GENERIC("Generic"),
    NAT_NETWORK("NATNetwork");
    private final String value;

    NetworkAttachmentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NetworkAttachmentType fromValue(String v) {
        for (NetworkAttachmentType c: NetworkAttachmentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
