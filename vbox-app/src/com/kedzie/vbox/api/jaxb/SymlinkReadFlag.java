

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for SymlinkReadFlag.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="SymlinkReadFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="NoSymlinks"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum SymlinkReadFlag {

	NONE("None"),
	NO_SYMLINKS("NoSymlinks");
	private final String value;

	SymlinkReadFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static SymlinkReadFlag fromValue(String v) {
		for (SymlinkReadFlag c : SymlinkReadFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
