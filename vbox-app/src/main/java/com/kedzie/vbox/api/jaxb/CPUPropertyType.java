

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for CPUPropertyType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="CPUPropertyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="PAE"/>
 *     &lt;enumeration value="Synthetic"/>
 *     &lt;enumeration value="LongMode"/>
 *     &lt;enumeration value="TripleFaultReset"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum CPUPropertyType {

	NULL("Null"),
	PAE("PAE"),
	SYNTHETIC("Synthetic"),
	LONG_MODE("LongMode"),
	TRIPLE_FAULT_RESET("TripleFaultReset");
	private final String value;

	CPUPropertyType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static CPUPropertyType fromValue(String v) {
		for (CPUPropertyType c : CPUPropertyType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
