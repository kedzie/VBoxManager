

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for HWVirtExPropertyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HWVirtExPropertyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Enabled"/>
 *     &lt;enumeration value="VPID"/>
 *     &lt;enumeration value="NestedPaging"/>
 *     &lt;enumeration value="UnrestrictedExecution"/>
 *     &lt;enumeration value="LargePages"/>
 *     &lt;enumeration value="Force"/>
 *     &lt;enumeration value="UseNativeApi"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum HWVirtExPropertyType {

    NULL("Null"),
    ENABLED("Enabled"),
    VPID("VPID"),
    NESTED_PAGING("NestedPaging"),
    UNRESTRICTED_EXECUTION("UnrestrictedExecution"),
    LARGE_PAGES("LargePages"),
    FORCE("Force"),
    USE_NATIVE_API("UseNativeApi");
    private final String value;

    HWVirtExPropertyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HWVirtExPropertyType fromValue(String v) {
        for (HWVirtExPropertyType c: HWVirtExPropertyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
