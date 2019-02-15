

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for UartType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UartType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="U16450"/>
 *     &lt;enumeration value="U16550A"/>
 *     &lt;enumeration value="U16750"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum UartType {

    U_16450("U16450"),
    U_16550_A("U16550A"),
    U_16750("U16750");
    private final String value;

    UartType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UartType fromValue(String v) {
        for (UartType c: UartType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
