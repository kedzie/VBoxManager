

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for GuestSessionWaitResult.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="GuestSessionWaitResult">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Start"/>
 *     &lt;enumeration value="Terminate"/>
 *     &lt;enumeration value="Status"/>
 *     &lt;enumeration value="Error"/>
 *     &lt;enumeration value="Timeout"/>
 *     &lt;enumeration value="WaitFlagNotSupported"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum GuestSessionWaitResult {

	NONE("None"),
	START("Start"),
	TERMINATE("Terminate"),
	STATUS("Status"),
	ERROR("Error"),
	TIMEOUT("Timeout"),
	WAIT_FLAG_NOT_SUPPORTED("WaitFlagNotSupported");
	private final String value;

	GuestSessionWaitResult(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static GuestSessionWaitResult fromValue(String v) {
		for (GuestSessionWaitResult c : GuestSessionWaitResult.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
