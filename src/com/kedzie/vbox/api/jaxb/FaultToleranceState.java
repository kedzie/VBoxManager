







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum FaultToleranceState {

    
    INACTIVE("Inactive"),
    
    MASTER("Master"),
    
    STANDBY("Standby");
    private final String value;

    FaultToleranceState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FaultToleranceState fromValue(String v) {
        for (FaultToleranceState c: FaultToleranceState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
