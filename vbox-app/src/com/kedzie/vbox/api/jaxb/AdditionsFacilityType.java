package com.kedzie.vbox.api.jaxb;

public enum AdditionsFacilityType implements java.io.Serializable{
    NONE("None"),
    V_BOX_GUEST_DRIVER("VBoxGuestDriver"),
    AUTO_LOGON("AutoLogon"),
    V_BOX_SERVICE("VBoxService"),
    V_BOX_TRAY_CLIENT("VBoxTrayClient"),
    SEAMLESS("Seamless"),
    GRAPHICS("Graphics"),
    ALL("All");
    private final String value;
    public String toString() {
        return value;
    }
    AdditionsFacilityType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AdditionsFacilityType fromValue(String v) {
        for (AdditionsFacilityType c : AdditionsFacilityType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
