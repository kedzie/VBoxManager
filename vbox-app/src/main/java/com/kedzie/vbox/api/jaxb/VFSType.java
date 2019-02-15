

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for VFSType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VFSType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="File"/>
 *     &lt;enumeration value="Cloud"/>
 *     &lt;enumeration value="S3"/>
 *     &lt;enumeration value="WebDav"/>
 *     &lt;enumeration value="OCI"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum VFSType {

    FILE("File"),
    CLOUD("Cloud"),
    S_3("S3"),
    WEB_DAV("WebDav"),
    OCI("OCI");
    private final String value;

    VFSType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VFSType fromValue(String v) {
        for (VFSType c: VFSType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
