

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for VirtualSystemDescriptionType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="VirtualSystemDescriptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Ignore"/>
 *     &lt;enumeration value="OS"/>
 *     &lt;enumeration value="Name"/>
 *     &lt;enumeration value="Product"/>
 *     &lt;enumeration value="Vendor"/>
 *     &lt;enumeration value="Version"/>
 *     &lt;enumeration value="ProductUrl"/>
 *     &lt;enumeration value="VendorUrl"/>
 *     &lt;enumeration value="Description"/>
 *     &lt;enumeration value="License"/>
 *     &lt;enumeration value="Miscellaneous"/>
 *     &lt;enumeration value="CPU"/>
 *     &lt;enumeration value="Memory"/>
 *     &lt;enumeration value="HardDiskControllerIDE"/>
 *     &lt;enumeration value="HardDiskControllerSATA"/>
 *     &lt;enumeration value="HardDiskControllerSCSI"/>
 *     &lt;enumeration value="HardDiskControllerSAS"/>
 *     &lt;enumeration value="HardDiskImage"/>
 *     &lt;enumeration value="Floppy"/>
 *     &lt;enumeration value="CDROM"/>
 *     &lt;enumeration value="NetworkAdapter"/>
 *     &lt;enumeration value="USBController"/>
 *     &lt;enumeration value="SoundCard"/>
 *     &lt;enumeration value="SettingsFile"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum VirtualSystemDescriptionType {

	IGNORE("Ignore"),
	OS("OS"),
	NAME("Name"),
	PRODUCT("Product"),
	VENDOR("Vendor"),
	VERSION("Version"),
	PRODUCT_URL("ProductUrl"),
	VENDOR_URL("VendorUrl"),
	DESCRIPTION("Description"),
	LICENSE("License"),
	MISCELLANEOUS("Miscellaneous"),
	CPU("CPU"),
	MEMORY("Memory"),
	HARD_DISK_CONTROLLER_IDE("HardDiskControllerIDE"),
	HARD_DISK_CONTROLLER_SATA("HardDiskControllerSATA"),
	HARD_DISK_CONTROLLER_SCSI("HardDiskControllerSCSI"),
	HARD_DISK_CONTROLLER_SAS("HardDiskControllerSAS"),
	HARD_DISK_IMAGE("HardDiskImage"),
	FLOPPY("Floppy"),
	CDROM("CDROM"),
	NETWORK_ADAPTER("NetworkAdapter"),
	USB_CONTROLLER("USBController"),
	SOUND_CARD("SoundCard"),
	SETTINGS_FILE("SettingsFile");
	private final String value;

	VirtualSystemDescriptionType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static VirtualSystemDescriptionType fromValue(String v) {
		for (VirtualSystemDescriptionType c : VirtualSystemDescriptionType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
