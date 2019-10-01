

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for AdditionsFacilityClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AdditionsFacilityClass">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Driver"/>
 *     &lt;enumeration value="Service"/>
 *     &lt;enumeration value="Program"/>
 *     &lt;enumeration value="Feature"/>
 *     &lt;enumeration value="ThirdParty"/>
 *     &lt;enumeration value="All"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum AdditionsFacilityClass {

    NONE("None"),
    DRIVER("Driver"),
    SERVICE("Service"),
    PROGRAM("Program"),
    FEATURE("Feature"),
    THIRD_PARTY("ThirdParty"),
    ALL("All");
    private final String value;

    AdditionsFacilityClass(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AdditionsFacilityClass fromValue(String v) {
        for (AdditionsFacilityClass c: AdditionsFacilityClass.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
