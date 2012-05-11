







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum StorageBus {

        NULL("Null"),
    IDE("IDE"),
    SATA("SATA"),
    SCSI("SCSI"),
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
