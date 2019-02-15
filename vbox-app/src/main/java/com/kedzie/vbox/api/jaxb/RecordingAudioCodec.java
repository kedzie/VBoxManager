

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for RecordingAudioCodec.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RecordingAudioCodec">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="WavPCM"/>
 *     &lt;enumeration value="Opus"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum RecordingAudioCodec {

    NONE("None"),
    WAV_PCM("WavPCM"),
    OPUS("Opus");
    private final String value;

    RecordingAudioCodec(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RecordingAudioCodec fromValue(String v) {
        for (RecordingAudioCodec c: RecordingAudioCodec.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
