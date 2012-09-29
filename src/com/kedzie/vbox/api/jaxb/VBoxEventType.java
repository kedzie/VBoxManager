
package com.kedzie.vbox.api.jaxb;

public enum VBoxEventType {
    INVALID("Invalid"),
    ANY("Any"),
    VETOABLE("Vetoable"),
    MACHINE_EVENT("MachineEvent"),
    SNAPSHOT_EVENT("SnapshotEvent"),
    INPUT_EVENT("InputEvent"),
    LAST_WILDCARD("LastWildcard"),
    ON_MACHINE_STATE_CHANGED("OnMachineStateChanged"),
    ON_MACHINE_DATA_CHANGED("OnMachineDataChanged"),
    ON_EXTRA_DATA_CHANGED("OnExtraDataChanged"),
    ON_EXTRA_DATA_CAN_CHANGE("OnExtraDataCanChange"),
    ON_MEDIUM_REGISTERED("OnMediumRegistered"),
    ON_MACHINE_REGISTERED("OnMachineRegistered"),
    ON_SESSION_STATE_CHANGED("OnSessionStateChanged"),
    ON_SNAPSHOT_TAKEN("OnSnapshotTaken"),
    ON_SNAPSHOT_DELETED("OnSnapshotDeleted"),
    ON_SNAPSHOT_CHANGED("OnSnapshotChanged"),
    ON_GUEST_PROPERTY_CHANGED("OnGuestPropertyChanged"),
    ON_MOUSE_POINTER_SHAPE_CHANGED("OnMousePointerShapeChanged"),
    ON_MOUSE_CAPABILITY_CHANGED("OnMouseCapabilityChanged"),
    ON_KEYBOARD_LEDS_CHANGED("OnKeyboardLedsChanged"),
    ON_STATE_CHANGED("OnStateChanged"),
    ON_ADDITIONS_STATE_CHANGED("OnAdditionsStateChanged"),
    ON_NETWORK_ADAPTER_CHANGED("OnNetworkAdapterChanged"),
    ON_SERIAL_PORT_CHANGED("OnSerialPortChanged"),
    ON_PARALLEL_PORT_CHANGED("OnParallelPortChanged"),
    ON_STORAGE_CONTROLLER_CHANGED("OnStorageControllerChanged"),
    ON_MEDIUM_CHANGED("OnMediumChanged"),
    ON_VRDE_SERVER_CHANGED("OnVRDEServerChanged"),
    ON_USB_CONTROLLER_CHANGED("OnUSBControllerChanged"),
    ON_USB_DEVICE_STATE_CHANGED("OnUSBDeviceStateChanged"),
    ON_SHARED_FOLDER_CHANGED("OnSharedFolderChanged"),
    ON_RUNTIME_ERROR("OnRuntimeError"),
    ON_CAN_SHOW_WINDOW("OnCanShowWindow"),
    ON_SHOW_WINDOW("OnShowWindow"),
    ON_CPU_CHANGED("OnCPUChanged"),
    ON_VRDE_SERVER_INFO_CHANGED("OnVRDEServerInfoChanged"),
    ON_EVENT_SOURCE_CHANGED("OnEventSourceChanged"),
    ON_CPU_EXECUTION_CAP_CHANGED("OnCPUExecutionCapChanged"),
    ON_GUEST_KEYBOARD("OnGuestKeyboard"),
    ON_GUEST_MOUSE("OnGuestMouse"),
    ON_NAT_REDIRECT("OnNATRedirect"),
    ON_HOST_PCI_DEVICE_PLUG("OnHostPCIDevicePlug"),
    ON_V_BOX_SVC_AVAILABILITY_CHANGED("OnVBoxSVCAvailabilityChanged"),
    ON_BANDWIDTH_GROUP_CHANGED("OnBandwidthGroupChanged"),
    ON_GUEST_MONITOR_CHANGED("OnGuestMonitorChanged"),
    ON_STORAGE_DEVICE_CHANGED("OnStorageDeviceChanged"),
    ON_CLIPBOARD_MODE_CHANGED("OnClipboardModeChanged"),
    ON_DRAG_AND_DROP_MODE_CHANGED("OnDragAndDropModeChanged"),
    LAST("Last");
    
    private final String value;

    VBoxEventType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public String toString() {
        return value;
    }

    public static VBoxEventType fromValue(String v) {
        for (VBoxEventType c : VBoxEventType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
