

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for NetworkAdapterType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="NetworkAdapterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Am79C970A"/>
 *     &lt;enumeration value="Am79C973"/>
 *     &lt;enumeration value="I82540EM"/>
 *     &lt;enumeration value="I82543GC"/>
 *     &lt;enumeration value="I82545EM"/>
 *     &lt;enumeration value="Virtio"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum NetworkAdapterType {

	NULL("Null"),
	AM_79_C_970_A("Am79C970A"),
	AM_79_C_973("Am79C973"),
	I_82540_EM("I82540EM"),
	I_82543_GC("I82543GC"),
	I_82545_EM("I82545EM"),
	VIRTIO("Virtio");
	private final String value;

	NetworkAdapterType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static NetworkAdapterType fromValue(String v) {
		for (NetworkAdapterType c : NetworkAdapterType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
