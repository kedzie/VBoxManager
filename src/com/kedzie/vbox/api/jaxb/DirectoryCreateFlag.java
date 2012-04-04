







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum DirectoryCreateFlag {

    
    NONE("None"),
    
    PARENTS("Parents");
    private final String value;

    DirectoryCreateFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DirectoryCreateFlag fromValue(String v) {
        for (DirectoryCreateFlag c: DirectoryCreateFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
