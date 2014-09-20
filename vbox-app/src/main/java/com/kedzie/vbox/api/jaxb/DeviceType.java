

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for DeviceType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="DeviceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="Floppy"/>
 *     &lt;enumeration value="DVD"/>
 *     &lt;enumeration value="HardDisk"/>
 *     &lt;enumeration value="Network"/>
 *     &lt;enumeration value="USB"/>
 *     &lt;enumeration value="SharedFolder"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum DeviceType {

	NULL("Null"),
	FLOPPY("Floppy"),
	DVD("DVD"),
	HARD_DISK("HardDisk"),
	NETWORK("Network"),
	USB("USB"),
	SHARED_FOLDER("SharedFolder");
	private final String value;

	DeviceType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static DeviceType fromValue(String v) {
		for (DeviceType c : DeviceType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
