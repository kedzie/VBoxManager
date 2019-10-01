

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for GraphicsControllerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GraphicsControllerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="VBoxVGA"/>
 *     &lt;enumeration value="VMSVGA"/>
 *     &lt;enumeration value="VBoxSVGA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum GraphicsControllerType {

    NULL("Null"),
    V_BOX_VGA("VBoxVGA"),
    VMSVGA("VMSVGA"),
    V_BOX_SVGA("VBoxSVGA");
    private final String value;

    GraphicsControllerType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GraphicsControllerType fromValue(String v) {
        for (GraphicsControllerType c: GraphicsControllerType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
