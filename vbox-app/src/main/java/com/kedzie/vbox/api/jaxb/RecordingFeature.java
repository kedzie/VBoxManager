

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for RecordingFeature.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RecordingFeature">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Video"/>
 *     &lt;enumeration value="Audio"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum RecordingFeature {

    NONE("None"),
    VIDEO("Video"),
    AUDIO("Audio");
    private final String value;

    RecordingFeature(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RecordingFeature fromValue(String v) {
        for (RecordingFeature c: RecordingFeature.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
