







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum ExecuteProcessFlag {

    
    NONE("None"),
    
    WAIT_FOR_PROCESS_START_ONLY("WaitForProcessStartOnly"),
    
    IGNORE_ORPHANED_PROCESSES("IgnoreOrphanedProcesses"),
    
    HIDDEN("Hidden"),
    
    NO_PROFILE("NoProfile");
    private final String value;

    ExecuteProcessFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExecuteProcessFlag fromValue(String v) {
        for (ExecuteProcessFlag c: ExecuteProcessFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
