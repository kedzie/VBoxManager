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
 * <p>Java class for StorageBus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StorageBus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="IDE"/>
 *     &lt;enumeration value="SATA"/>
 *     &lt;enumeration value="SCSI"/>
 *     &lt;enumeration value="Floppy"/>
 *     &lt;enumeration value="SAS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StorageBus")
@XmlEnum
public enum StorageBus {

    @XmlEnumValue("Null")
    NULL("Null"),
    IDE("IDE"),
    SATA("SATA"),
    SCSI("SCSI"),
    @XmlEnumValue("Floppy")
    FLOPPY("Floppy"),
    SAS("SAS");
    private final String value;

    StorageBus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StorageBus fromValue(String v) {
        for (StorageBus c: StorageBus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
