







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum GuestDirEntryType {

    
    UNKNOWN("Unknown"),
    
    DIRECTORY("Directory"),
    
    FILE("File"),
    
    SYMLINK("Symlink");
    private final String value;

    GuestDirEntryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GuestDirEntryType fromValue(String v) {
        for (GuestDirEntryType c: GuestDirEntryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
