

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ProcessInputFlag.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ProcessInputFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="EndOfFile"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ProcessInputFlag {

	NONE("None"),
	END_OF_FILE("EndOfFile");
	private final String value;

	ProcessInputFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessInputFlag fromValue(String v) {
		for (ProcessInputFlag c : ProcessInputFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
