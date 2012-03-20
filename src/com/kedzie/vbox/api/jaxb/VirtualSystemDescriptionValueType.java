







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum VirtualSystemDescriptionValueType {

    REFERENCE("Reference"),
    ORIGINAL("Original"),
    AUTO("Auto"),
    EXTRA_CONFIG("ExtraConfig");
    private final String value;

    VirtualSystemDescriptionValueType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VirtualSystemDescriptionValueType fromValue(String v) {
        for (VirtualSystemDescriptionValueType c: VirtualSystemDescriptionValueType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
