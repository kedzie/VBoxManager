

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for HostNetworkInterfaceStatus.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="HostNetworkInterfaceStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Up"/>
 *     &lt;enumeration value="Down"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum HostNetworkInterfaceStatus {

	UNKNOWN("Unknown"),
	UP("Up"),
	DOWN("Down");
	private final String value;

	HostNetworkInterfaceStatus(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static HostNetworkInterfaceStatus fromValue(String v) {
		for (HostNetworkInterfaceStatus c : HostNetworkInterfaceStatus.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
