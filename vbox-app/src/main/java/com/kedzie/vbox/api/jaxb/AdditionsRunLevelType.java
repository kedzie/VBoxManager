

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for AdditionsRunLevelType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AdditionsRunLevelType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="System"/>
 *     &lt;enumeration value="Userland"/>
 *     &lt;enumeration value="Desktop"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum AdditionsRunLevelType {

    NONE("None"),
    SYSTEM("System"),
    USERLAND("Userland"),
    DESKTOP("Desktop");
    private final String value;

    AdditionsRunLevelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AdditionsRunLevelType fromValue(String v) {
        for (AdditionsRunLevelType c: AdditionsRunLevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
