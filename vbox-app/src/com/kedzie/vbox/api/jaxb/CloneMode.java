package com.kedzie.vbox.api.jaxb;

public enum CloneMode implements java.io.Serializable{
    MACHINE_STATE("MachineState"),
    MACHINE_AND_CHILD_STATES("MachineAndChildStates"),
    ALL_STATES("AllStates");
    private final String value;
    public String toString() {
        return value;
    }
    CloneMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static CloneMode fromValue(String v) {
        for (CloneMode c: CloneMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
