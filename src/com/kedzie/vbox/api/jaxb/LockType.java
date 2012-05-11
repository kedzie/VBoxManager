







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum LockType {

    
    WRITE("Write"),
    
    SHARED("Shared");
    private final String value;

    LockType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    public String toString() { return value; }
    public static LockType fromValue(String v) {
        for (LockType c: LockType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
