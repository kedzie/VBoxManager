package com.kedzie.vbox.api.jaxb;

public enum ProcessStatus {
    UNDEFINED("Undefined"),
    STARTING("Starting"),
    STARTED("Started"),
    PAUSED("Paused"),
    TERMINATING("Terminating"),
    TERMINATED_NORMALLY("TerminatedNormally"),
    TERMINATED_SIGNAL("TerminatedSignal"),
    TERMINATED_ABNORMALLY("TerminatedAbnormally"),
    TIMED_OUT_KILLED("TimedOutKilled"),
    TIMED_OUT_ABNORMALLY("TimedOutAbnormally"),
    DOWN("Down"),
    ERROR("Error");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessStatus(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessStatus fromValue(String v) {
        for (ProcessStatus c: ProcessStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
