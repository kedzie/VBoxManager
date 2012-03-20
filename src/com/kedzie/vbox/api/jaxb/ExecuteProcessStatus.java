







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum ExecuteProcessStatus {

    
    UNDEFINED("Undefined"),
    
    STARTED("Started"),
    
    TERMINATED_NORMALLY("TerminatedNormally"),
    
    TERMINATED_SIGNAL("TerminatedSignal"),
    
    TERMINATED_ABNORMALLY("TerminatedAbnormally"),
    
    TIMED_OUT_KILLED("TimedOutKilled"),
    
    TIMED_OUT_ABNORMALLY("TimedOutAbnormally"),
    
    DOWN("Down"),
    
    ERROR("Error");
    private final String value;

    ExecuteProcessStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExecuteProcessStatus fromValue(String v) {
        for (ExecuteProcessStatus c: ExecuteProcessStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
