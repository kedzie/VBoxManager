

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for AutostopType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="AutostopType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="SaveState"/>
 *     &lt;enumeration value="PowerOff"/>
 *     &lt;enumeration value="AcpiShutdown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum AutostopType {

	DISABLED("Disabled"),
	SAVE_STATE("SaveState"),
	POWER_OFF("PowerOff"),
	ACPI_SHUTDOWN("AcpiShutdown");
	private final String value;

	AutostopType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static AutostopType fromValue(String v) {
		for (AutostopType c : AutostopType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
