

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for DirectoryCreateFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DirectoryCreateFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Parents"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum DirectoryCreateFlag {

    NONE("None"),
    PARENTS("Parents");
    private final String value;

    DirectoryCreateFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DirectoryCreateFlag fromValue(String v) {
        for (DirectoryCreateFlag c: DirectoryCreateFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
