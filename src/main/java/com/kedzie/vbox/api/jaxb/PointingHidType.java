//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.12 at 07:46:30 PM CDT 
//


package com.kedzie.vbox.api.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PointingHidType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PointingHidType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="PS2Mouse"/>
 *     &lt;enumeration value="USBMouse"/>
 *     &lt;enumeration value="USBTablet"/>
 *     &lt;enumeration value="ComboMouse"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PointingHidType")
@XmlEnum
public enum PointingHidType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("PS2Mouse")
    PS_2_MOUSE("PS2Mouse"),
    @XmlEnumValue("USBMouse")
    USB_MOUSE("USBMouse"),
    @XmlEnumValue("USBTablet")
    USB_TABLET("USBTablet"),
    @XmlEnumValue("ComboMouse")
    COMBO_MOUSE("ComboMouse");
    private final String value;

    PointingHidType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PointingHidType fromValue(String v) {
        for (PointingHidType c: PointingHidType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
