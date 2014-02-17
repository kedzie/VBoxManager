

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ExportOptions.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ExportOptions">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CreateManifest"/>
 *     &lt;enumeration value="ExportDVDImages"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ExportOptions {

	CREATE_MANIFEST("CreateManifest"),
	EXPORT_DVD_IMAGES("ExportDVDImages");
	private final String value;

	ExportOptions(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ExportOptions fromValue(String v) {
		for (ExportOptions c : ExportOptions.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
