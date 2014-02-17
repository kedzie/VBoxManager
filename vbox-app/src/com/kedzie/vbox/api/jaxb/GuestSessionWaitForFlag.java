

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for GuestSessionWaitForFlag.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="GuestSessionWaitForFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Start"/>
 *     &lt;enumeration value="Terminate"/>
 *     &lt;enumeration value="Status"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum GuestSessionWaitForFlag {

	NONE("None"),
	START("Start"),
	TERMINATE("Terminate"),
	STATUS("Status");
	private final String value;

	GuestSessionWaitForFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static GuestSessionWaitForFlag fromValue(String v) {
		for (GuestSessionWaitForFlag c : GuestSessionWaitForFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
