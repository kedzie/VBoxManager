package com.kedzie.vbox.api.jaxb;

public enum FramebufferPixelFormat {
    OPAQUE("Opaque"),
    FOURCC_RGB("FOURCC_RGB");
    private final String value;
    public String toString() {
        return value;
    }
    FramebufferPixelFormat(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static FramebufferPixelFormat fromValue(String v) {
        for (FramebufferPixelFormat c: FramebufferPixelFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
