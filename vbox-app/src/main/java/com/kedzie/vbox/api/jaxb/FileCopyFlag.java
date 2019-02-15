

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileCopyFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileCopyFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="NoReplace"/>
 *     &lt;enumeration value="FollowLinks"/>
 *     &lt;enumeration value="Update"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileCopyFlag {

    NONE("None"),
    NO_REPLACE("NoReplace"),
    FOLLOW_LINKS("FollowLinks"),
    UPDATE("Update");
    private final String value;

    FileCopyFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileCopyFlag fromValue(String v) {
        for (FileCopyFlag c: FileCopyFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
