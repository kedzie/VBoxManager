

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for MouseButtonState.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="MouseButtonState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LeftButton"/>
 *     &lt;enumeration value="RightButton"/>
 *     &lt;enumeration value="MiddleButton"/>
 *     &lt;enumeration value="WheelUp"/>
 *     &lt;enumeration value="WheelDown"/>
 *     &lt;enumeration value="XButton1"/>
 *     &lt;enumeration value="XButton2"/>
 *     &lt;enumeration value="MouseStateMask"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum MouseButtonState {

	LEFT_BUTTON("LeftButton"),
	RIGHT_BUTTON("RightButton"),
	MIDDLE_BUTTON("MiddleButton"),
	WHEEL_UP("WheelUp"),
	WHEEL_DOWN("WheelDown"),
	X_BUTTON_1("XButton1"),
	X_BUTTON_2("XButton2"),
	MOUSE_STATE_MASK("MouseStateMask");
	private final String value;

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
