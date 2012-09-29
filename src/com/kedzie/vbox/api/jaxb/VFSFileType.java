package com.kedzie.vbox.api.jaxb;

public enum VFSFileType {
    UNKNOWN("Unknown"),
    FIFO("Fifo"),
    DEV_CHAR("DevChar"),
    DIRECTORY("Directory"),
    DEV_BLOCK("DevBlock"),
    FILE("File"),
    SYM_LINK("SymLink"),
    SOCKET("Socket"),
    WHITE_OUT("WhiteOut");
    private final String value;
    public String toString() {
        return value;
    }
    VFSFileType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static VFSFileType fromValue(String v) {
        for (VFSFileType c: VFSFileType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
