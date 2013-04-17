package com.kedzie.vbox.api.jaxb;

public enum CleanupMode implements java.io.Serializable{
    UNREGISTER_ONLY("UnregisterOnly"),
    DETACH_ALL_RETURN_NONE("DetachAllReturnNone"),
    DETACH_ALL_RETURN_HARD_DISKS_ONLY("DetachAllReturnHardDisksOnly"),
    FULL("Full");
    private final String value;
    public String toString() {
        return value;
    }
    CleanupMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static CleanupMode fromValue(String v) {
        for (CleanupMode c: CleanupMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
