







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum AdditionsFacilityStatus {

    INACTIVE("Inactive"),
    PAUSED("Paused"),
    PRE_INIT("PreInit"),
    INIT("Init"),
    ACTIVE("Active"),
    TERMINATING("Terminating"),
    TERMINATED("Terminated"),
    FAILED("Failed"),
    UNKNOWN("Unknown");
    private final String value;

    AdditionsFacilityStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AdditionsFacilityStatus fromValue(String v) {
        for (AdditionsFacilityStatus c: AdditionsFacilityStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
