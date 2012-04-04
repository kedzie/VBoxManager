







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum GuestMonitorChangedEventType {

    
    ENABLED("Enabled"),
    
    DISABLED("Disabled"),
    
    NEW_ORIGIN("NewOrigin");
    private final String value;

    GuestMonitorChangedEventType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GuestMonitorChangedEventType fromValue(String v) {
        for (GuestMonitorChangedEventType c: GuestMonitorChangedEventType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
