package com.kedzie.vbox.api.jaxb;

public enum ClipboardMode implements java.io.Serializable{
    DISABLED("Disabled"),
    HOST_TO_GUEST("HostToGuest"),
    GUEST_TO_HOST("GuestToHost"),
    BIDIRECTIONAL("Bidirectional");
    private final String value;
    public String toString() {
        return value;
    }
    ClipboardMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static ClipboardMode fromValue(String v) {
        for (ClipboardMode c: ClipboardMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
