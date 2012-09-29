package com.kedzie.vbox.api.jaxb;

public enum NetworkAttachmentType {
    NULL("Null"),
    NAT("NAT"),
    BRIDGED("Bridged"),
    INTERNAL("Internal"),
    HOST_ONLY("HostOnly"),
    GENERIC("Generic");
    private final String value;
    public String toString() {
        return value;
    }
    NetworkAttachmentType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static NetworkAttachmentType fromValue(String v) {
        for (NetworkAttachmentType c: NetworkAttachmentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
