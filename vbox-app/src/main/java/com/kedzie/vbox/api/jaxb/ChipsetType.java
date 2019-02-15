

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for ChipsetType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChipsetType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="PIIX3"/>
 *     &lt;enumeration value="ICH9"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum ChipsetType {

    NULL("Null"),
    PIIX_3("PIIX3"),
    ICH_9("ICH9");
    private final String value;

    ChipsetType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChipsetType fromValue(String v) {
        for (ChipsetType c: ChipsetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
