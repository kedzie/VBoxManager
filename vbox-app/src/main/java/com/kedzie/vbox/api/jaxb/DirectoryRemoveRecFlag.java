

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for DirectoryRemoveRecFlag.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="DirectoryRemoveRecFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="ContentAndDir"/>
 *     &lt;enumeration value="ContentOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum DirectoryRemoveRecFlag {

	NONE("None"),
	CONTENT_AND_DIR("ContentAndDir"),
	CONTENT_ONLY("ContentOnly");
	private final String value;

	DirectoryRemoveRecFlag(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static DirectoryRemoveRecFlag fromValue(String v) {
		for (DirectoryRemoveRecFlag c : DirectoryRemoveRecFlag.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
