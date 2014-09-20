

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ProcessWaitForFlag.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ProcessWaitForFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Start"/>
 *     &lt;enumeration value="Terminate"/>
 *     &lt;enumeration value="StdIn"/>
 *     &lt;enumeration value="StdOut"/>
 *     &lt;enumeration value="StdErr"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ProcessWaitForFlag {

	NONE("None"),
	START("Start"),
	TERMINATE("Terminate"),
	STD_IN("StdIn"),
	STD_OUT("StdOut"),
	STD_ERR("StdErr");
	private final String value;

	ProcessWaitForFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessWaitForFlag fromValue(String v) {
		for (ProcessWaitForFlag c : ProcessWaitForFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
