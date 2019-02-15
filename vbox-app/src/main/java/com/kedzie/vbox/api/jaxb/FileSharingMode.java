

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileSharingMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileSharingMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Read"/>
 *     &lt;enumeration value="Write"/>
 *     &lt;enumeration value="ReadWrite"/>
 *     &lt;enumeration value="Delete"/>
 *     &lt;enumeration value="ReadDelete"/>
 *     &lt;enumeration value="WriteDelete"/>
 *     &lt;enumeration value="All"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileSharingMode {

    READ("Read"),
    WRITE("Write"),
    READ_WRITE("ReadWrite"),
    DELETE("Delete"),
    READ_DELETE("ReadDelete"),
    WRITE_DELETE("WriteDelete"),
    ALL("All");
    private final String value;

    FileSharingMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileSharingMode fromValue(String v) {
        for (FileSharingMode c: FileSharingMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
