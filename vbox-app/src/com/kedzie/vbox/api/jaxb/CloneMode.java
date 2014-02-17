

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for CloneMode.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="CloneMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MachineState"/>
 *     &lt;enumeration value="MachineAndChildStates"/>
 *     &lt;enumeration value="AllStates"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum CloneMode {

	MACHINE_STATE("MachineState"),
	MACHINE_AND_CHILD_STATES("MachineAndChildStates"),
	ALL_STATES("AllStates");
	private final String value;

	CloneMode(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static CloneMode fromValue(String v) {
		for (CloneMode c : CloneMode.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
