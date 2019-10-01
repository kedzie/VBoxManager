

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for PartitionTableType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PartitionTableType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MBR"/>
 *     &lt;enumeration value="GPT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum PartitionTableType {

    MBR,
    GPT;

    public String value() {
        return name();
    }

    public static PartitionTableType fromValue(String v) {
        return valueOf(v);
    }

}
