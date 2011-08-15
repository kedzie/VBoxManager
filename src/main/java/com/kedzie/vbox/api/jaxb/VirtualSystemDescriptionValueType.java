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
 * <p>Java class for VirtualSystemDescriptionValueType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VirtualSystemDescriptionValueType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Reference"/>
 *     &lt;enumeration value="Original"/>
 *     &lt;enumeration value="Auto"/>
 *     &lt;enumeration value="ExtraConfig"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VirtualSystemDescriptionValueType")
@XmlEnum
public enum VirtualSystemDescriptionValueType {

    @XmlEnumValue("Reference")
    REFERENCE("Reference"),
    @XmlEnumValue("Original")
    ORIGINAL("Original"),
    @XmlEnumValue("Auto")
    AUTO("Auto"),
    @XmlEnumValue("ExtraConfig")
    EXTRA_CONFIG("ExtraConfig");
    private final String value;

    VirtualSystemDescriptionValueType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VirtualSystemDescriptionValueType fromValue(String v) {
        for (VirtualSystemDescriptionValueType c: VirtualSystemDescriptionValueType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
