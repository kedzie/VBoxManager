package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum ImportOptions implements Serializable{
    KEEP_ALL_MA_CS("KeepAllMACs"),
    KEEP_NATMA_CS("KeepNATMACs");
    private final String value;
    public String toString() {
        return value;
    }
    ImportOptions(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ImportOptions fromValue(String v) {
        for (ImportOptions c: ImportOptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
