

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ProcessInputStatus.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ProcessInputStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Undefined"/>
 *     &lt;enumeration value="Broken"/>
 *     &lt;enumeration value="Available"/>
 *     &lt;enumeration value="Written"/>
 *     &lt;enumeration value="Overflow"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ProcessInputStatus {

	UNDEFINED("Undefined"),
	BROKEN("Broken"),
	AVAILABLE("Available"),
	WRITTEN("Written"),
	OVERFLOW("Overflow");
	private final String value;

	ProcessInputStatus(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessInputStatus fromValue(String v) {
		for (ProcessInputStatus c : ProcessInputStatus.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
