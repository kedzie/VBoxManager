

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for AudioCodecType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AudioCodecType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="SB16"/>
 *     &lt;enumeration value="STAC9700"/>
 *     &lt;enumeration value="AD1980"/>
 *     &lt;enumeration value="STAC9221"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum AudioCodecType {

    NULL("Null"),
    SB_16("SB16"),
    STAC_9700("STAC9700"),
    AD_1980("AD1980"),
    STAC_9221("STAC9221");
    private final String value;

    AudioCodecType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AudioCodecType fromValue(String v) {
        for (AudioCodecType c: AudioCodecType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
