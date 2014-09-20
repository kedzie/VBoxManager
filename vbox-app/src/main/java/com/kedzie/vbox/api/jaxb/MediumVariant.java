

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for MediumVariant.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="MediumVariant">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Standard"/>
 *     &lt;enumeration value="VmdkSplit2G"/>
 *     &lt;enumeration value="VmdkRawDisk"/>
 *     &lt;enumeration value="VmdkStreamOptimized"/>
 *     &lt;enumeration value="VmdkESX"/>
 *     &lt;enumeration value="Fixed"/>
 *     &lt;enumeration value="Diff"/>
 *     &lt;enumeration value="NoCreateDir"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum MediumVariant {

	STANDARD("Standard"),
	VMDK_SPLIT_2_G("VmdkSplit2G"),
	VMDK_RAW_DISK("VmdkRawDisk"),
	VMDK_STREAM_OPTIMIZED("VmdkStreamOptimized"),
	VMDK_ESX("VmdkESX"),
	FIXED("Fixed"),
	DIFF("Diff"),
	NO_CREATE_DIR("NoCreateDir");
	private final String value;

	MediumVariant(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static MediumVariant fromValue(String v) {
		for (MediumVariant c : MediumVariant.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
