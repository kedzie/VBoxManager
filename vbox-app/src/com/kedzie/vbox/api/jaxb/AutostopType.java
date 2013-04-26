package com.kedzie.vbox.api.jaxb;

public enum AutostopType implements java.io.Serializable{
    DISABLED("Disabled"),
    SAVE_STATE("SaveState"),
    POWER_OFF("PowerOff"),
    ACPI_SHUTDOWN("AcpiShutdown");
    private final String value;
    public String toString() {
        return value;
    }
    AutostopType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AutostopType fromValue(String v) {
        for (AutostopType c: AutostopType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
