

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for CloneOptions.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CloneOptions">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Link"/>
 *     &lt;enumeration value="KeepAllMACs"/>
 *     &lt;enumeration value="KeepNATMACs"/>
 *     &lt;enumeration value="KeepDiskNames"/>
 *     &lt;enumeration value="KeepHwUUIDs"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum CloneOptions {

    LINK("Link"),
    KEEP_ALL_MA_CS("KeepAllMACs"),
    KEEP_NATMA_CS("KeepNATMACs"),
    KEEP_DISK_NAMES("KeepDiskNames"),
    KEEP_HW_UUI_DS("KeepHwUUIDs");
    private final String value;

    CloneOptions(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CloneOptions fromValue(String v) {
        for (CloneOptions c: CloneOptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
