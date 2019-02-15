

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for BitmapFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BitmapFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Opaque"/>
 *     &lt;enumeration value="BGR"/>
 *     &lt;enumeration value="BGR0"/>
 *     &lt;enumeration value="BGRA"/>
 *     &lt;enumeration value="RGBA"/>
 *     &lt;enumeration value="PNG"/>
 *     &lt;enumeration value="JPEG"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum BitmapFormat {

    OPAQUE("Opaque"),
    BGR("BGR"),
    BGR_0("BGR0"),
    BGRA("BGRA"),
    RGBA("RGBA"),
    PNG("PNG"),
    JPEG("JPEG");
    private final String value;

    BitmapFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BitmapFormat fromValue(String v) {
        for (BitmapFormat c: BitmapFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
