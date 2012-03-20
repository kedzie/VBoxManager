







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum CopyFileFlag {

    
    NONE("None"),
    
    RECURSIVE("Recursive"),
    
    UPDATE("Update"),
    
    FOLLOW_LINKS("FollowLinks");
    private final String value;

    CopyFileFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CopyFileFlag fromValue(String v) {
        for (CopyFileFlag c: CopyFileFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
