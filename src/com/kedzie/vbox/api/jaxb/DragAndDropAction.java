package com.kedzie.vbox.api.jaxb;

public enum DragAndDropAction {
    IGNORE("Ignore"),
    COPY("Copy"),
    MOVE("Move"),
    LINK("Link");
    private final String value;
    public String toString() {
        return value;
    }
    DragAndDropAction(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static DragAndDropAction fromValue(String v) {
        for (DragAndDropAction c: DragAndDropAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
