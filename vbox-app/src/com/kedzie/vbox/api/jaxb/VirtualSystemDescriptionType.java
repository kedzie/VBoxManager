package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum VirtualSystemDescriptionType implements Serializable{
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
    public String toString() {
        return value;
    }
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
