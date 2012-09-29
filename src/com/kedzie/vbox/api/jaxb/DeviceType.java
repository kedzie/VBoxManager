package com.kedzie.vbox.api.jaxb;

public enum DeviceType {
    NULL("Null"),
    FLOPPY("Floppy"),
    DVD("DVD"),
    HARD_DISK("HardDisk"),
    NETWORK("Network"),
    USB("USB"),
    SHARED_FOLDER("SharedFolder");
    private final String value;
    public String toString() {
        return value;
    }
    DeviceType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static DeviceType fromValue(String v) {
        for (DeviceType c: DeviceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
