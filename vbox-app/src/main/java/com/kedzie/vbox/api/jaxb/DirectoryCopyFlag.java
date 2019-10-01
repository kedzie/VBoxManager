

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for DirectoryCopyFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DirectoryCopyFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="CopyIntoExisting"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum DirectoryCopyFlag {

    NONE("None"),
    COPY_INTO_EXISTING("CopyIntoExisting");
    private final String value;

    DirectoryCopyFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DirectoryCopyFlag fromValue(String v) {
        for (DirectoryCopyFlag c: DirectoryCopyFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
