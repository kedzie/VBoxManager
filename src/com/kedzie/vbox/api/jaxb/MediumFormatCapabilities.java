







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum MediumFormatCapabilities {

    
    UUID("Uuid"),
    
    CREATE_FIXED("CreateFixed"),
    
    CREATE_DYNAMIC("CreateDynamic"),
    
    CREATE_SPLIT_2_G("CreateSplit2G"),
    
    DIFFERENCING("Differencing"),
    
    ASYNCHRONOUS("Asynchronous"),
    
    FILE("File"),
    
    PROPERTIES("Properties"),
    
    TCP_NETWORKING("TcpNetworking"),
    VFS("VFS"),
    
    CAPABILITY_MASK("CapabilityMask");
    private final String value;

    MediumFormatCapabilities(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MediumFormatCapabilities fromValue(String v) {
        for (MediumFormatCapabilities c: MediumFormatCapabilities.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
