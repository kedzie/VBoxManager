

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for TouchContactState.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="TouchContactState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="InContact"/>
 *     &lt;enumeration value="InRange"/>
 *     &lt;enumeration value="ContactStateMask"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum TouchContactState {

	NONE("None"),
	IN_CONTACT("InContact"),
	IN_RANGE("InRange"),
	CONTACT_STATE_MASK("ContactStateMask");
	private final String value;

	TouchContactState(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static TouchContactState fromValue(String v) {
		for (TouchContactState c : TouchContactState.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
