package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum HWVirtExPropertyType implements Serializable{
    NULL("Null"),
    ENABLED("Enabled"),
    EXCLUSIVE("Exclusive"),
    VPID("VPID"),
    NESTED_PAGING("NestedPaging"),
    LARGE_PAGES("LargePages"),
    FORCE("Force");
    private final String value;
    public String toString() {
        return value;
    }
    HWVirtExPropertyType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static HWVirtExPropertyType fromValue(String v) {
        for (HWVirtExPropertyType c: HWVirtExPropertyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
