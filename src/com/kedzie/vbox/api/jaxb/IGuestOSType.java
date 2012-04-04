package com.kedzie.vbox.api.jaxb;

public class IGuestOSType {

	protected String familyId;

	protected String familyDescription;

	protected String id;

	protected String description;
	protected boolean is64Bit;
	protected boolean recommendedIOAPIC;
	protected boolean recommendedVirtEx;
	protected long recommendedRAM;
	protected long recommendedVRAM;
	protected long recommendedHDD;

	protected NetworkAdapterType adapterType;
	protected boolean recommendedPae;

	protected StorageControllerType recommendedDvdStorageController;

	protected StorageBus recommendedDvdStorageBus;

	protected StorageControllerType recommendedHdStorageController;

	protected StorageBus recommendedHdStorageBus;

	protected FirmwareType recommendedFirmware;
	protected boolean recommendedUsbHid;
	protected boolean recommendedHpet;
	protected boolean recommendedUsbTablet;
	protected boolean recommendedRtcUseUtc;

	protected ChipsetType recommendedChipset;

	protected AudioControllerType recommendedAudioController;

	public String getFamilyId() {
		return familyId;
	}

	public void setFamilyId(String value) {
		this.familyId = value;
	}

	public String getFamilyDescription() {
		return familyDescription;
	}

	public void setFamilyDescription(String value) {
		this.familyDescription = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String value) {
		this.id = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public boolean isIs64Bit() {
		return is64Bit;
	}

	public void setIs64Bit(boolean value) {
		this.is64Bit = value;
	}

	public boolean isRecommendedIOAPIC() {
		return recommendedIOAPIC;
	}

	public void setRecommendedIOAPIC(boolean value) {
		this.recommendedIOAPIC = value;
	}

	public boolean isRecommendedVirtEx() {
		return recommendedVirtEx;
	}

	public void setRecommendedVirtEx(boolean value) {
		this.recommendedVirtEx = value;
	}

	public long getRecommendedRAM() {
		return recommendedRAM;
	}

	public void setRecommendedRAM(long value) {
		this.recommendedRAM = value;
	}

	public long getRecommendedVRAM() {
		return recommendedVRAM;
	}

	public void setRecommendedVRAM(long value) {
		this.recommendedVRAM = value;
	}

	public long getRecommendedHDD() {
		return recommendedHDD;
	}

	public void setRecommendedHDD(long value) {
		this.recommendedHDD = value;
	}

	public NetworkAdapterType getAdapterType() {
		return adapterType;
	}

	public void setAdapterType(NetworkAdapterType value) {
		this.adapterType = value;
	}

	public boolean isRecommendedPae() {
		return recommendedPae;
	}

	public void setRecommendedPae(boolean value) {
		this.recommendedPae = value;
	}

	public StorageControllerType getRecommendedDvdStorageController() {
		return recommendedDvdStorageController;
	}

	public void setRecommendedDvdStorageController(StorageControllerType value) {
		this.recommendedDvdStorageController = value;
	}

	public StorageBus getRecommendedDvdStorageBus() {
		return recommendedDvdStorageBus;
	}

	public void setRecommendedDvdStorageBus(StorageBus value) {
		this.recommendedDvdStorageBus = value;
	}

	public StorageControllerType getRecommendedHdStorageController() {
		return recommendedHdStorageController;
	}

	public void setRecommendedHdStorageController(StorageControllerType value) {
		this.recommendedHdStorageController = value;
	}

	public StorageBus getRecommendedHdStorageBus() {
		return recommendedHdStorageBus;
	}

	public void setRecommendedHdStorageBus(StorageBus value) {
		this.recommendedHdStorageBus = value;
	}

	public FirmwareType getRecommendedFirmware() {
		return recommendedFirmware;
	}

	public void setRecommendedFirmware(FirmwareType value) {
		this.recommendedFirmware = value;
	}

	public boolean isRecommendedUsbHid() {
		return recommendedUsbHid;
	}

	public void setRecommendedUsbHid(boolean value) {
		this.recommendedUsbHid = value;
	}

	public boolean isRecommendedHpet() {
		return recommendedHpet;
	}

	public void setRecommendedHpet(boolean value) {
		this.recommendedHpet = value;
	}

	public boolean isRecommendedUsbTablet() {
		return recommendedUsbTablet;
	}

	public void setRecommendedUsbTablet(boolean value) {
		this.recommendedUsbTablet = value;
	}

	public boolean isRecommendedRtcUseUtc() {
		return recommendedRtcUseUtc;
	}

	public void setRecommendedRtcUseUtc(boolean value) {
		this.recommendedRtcUseUtc = value;
	}

	public ChipsetType getRecommendedChipset() {
		return recommendedChipset;
	}

	public void setRecommendedChipset(ChipsetType value) {
		this.recommendedChipset = value;
	}

	public AudioControllerType getRecommendedAudioController() {
		return recommendedAudioController;
	}

	public void setRecommendedAudioController(AudioControllerType value) {
		this.recommendedAudioController = value;
	}

}
