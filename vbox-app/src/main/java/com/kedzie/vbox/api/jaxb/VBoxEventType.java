

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for VBoxEventType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VBoxEventType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Invalid"/>
 *     &lt;enumeration value="Any"/>
 *     &lt;enumeration value="Vetoable"/>
 *     &lt;enumeration value="MachineEvent"/>
 *     &lt;enumeration value="SnapshotEvent"/>
 *     &lt;enumeration value="InputEvent"/>
 *     &lt;enumeration value="LastWildcard"/>
 *     &lt;enumeration value="OnMachineStateChanged"/>
 *     &lt;enumeration value="OnMachineDataChanged"/>
 *     &lt;enumeration value="OnExtraDataChanged"/>
 *     &lt;enumeration value="OnExtraDataCanChange"/>
 *     &lt;enumeration value="OnMediumRegistered"/>
 *     &lt;enumeration value="OnMachineRegistered"/>
 *     &lt;enumeration value="OnSessionStateChanged"/>
 *     &lt;enumeration value="OnSnapshotTaken"/>
 *     &lt;enumeration value="OnSnapshotDeleted"/>
 *     &lt;enumeration value="OnSnapshotChanged"/>
 *     &lt;enumeration value="OnGuestPropertyChanged"/>
 *     &lt;enumeration value="OnMousePointerShapeChanged"/>
 *     &lt;enumeration value="OnMouseCapabilityChanged"/>
 *     &lt;enumeration value="OnKeyboardLedsChanged"/>
 *     &lt;enumeration value="OnStateChanged"/>
 *     &lt;enumeration value="OnAdditionsStateChanged"/>
 *     &lt;enumeration value="OnNetworkAdapterChanged"/>
 *     &lt;enumeration value="OnSerialPortChanged"/>
 *     &lt;enumeration value="OnParallelPortChanged"/>
 *     &lt;enumeration value="OnStorageControllerChanged"/>
 *     &lt;enumeration value="OnMediumChanged"/>
 *     &lt;enumeration value="OnVRDEServerChanged"/>
 *     &lt;enumeration value="OnUSBControllerChanged"/>
 *     &lt;enumeration value="OnUSBDeviceStateChanged"/>
 *     &lt;enumeration value="OnSharedFolderChanged"/>
 *     &lt;enumeration value="OnRuntimeError"/>
 *     &lt;enumeration value="OnCanShowWindow"/>
 *     &lt;enumeration value="OnShowWindow"/>
 *     &lt;enumeration value="OnCPUChanged"/>
 *     &lt;enumeration value="OnVRDEServerInfoChanged"/>
 *     &lt;enumeration value="OnEventSourceChanged"/>
 *     &lt;enumeration value="OnCPUExecutionCapChanged"/>
 *     &lt;enumeration value="OnGuestKeyboard"/>
 *     &lt;enumeration value="OnGuestMouse"/>
 *     &lt;enumeration value="OnNATRedirect"/>
 *     &lt;enumeration value="OnHostPCIDevicePlug"/>
 *     &lt;enumeration value="OnVBoxSVCAvailabilityChanged"/>
 *     &lt;enumeration value="OnBandwidthGroupChanged"/>
 *     &lt;enumeration value="OnGuestMonitorChanged"/>
 *     &lt;enumeration value="OnStorageDeviceChanged"/>
 *     &lt;enumeration value="OnClipboardModeChanged"/>
 *     &lt;enumeration value="OnDnDModeChanged"/>
 *     &lt;enumeration value="OnNATNetworkChanged"/>
 *     &lt;enumeration value="OnNATNetworkStartStop"/>
 *     &lt;enumeration value="OnNATNetworkAlter"/>
 *     &lt;enumeration value="OnNATNetworkCreationDeletion"/>
 *     &lt;enumeration value="OnNATNetworkSetting"/>
 *     &lt;enumeration value="OnNATNetworkPortForward"/>
 *     &lt;enumeration value="OnGuestSessionStateChanged"/>
 *     &lt;enumeration value="OnGuestSessionRegistered"/>
 *     &lt;enumeration value="OnGuestProcessRegistered"/>
 *     &lt;enumeration value="OnGuestProcessStateChanged"/>
 *     &lt;enumeration value="OnGuestProcessInputNotify"/>
 *     &lt;enumeration value="OnGuestProcessOutput"/>
 *     &lt;enumeration value="OnGuestFileRegistered"/>
 *     &lt;enumeration value="OnGuestFileStateChanged"/>
 *     &lt;enumeration value="OnGuestFileOffsetChanged"/>
 *     &lt;enumeration value="OnGuestFileRead"/>
 *     &lt;enumeration value="OnGuestFileWrite"/>
 *     &lt;enumeration value="OnRecordingChanged"/>
 *     &lt;enumeration value="OnGuestUserStateChanged"/>
 *     &lt;enumeration value="OnGuestMultiTouch"/>
 *     &lt;enumeration value="OnHostNameResolutionConfigurationChange"/>
 *     &lt;enumeration value="OnSnapshotRestored"/>
 *     &lt;enumeration value="OnMediumConfigChanged"/>
 *     &lt;enumeration value="OnAudioAdapterChanged"/>
 *     &lt;enumeration value="OnProgressPercentageChanged"/>
 *     &lt;enumeration value="OnProgressTaskCompleted"/>
 *     &lt;enumeration value="OnCursorPositionChanged"/>
 *     &lt;enumeration value="Last"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
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
    ON_DN_D_MODE_CHANGED("OnDnDModeChanged"),
    ON_NAT_NETWORK_CHANGED("OnNATNetworkChanged"),
    ON_NAT_NETWORK_START_STOP("OnNATNetworkStartStop"),
    ON_NAT_NETWORK_ALTER("OnNATNetworkAlter"),
    ON_NAT_NETWORK_CREATION_DELETION("OnNATNetworkCreationDeletion"),
    ON_NAT_NETWORK_SETTING("OnNATNetworkSetting"),
    ON_NAT_NETWORK_PORT_FORWARD("OnNATNetworkPortForward"),
    ON_GUEST_SESSION_STATE_CHANGED("OnGuestSessionStateChanged"),
    ON_GUEST_SESSION_REGISTERED("OnGuestSessionRegistered"),
    ON_GUEST_PROCESS_REGISTERED("OnGuestProcessRegistered"),
    ON_GUEST_PROCESS_STATE_CHANGED("OnGuestProcessStateChanged"),
    ON_GUEST_PROCESS_INPUT_NOTIFY("OnGuestProcessInputNotify"),
    ON_GUEST_PROCESS_OUTPUT("OnGuestProcessOutput"),
    ON_GUEST_FILE_REGISTERED("OnGuestFileRegistered"),
    ON_GUEST_FILE_STATE_CHANGED("OnGuestFileStateChanged"),
    ON_GUEST_FILE_OFFSET_CHANGED("OnGuestFileOffsetChanged"),
    ON_GUEST_FILE_READ("OnGuestFileRead"),
    ON_GUEST_FILE_WRITE("OnGuestFileWrite"),
    ON_RECORDING_CHANGED("OnRecordingChanged"),
    ON_GUEST_USER_STATE_CHANGED("OnGuestUserStateChanged"),
    ON_GUEST_MULTI_TOUCH("OnGuestMultiTouch"),
    ON_HOST_NAME_RESOLUTION_CONFIGURATION_CHANGE("OnHostNameResolutionConfigurationChange"),
    ON_SNAPSHOT_RESTORED("OnSnapshotRestored"),
    ON_MEDIUM_CONFIG_CHANGED("OnMediumConfigChanged"),
    ON_AUDIO_ADAPTER_CHANGED("OnAudioAdapterChanged"),
    ON_PROGRESS_PERCENTAGE_CHANGED("OnProgressPercentageChanged"),
    ON_PROGRESS_TASK_COMPLETED("OnProgressTaskCompleted"),
    ON_CURSOR_POSITION_CHANGED("OnCursorPositionChanged"),
    LAST("Last");
    private final String value;

    VBoxEventType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VBoxEventType fromValue(String v) {
        for (VBoxEventType c: VBoxEventType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
