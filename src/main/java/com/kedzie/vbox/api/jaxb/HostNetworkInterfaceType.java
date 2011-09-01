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
 * <p>Java class for HostNetworkInterfaceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HostNetworkInterfaceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Bridged"/>
 *     &lt;enumeration value="HostOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HostNetworkInterfaceType")
@XmlEnum
public enum HostNetworkInterfaceType {

    @XmlEnumValue("Bridged")
    BRIDGED("Bridged"),
    @XmlEnumValue("HostOnly")
    HOST_ONLY("HostOnly");
    private final String value;

    HostNetworkInterfaceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HostNetworkInterfaceType fromValue(String v) {
        for (HostNetworkInterfaceType c: HostNetworkInterfaceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}