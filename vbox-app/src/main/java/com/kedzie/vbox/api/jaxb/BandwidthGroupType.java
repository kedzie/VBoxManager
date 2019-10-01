

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for BandwidthGroupType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BandwidthGroupType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Disk"/>
 *     &lt;enumeration value="Network"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum BandwidthGroupType {

    NULL("Null"),
    DISK("Disk"),
    NETWORK("Network");
    private final String value;

    BandwidthGroupType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BandwidthGroupType fromValue(String v) {
        for (BandwidthGroupType c: BandwidthGroupType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
