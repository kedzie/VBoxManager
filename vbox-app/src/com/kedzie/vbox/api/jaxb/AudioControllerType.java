package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum AudioControllerType implements Serializable {
    AC_97("AC97"),
    SB_16("SB16"),
    HDA("HDA");
    private final String value;
    public String toString() {
        return value;
    }
    AudioControllerType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AudioControllerType fromValue(String v) {
        for (AudioControllerType c : AudioControllerType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
