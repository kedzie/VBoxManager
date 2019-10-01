

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for RecordingVideoScalingMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RecordingVideoScalingMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="NearestNeighbor"/>
 *     &lt;enumeration value="Bilinear"/>
 *     &lt;enumeration value="Bicubic"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum RecordingVideoScalingMethod {

    NONE("None"),
    NEAREST_NEIGHBOR("NearestNeighbor"),
    BILINEAR("Bilinear"),
    BICUBIC("Bicubic");
    private final String value;

    RecordingVideoScalingMethod(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RecordingVideoScalingMethod fromValue(String v) {
        for (RecordingVideoScalingMethod c: RecordingVideoScalingMethod.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
