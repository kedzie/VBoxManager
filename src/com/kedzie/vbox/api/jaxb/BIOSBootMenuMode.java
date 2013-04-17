package com.kedzie.vbox.api.jaxb;

public enum BIOSBootMenuMode implements java.io.Serializable{
    DISABLED("Disabled"),
    MENU_ONLY("MenuOnly"),
    MESSAGE_AND_MENU("MessageAndMenu");
    private final String value;
    public String toString() {
        return value;
    }
    BIOSBootMenuMode(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static BIOSBootMenuMode fromValue(String v) {
        for (BIOSBootMenuMode c: BIOSBootMenuMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
