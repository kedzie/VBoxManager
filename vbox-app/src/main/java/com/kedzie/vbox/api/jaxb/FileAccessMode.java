

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileAccessMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileAccessMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ReadOnly"/>
 *     &lt;enumeration value="WriteOnly"/>
 *     &lt;enumeration value="ReadWrite"/>
 *     &lt;enumeration value="AppendOnly"/>
 *     &lt;enumeration value="AppendRead"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileAccessMode {

    READ_ONLY("ReadOnly"),
    WRITE_ONLY("WriteOnly"),
    READ_WRITE("ReadWrite"),
    APPEND_ONLY("AppendOnly"),
    APPEND_READ("AppendRead");
    private final String value;

    FileAccessMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileAccessMode fromValue(String v) {
        for (FileAccessMode c: FileAccessMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
