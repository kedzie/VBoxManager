

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for FsObjType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="FsObjType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Undefined"/>
 *     &lt;enumeration value="FIFO"/>
 *     &lt;enumeration value="DevChar"/>
 *     &lt;enumeration value="DevBlock"/>
 *     &lt;enumeration value="Directory"/>
 *     &lt;enumeration value="File"/>
 *     &lt;enumeration value="Symlink"/>
 *     &lt;enumeration value="Socket"/>
 *     &lt;enumeration value="Whiteout"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum FsObjType {

	UNDEFINED("Undefined"),
	FIFO("FIFO"),
	DEV_CHAR("DevChar"),
	DEV_BLOCK("DevBlock"),
	DIRECTORY("Directory"),
	FILE("File"),
	SYMLINK("Symlink"),
	SOCKET("Socket"),
	WHITEOUT("Whiteout");
	private final String value;

	FsObjType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static FsObjType fromValue(String v) {
		for (FsObjType c : FsObjType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
