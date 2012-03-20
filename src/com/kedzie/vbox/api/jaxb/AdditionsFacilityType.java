







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum AdditionsFacilityType {

    NONE("None"),
    V_BOX_GUEST_DRIVER("VBoxGuestDriver"),
    V_BOX_SERVICE("VBoxService"),
    V_BOX_TRAY_CLIENT("VBoxTrayClient"),
    SEAMLESS("Seamless"),
    GRAPHICS("Graphics"),
    ALL("All");
    private final String value;

    AdditionsFacilityType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AdditionsFacilityType fromValue(String v) {
        for (AdditionsFacilityType c: AdditionsFacilityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
