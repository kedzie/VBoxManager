package com.kedzie.vbox.api.jaxb;


public enum ChipsetType implements java.io.Serializable {
    NULL("Null"),
    PIIX_3("PIIX3"),
    ICH_9("ICH9");
    private final String value;
    public String toString() {
        return value;
    }
    ChipsetType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ChipsetType fromValue(String v) {
        for (ChipsetType c: ChipsetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
