

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for StorageBus.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="StorageBus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="IDE"/>
 *     &lt;enumeration value="SATA"/>
 *     &lt;enumeration value="SCSI"/>
 *     &lt;enumeration value="Floppy"/>
 *     &lt;enumeration value="SAS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum StorageBus {

	NULL("Null"),
	IDE("IDE"),
	SATA("SATA"),
	SCSI("SCSI"),
	FLOPPY("Floppy"),
	SAS("SAS");
	private final String value;

	StorageBus(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static StorageBus fromValue(String v) {
		for (StorageBus c : StorageBus.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
