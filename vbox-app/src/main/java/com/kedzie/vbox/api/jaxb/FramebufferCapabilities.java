

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for FramebufferCapabilities.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FramebufferCapabilities">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UpdateImage"/>
 *     &lt;enumeration value="VHWA"/>
 *     &lt;enumeration value="VisibleRegion"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum FramebufferCapabilities {

    UPDATE_IMAGE("UpdateImage"),
    VHWA("VHWA"),
    VISIBLE_REGION("VisibleRegion");
    private final String value;

    FramebufferCapabilities(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FramebufferCapabilities fromValue(String v) {
        for (FramebufferCapabilities c: FramebufferCapabilities.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
