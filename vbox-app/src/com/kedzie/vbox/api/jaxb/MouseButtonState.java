
package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum MouseButtonState implements Serializable{
    LEFT_BUTTON("LeftButton"),
    RIGHT_BUTTON("RightButton"),
    MIDDLE_BUTTON("MiddleButton"),
    WHEEL_UP("WheelUp"),
    WHEEL_DOWN("WheelDown"),
    X_BUTTON_1("XButton1"),
    X_BUTTON_2("XButton2"),
    MOUSE_STATE_MASK("MouseStateMask");
    private final String value;

    public String toString() {
        return value;
    }

    MouseButtonState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MouseButtonState fromValue(String v) {
        for (MouseButtonState c : MouseButtonState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
