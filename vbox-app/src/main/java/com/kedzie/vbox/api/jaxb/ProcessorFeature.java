

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ProcessorFeature.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ProcessorFeature">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="HWVirtEx"/>
 *     &lt;enumeration value="PAE"/>
 *     &lt;enumeration value="LongMode"/>
 *     &lt;enumeration value="NestedPaging"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ProcessorFeature {

	HW_VIRT_EX("HWVirtEx"),
	PAE("PAE"),
	LONG_MODE("LongMode"),
	NESTED_PAGING("NestedPaging");
	private final String value;

	ProcessorFeature(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessorFeature fromValue(String v) {
		for (ProcessorFeature c : ProcessorFeature.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
