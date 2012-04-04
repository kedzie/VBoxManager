







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 


public enum DirectoryOpenFlag {

    
    NONE("None");
    private final String value;

    DirectoryOpenFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DirectoryOpenFlag fromValue(String v) {
        for (DirectoryOpenFlag c: DirectoryOpenFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
