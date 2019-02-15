

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FileOpenAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileOpenAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OpenExisting"/>
 *     &lt;enumeration value="OpenOrCreate"/>
 *     &lt;enumeration value="CreateNew"/>
 *     &lt;enumeration value="CreateOrReplace"/>
 *     &lt;enumeration value="OpenExistingTruncated"/>
 *     &lt;enumeration value="AppendOrCreate"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FileOpenAction {

    OPEN_EXISTING("OpenExisting"),
    OPEN_OR_CREATE("OpenOrCreate"),
    CREATE_NEW("CreateNew"),
    CREATE_OR_REPLACE("CreateOrReplace"),
    OPEN_EXISTING_TRUNCATED("OpenExistingTruncated"),
    APPEND_OR_CREATE("AppendOrCreate");
    private final String value;

    FileOpenAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileOpenAction fromValue(String v) {
        for (FileOpenAction c: FileOpenAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
