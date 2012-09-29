package com.kedzie.vbox.api.jaxb;

public enum DragAndDropMode {
    DISABLED("Disabled"),
    HOST_TO_GUEST("HostToGuest"),
    GUEST_TO_HOST("GuestToHost"),
    BIDIRECTIONAL("Bidirectional");
    private final String value;
    public String toString() {
        return value;
    }
    DragAndDropMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static DragAndDropMode fromValue(String v) {
        for (DragAndDropMode c: DragAndDropMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
