package com.kedzie.vbox.api.jaxb;

public enum ProcessCreateFlag {
    NONE("None"),
    WAIT_FOR_PROCESS_START_ONLY("WaitForProcessStartOnly"),
    IGNORE_ORPHANED_PROCESSES("IgnoreOrphanedProcesses"),
    HIDDEN("Hidden"),
    NO_PROFILE("NoProfile"),
    WAIT_FOR_STD_OUT("WaitForStdOut"),
    WAIT_FOR_STD_ERR("WaitForStdErr"),
    EXPAND_ARGUMENTS("ExpandArguments");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessCreateFlag(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessCreateFlag fromValue(String v) {
        for (ProcessCreateFlag c: ProcessCreateFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
