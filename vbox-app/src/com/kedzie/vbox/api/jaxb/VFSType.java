package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;



public enum VFSType implements Serializable{
    FILE("File"),
    CLOUD("Cloud"),
    S_3("S3"),
    WEB_DAV("WebDav");
    private final String value;
    public String toString() {
        return value;
    }
    VFSType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static VFSType fromValue(String v) {
        for (VFSType c: VFSType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
