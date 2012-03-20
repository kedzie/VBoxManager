







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum DeviceActivity {

    
    NULL("Null"),
    
    IDLE("Idle"),
    
    READING("Reading"),
    
    WRITING("Writing");
    private final String value;

    DeviceActivity(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeviceActivity fromValue(String v) {
        for (DeviceActivity c: DeviceActivity.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
