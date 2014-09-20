

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for AdditionsFacilityStatus.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="AdditionsFacilityStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Inactive"/>
 *     &lt;enumeration value="Paused"/>
 *     &lt;enumeration value="PreInit"/>
 *     &lt;enumeration value="Init"/>
 *     &lt;enumeration value="Active"/>
 *     &lt;enumeration value="Terminating"/>
 *     &lt;enumeration value="Terminated"/>
 *     &lt;enumeration value="Failed"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum AdditionsFacilityStatus {

	INACTIVE("Inactive"),
	PAUSED("Paused"),
	PRE_INIT("PreInit"),
	INIT("Init"),
	ACTIVE("Active"),
	TERMINATING("Terminating"),
	TERMINATED("Terminated"),
	FAILED("Failed"),
	UNKNOWN("Unknown");
	private final String value;

	AdditionsFacilityStatus(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static AdditionsFacilityStatus fromValue(String v) {
		for (AdditionsFacilityStatus c : AdditionsFacilityStatus.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
