

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Undefined"/>
 *     &lt;enumeration value="Opening"/>
 *     &lt;enumeration value="Open"/>
 *     &lt;enumeration value="Closing"/>
 *     &lt;enumeration value="Closed"/>
 *     &lt;enumeration value="Down"/>
 *     &lt;enumeration value="Error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileStatus {

    UNDEFINED("Undefined"),
    OPENING("Opening"),
    OPEN("Open"),
    CLOSING("Closing"),
    CLOSED("Closed"),
    DOWN("Down"),
    ERROR("Error");
    private final String value;

    FileStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileStatus fromValue(String v) {
        for (FileStatus c: FileStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
