

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for VirtualSystemDescriptionValueType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="VirtualSystemDescriptionValueType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Reference"/>
 *     &lt;enumeration value="Original"/>
 *     &lt;enumeration value="Auto"/>
 *     &lt;enumeration value="ExtraConfig"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum VirtualSystemDescriptionValueType {

	REFERENCE("Reference"),
	ORIGINAL("Original"),
	AUTO("Auto"),
	EXTRA_CONFIG("ExtraConfig");
	private final String value;

	VirtualSystemDescriptionValueType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static VirtualSystemDescriptionValueType fromValue(String v) {
		for (VirtualSystemDescriptionValueType c : VirtualSystemDescriptionValueType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
