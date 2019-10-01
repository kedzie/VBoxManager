

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for RecordingVideoCodec.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RecordingVideoCodec">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="VP8"/>
 *     &lt;enumeration value="VP9"/>
 *     &lt;enumeration value="AV1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum RecordingVideoCodec {

    NONE("None"),
    VP_8("VP8"),
    VP_9("VP9"),
    AV_1("AV1");
    private final String value;

    RecordingVideoCodec(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RecordingVideoCodec fromValue(String v) {
        for (RecordingVideoCodec c: RecordingVideoCodec.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
