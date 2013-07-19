package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum NATProtocol implements Serializable{
    UDP,
    TCP;
    public String value() {
        return name();
    }
    public static NATProtocol fromValue(String v) {
        return valueOf(v);
    }
}
