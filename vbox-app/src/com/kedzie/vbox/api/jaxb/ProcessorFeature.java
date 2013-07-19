package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum ProcessorFeature implements Serializable{
    HW_VIRT_EX("HWVirtEx"),
    PAE("PAE"),
    LONG_MODE("LongMode"),
    NESTED_PAGING("NestedPaging");
    private final String value;
    public String toString() {
        return value;
    }
    ProcessorFeature(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ProcessorFeature fromValue(String v) {
        for (ProcessorFeature c: ProcessorFeature.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
