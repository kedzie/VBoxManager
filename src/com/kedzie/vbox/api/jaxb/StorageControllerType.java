package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum StorageControllerType implements Serializable {
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
    public String toString() {
        return value;
    }
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
    
    public static StorageControllerType[] getValidTypes(StorageBus bus) {
    	if(bus.equals(StorageBus.SCSI)) 
    		return new StorageControllerType[] { StorageControllerType.LSI_LOGIC, StorageControllerType.BUS_LOGIC };
    	else if(bus.equals(StorageBus.IDE)) 
    		return new StorageControllerType[] { StorageControllerType.PIIX_3, StorageControllerType.PIIX_4, StorageControllerType.ICH_6 };
    	else if(bus.equals(StorageBus.SAS)) 
    		return new StorageControllerType[] { StorageControllerType.LSI_LOGIC_SAS };
    	else if(bus.equals(StorageBus.SATA)) 
    		return new StorageControllerType[] { StorageControllerType.INTEL_AHCI};
    	else if(bus.equals(StorageBus.FLOPPY)) 
    		return new StorageControllerType[] { StorageControllerType.I_82078 };
    	return new StorageControllerType[0];
    }
}
