

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileSeekOrigin.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileSeekOrigin">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Begin"/>
 *     &lt;enumeration value="Current"/>
 *     &lt;enumeration value="End"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileSeekOrigin {

    BEGIN("Begin"),
    CURRENT("Current"),
    END("End");
    private final String value;

    FileSeekOrigin(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileSeekOrigin fromValue(String v) {
        for (FileSeekOrigin c: FileSeekOrigin.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
