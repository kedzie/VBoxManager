

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for MediumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MediumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Normal"/>
 *     &lt;enumeration value="Immutable"/>
 *     &lt;enumeration value="Writethrough"/>
 *     &lt;enumeration value="Shareable"/>
 *     &lt;enumeration value="Readonly"/>
 *     &lt;enumeration value="MultiAttach"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum MediumType {

    NORMAL("Normal"),
    IMMUTABLE("Immutable"),
    WRITETHROUGH("Writethrough"),
    SHAREABLE("Shareable"),
    READONLY("Readonly"),
    MULTI_ATTACH("MultiAttach");
    private final String value;

    MediumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MediumType fromValue(String v) {
        for (MediumType c: MediumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
