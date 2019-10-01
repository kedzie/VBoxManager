

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for CPUPropertyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CPUPropertyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="PAE"/>
 *     &lt;enumeration value="LongMode"/>
 *     &lt;enumeration value="TripleFaultReset"/>
 *     &lt;enumeration value="APIC"/>
 *     &lt;enumeration value="X2APIC"/>
 *     &lt;enumeration value="IBPBOnVMExit"/>
 *     &lt;enumeration value="IBPBOnVMEntry"/>
 *     &lt;enumeration value="HWVirt"/>
 *     &lt;enumeration value="SpecCtrl"/>
 *     &lt;enumeration value="SpecCtrlByHost"/>
 *     &lt;enumeration value="L1DFlushOnEMTScheduling"/>
 *     &lt;enumeration value="L1DFlushOnVMEntry"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum CPUPropertyType {

    NULL("Null"),
    PAE("PAE"),
    LONG_MODE("LongMode"),
    TRIPLE_FAULT_RESET("TripleFaultReset"),
    APIC("APIC"),
    X_2_APIC("X2APIC"),
    IBPB_ON_VM_EXIT("IBPBOnVMExit"),
    IBPB_ON_VM_ENTRY("IBPBOnVMEntry"),
    HW_VIRT("HWVirt"),
    SPEC_CTRL("SpecCtrl"),
    SPEC_CTRL_BY_HOST("SpecCtrlByHost"),
    L_1_D_FLUSH_ON_EMT_SCHEDULING("L1DFlushOnEMTScheduling"),
    L_1_D_FLUSH_ON_VM_ENTRY("L1DFlushOnVMEntry");
    private final String value;

    CPUPropertyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CPUPropertyType fromValue(String v) {
        for (CPUPropertyType c: CPUPropertyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
