







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum ImportOptions {

    
    KEEP_ALL_MA_CS("KeepAllMACs"),
    
    KEEP_NATMA_CS("KeepNATMACs");
    private final String value;

    ImportOptions(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImportOptions fromValue(String v) {
        for (ImportOptions c: ImportOptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
