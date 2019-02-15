

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for RecordingVideoRateControlMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RecordingVideoRateControlMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CBR"/>
 *     &lt;enumeration value="VBR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum RecordingVideoRateControlMode {

    CBR,
    VBR;

    public String value() {
        return name();
    }

    public static RecordingVideoRateControlMode fromValue(String v) {
        return valueOf(v);
    }

}
