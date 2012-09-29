package com.kedzie.vbox.api.jaxb;

public enum BandwidthGroupType {
    NULL("Null"),
    DISK("Disk"),
    NETWORK("Network");
    private final String value;
    public String toString() {
        return value;
    }
    BandwidthGroupType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static BandwidthGroupType fromValue(String v) {
        for (BandwidthGroupType c: BandwidthGroupType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
