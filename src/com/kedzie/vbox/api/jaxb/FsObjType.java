package com.kedzie.vbox.api.jaxb;

public enum FsObjType implements java.io.Serializable{
    UNDEFINED("Undefined"),
    FIFO("FIFO"),
    DEV_CHAR("DevChar"),
    DEV_BLOCK("DevBlock"),
    DIRECTORY("Directory"),
    FILE("File"),
    SYMLINK("Symlink"),
    SOCKET("Socket"),
    WHITEOUT("Whiteout");
    private final String value;
    public String toString() {
        return value;
    }
    FsObjType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static FsObjType fromValue(String v) {
        for (FsObjType c: FsObjType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
