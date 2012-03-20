







package com.kedzie.vbox.api.jaxb;






 
 
 
 
 
 
 
 
 
 
 
 
 
 


public enum NATProtocol {

    UDP,
    TCP;

    public String value() {
        return name();
    }

    public static NATProtocol fromValue(String v) {
        return valueOf(v);
    }

}
