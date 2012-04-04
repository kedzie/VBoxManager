







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum DataFlags {

    
    NONE("None"),
    
    MANDATORY("Mandatory"),
    
    EXPERT("Expert"),
    
    ARRAY("Array"),
    
    FLAG_MASK("FlagMask");
    private final String value;

    DataFlags(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataFlags fromValue(String v) {
        for (DataFlags c: DataFlags.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
