

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for PortMode.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="PortMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disconnected"/>
 *     &lt;enumeration value="HostPipe"/>
 *     &lt;enumeration value="HostDevice"/>
 *     &lt;enumeration value="RawFile"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum PortMode {

	DISCONNECTED("Disconnected"),
	HOST_PIPE("HostPipe"),
	HOST_DEVICE("HostDevice"),
	RAW_FILE("RawFile");
	private final String value;

	PortMode(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static PortMode fromValue(String v) {
		for (PortMode c : PortMode.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
