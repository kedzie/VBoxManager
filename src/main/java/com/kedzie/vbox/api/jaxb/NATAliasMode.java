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
 * <p>Java class for NATAliasMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NATAliasMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AliasLog"/>
 *     &lt;enumeration value="AliasProxyOnly"/>
 *     &lt;enumeration value="AliasUseSamePorts"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NATAliasMode")
@XmlEnum
public enum NATAliasMode {

    @XmlEnumValue("AliasLog")
    ALIAS_LOG("AliasLog"),
    @XmlEnumValue("AliasProxyOnly")
    ALIAS_PROXY_ONLY("AliasProxyOnly"),
    @XmlEnumValue("AliasUseSamePorts")
    ALIAS_USE_SAME_PORTS("AliasUseSamePorts");
    private final String value;

    NATAliasMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NATAliasMode fromValue(String v) {
        for (NATAliasMode c: NATAliasMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
