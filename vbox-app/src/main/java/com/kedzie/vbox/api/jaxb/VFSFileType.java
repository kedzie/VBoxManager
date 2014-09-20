

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for VFSFileType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="VFSFileType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Fifo"/>
 *     &lt;enumeration value="DevChar"/>
 *     &lt;enumeration value="Directory"/>
 *     &lt;enumeration value="DevBlock"/>
 *     &lt;enumeration value="File"/>
 *     &lt;enumeration value="SymLink"/>
 *     &lt;enumeration value="Socket"/>
 *     &lt;enumeration value="WhiteOut"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum VFSFileType {

	UNKNOWN("Unknown"),
	FIFO("Fifo"),
	DEV_CHAR("DevChar"),
	DIRECTORY("Directory"),
	DEV_BLOCK("DevBlock"),
	FILE("File"),
	SYM_LINK("SymLink"),
	SOCKET("Socket"),
	WHITE_OUT("WhiteOut");
	private final String value;

	VFSFileType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static VFSFileType fromValue(String v) {
		for (VFSFileType c : VFSFileType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
