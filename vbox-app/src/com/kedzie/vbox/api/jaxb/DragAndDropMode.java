

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for DragAndDropMode.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="DragAndDropMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="HostToGuest"/>
 *     &lt;enumeration value="GuestToHost"/>
 *     &lt;enumeration value="Bidirectional"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum DragAndDropMode {

	DISABLED("Disabled"),
	HOST_TO_GUEST("HostToGuest"),
	GUEST_TO_HOST("GuestToHost"),
	BIDIRECTIONAL("Bidirectional");
	private final String value;

	DragAndDropMode(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static DragAndDropMode fromValue(String v) {
		for (DragAndDropMode c : DragAndDropMode.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
