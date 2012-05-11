







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum StorageControllerType {

        NULL("Null"),
        LSI_LOGIC("LsiLogic"),
        BUS_LOGIC("BusLogic"),
        INTEL_AHCI("IntelAhci"),
        PIIX_3("PIIX3"),
        PIIX_4("PIIX4"),
        ICH_6("ICH6"),
        I_82078("I82078"),
        LSI_LOGIC_SAS("LsiLogicSas");
    private final String value;

    StorageControllerType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StorageControllerType fromValue(String v) {
        for (StorageControllerType c: StorageControllerType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
