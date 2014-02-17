

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ProcessPriority.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ProcessPriority">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Invalid"/>
 *     &lt;enumeration value="Default"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ProcessPriority {

	INVALID("Invalid"),
	DEFAULT("Default");
	private final String value;

	ProcessPriority(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessPriority fromValue(String v) {
		for (ProcessPriority c : ProcessPriority.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
