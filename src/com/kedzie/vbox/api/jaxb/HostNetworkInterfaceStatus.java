







package com.kedzie.vbox.api.jaxb;







 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum HostNetworkInterfaceStatus {

    
    UNKNOWN("Unknown"),
    
    UP("Up"),
    
    DOWN("Down");
    private final String value;

    HostNetworkInterfaceStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HostNetworkInterfaceStatus fromValue(String v) {
        for (HostNetworkInterfaceStatus c: HostNetworkInterfaceStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
