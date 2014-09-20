

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for USBDeviceState.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="USBDeviceState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NotSupported"/>
 *     &lt;enumeration value="Unavailable"/>
 *     &lt;enumeration value="Busy"/>
 *     &lt;enumeration value="Available"/>
 *     &lt;enumeration value="Held"/>
 *     &lt;enumeration value="Captured"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum USBDeviceState {

	NOT_SUPPORTED("NotSupported"),
	UNAVAILABLE("Unavailable"),
	BUSY("Busy"),
	AVAILABLE("Available"),
	HELD("Held"),
	CAPTURED("Captured");
	private final String value;

	USBDeviceState(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static USBDeviceState fromValue(String v) {
		for (USBDeviceState c : USBDeviceState.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
