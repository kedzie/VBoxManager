package com.kedzie.vbox.api.jaxb;

public enum ProcessWaitForFlag {
    NONE("None"),
    START("Start"),
    TERMINATE("Terminate"),
    STD_IN("StdIn"),
    STD_OUT("StdOut"),
    STD_ERR("StdErr");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessWaitForFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessWaitForFlag fromValue(String v) {
        for (ProcessWaitForFlag c: ProcessWaitForFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
