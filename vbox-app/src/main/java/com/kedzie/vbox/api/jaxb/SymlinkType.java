

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for SymlinkType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="SymlinkType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Directory"/>
 *     &lt;enumeration value="File"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum SymlinkType {

	UNKNOWN("Unknown"),
	DIRECTORY("Directory"),
	FILE("File");
	private final String value;

	SymlinkType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static SymlinkType fromValue(String v) {
		for (SymlinkType c : SymlinkType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
