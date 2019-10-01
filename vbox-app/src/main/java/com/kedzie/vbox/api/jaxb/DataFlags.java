

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for DataFlags.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DataFlags">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Mandatory"/>
 *     &lt;enumeration value="Expert"/>
 *     &lt;enumeration value="Array"/>
 *     &lt;enumeration value="FlagMask"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum DataFlags {

    NONE("None"),
    MANDATORY("Mandatory"),
    EXPERT("Expert"),
    ARRAY("Array"),
    FLAG_MASK("FlagMask");
    private final String value;

    DataFlags(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataFlags fromValue(String v) {
        for (DataFlags c: DataFlags.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
