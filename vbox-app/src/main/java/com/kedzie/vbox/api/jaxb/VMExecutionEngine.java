

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for VMExecutionEngine.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VMExecutionEngine">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NotSet"/>
 *     &lt;enumeration value="RawMode"/>
 *     &lt;enumeration value="HwVirt"/>
 *     &lt;enumeration value="NativeApi"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum VMExecutionEngine {

    NOT_SET("NotSet"),
    RAW_MODE("RawMode"),
    HW_VIRT("HwVirt"),
    NATIVE_API("NativeApi");
    private final String value;

    VMExecutionEngine(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VMExecutionEngine fromValue(String v) {
        for (VMExecutionEngine c: VMExecutionEngine.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
