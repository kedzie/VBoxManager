

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileOpenExFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileOpenExFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileOpenExFlag {

    NONE("None");
    private final String value;

    FileOpenExFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileOpenExFlag fromValue(String v) {
        for (FileOpenExFlag c: FileOpenExFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
