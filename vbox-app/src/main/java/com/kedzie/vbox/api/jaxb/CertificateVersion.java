

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for CertificateVersion.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CertificateVersion">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="V1"/>
 *     &lt;enumeration value="V2"/>
 *     &lt;enumeration value="V3"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum CertificateVersion {

    V_1("V1"),
    V_2("V2"),
    V_3("V3"),
    UNKNOWN("Unknown");
    private final String value;

    CertificateVersion(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CertificateVersion fromValue(String v) {
        for (CertificateVersion c: CertificateVersion.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
