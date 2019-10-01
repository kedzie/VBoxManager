

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for PointingHIDType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PointingHIDType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="PS2Mouse"/>
 *     &lt;enumeration value="USBMouse"/>
 *     &lt;enumeration value="USBTablet"/>
 *     &lt;enumeration value="ComboMouse"/>
 *     &lt;enumeration value="USBMultiTouch"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum PointingHIDType {

    NONE("None"),
    PS_2_MOUSE("PS2Mouse"),
    USB_MOUSE("USBMouse"),
    USB_TABLET("USBTablet"),
    COMBO_MOUSE("ComboMouse"),
    USB_MULTI_TOUCH("USBMultiTouch");
    private final String value;

    PointingHIDType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PointingHIDType fromValue(String v) {
        for (PointingHIDType c: PointingHIDType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
