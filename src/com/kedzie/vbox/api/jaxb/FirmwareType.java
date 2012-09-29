package com.kedzie.vbox.api.jaxb;

public enum FirmwareType {
    BIOS("BIOS"),
    EFI("EFI"),
    EFI_32("EFI32"),
    EFI_64("EFI64"),
    EFIDUAL("EFIDUAL");
    private final String value;
    public String toString() {
        return value;
    }
    FirmwareType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static FirmwareType fromValue(String v) {
        for (FirmwareType c: FirmwareType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
