

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for CopyFileFlag.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="CopyFileFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Recursive"/>
 *     &lt;enumeration value="Update"/>
 *     &lt;enumeration value="FollowLinks"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum CopyFileFlag {

	NONE("None"),
	RECURSIVE("Recursive"),
	UPDATE("Update"),
	FOLLOW_LINKS("FollowLinks");
	private final String value;

	CopyFileFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static CopyFileFlag fromValue(String v) {
		for (CopyFileFlag c : CopyFileFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
