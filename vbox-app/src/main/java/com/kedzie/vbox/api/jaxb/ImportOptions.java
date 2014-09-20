

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ImportOptions.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ImportOptions">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="KeepAllMACs"/>
 *     &lt;enumeration value="KeepNATMACs"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ImportOptions {

	KEEP_ALL_MA_CS("KeepAllMACs"),
	KEEP_NATMA_CS("KeepNATMACs");
	private final String value;

	ImportOptions(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ImportOptions fromValue(String v) {
		for (ImportOptions c : ImportOptions.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
