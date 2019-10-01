

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for ScreenLayoutMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScreenLayoutMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Apply"/>
 *     &lt;enumeration value="Reset"/>
 *     &lt;enumeration value="Attach"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum ScreenLayoutMode {

    APPLY("Apply"),
    RESET("Reset"),
    ATTACH("Attach");
    private final String value;

    ScreenLayoutMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScreenLayoutMode fromValue(String v) {
        for (ScreenLayoutMode c: ScreenLayoutMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
