

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for PathStyle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PathStyle">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DOS"/>
 *     &lt;enumeration value="UNIX"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum PathStyle {

    DOS("DOS"),
    UNIX("UNIX"),
    UNKNOWN("Unknown");
    private final String value;

    PathStyle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PathStyle fromValue(String v) {
        for (PathStyle c: PathStyle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
