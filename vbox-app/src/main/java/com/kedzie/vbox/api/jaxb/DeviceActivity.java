

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for DeviceActivity.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="DeviceActivity">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Idle"/>
 *     &lt;enumeration value="Reading"/>
 *     &lt;enumeration value="Writing"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum DeviceActivity {

	NULL("Null"),
	IDLE("Idle"),
	READING("Reading"),
	WRITING("Writing");
	private final String value;

	DeviceActivity(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static DeviceActivity fromValue(String v) {
		for (DeviceActivity c : DeviceActivity.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
