







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum MediumVariant {

    
    STANDARD("Standard"),
    
    VMDK_SPLIT_2_G("VmdkSplit2G"),
    
    VMDK_STREAM_OPTIMIZED("VmdkStreamOptimized"),
    
    VMDK_ESX("VmdkESX"),
    
    FIXED("Fixed"),
    
    DIFF("Diff");
    private final String value;

    MediumVariant(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MediumVariant fromValue(String v) {
        for (MediumVariant c: MediumVariant.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
