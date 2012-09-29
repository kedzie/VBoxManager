package com.kedzie.vbox.api.jaxb;

public enum ProcessWaitResult {
    NONE("None"),
    START("Start"),
    TERMINATE("Terminate"),
    STATUS("Status"),
    ERROR("Error"),
    TIMEOUT("Timeout"),
    STD_IN("StdIn"),
    STD_OUT("StdOut"),
    STD_ERR("StdErr"),
    WAIT_FLAG_NOT_SUPPORTED("WaitFlagNotSupported");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessWaitResult(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessWaitResult fromValue(String v) {
        for (ProcessWaitResult c: ProcessWaitResult.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
