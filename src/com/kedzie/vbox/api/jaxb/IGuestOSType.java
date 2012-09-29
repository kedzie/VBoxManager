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
    protected boolean recommended2DVideoAcceleration;
    protected boolean recommended3DAcceleration;
    protected long recommendedHDD;
    protected NetworkAdapterType adapterType;
    protected boolean recommendedPAE;
    protected StorageControllerType recommendedDVDStorageController;
    protected StorageBus recommendedDVDStorageBus;
    protected StorageControllerType recommendedHDStorageController;
    protected StorageBus recommendedHDStorageBus;
    protected FirmwareType recommendedFirmware;
    protected boolean recommendedUSBHID;
    protected boolean recommendedHPET;
    protected boolean recommendedUSBTablet;
    protected boolean recommendedRTCUseUTC;
    protected ChipsetType recommendedChipset;
    protected AudioControllerType recommendedAudioController;
    protected boolean recommendedFloppy;
    protected boolean recommendedUSB;
    /**
     * Gets the value of the familyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyId() {
        return familyId;
    }
    /**
     * Sets the value of the familyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyId(String value) {
        this.familyId = value;
    }
    /**
     * Gets the value of the familyDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyDescription() {
        return familyDescription;
    }
    /**
     * Sets the value of the familyDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyDescription(String value) {
        this.familyDescription = value;
    }
    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }
    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }
    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }
    /**
     * Gets the value of the is64Bit property.
     * 
     */
    public boolean isIs64Bit() {
        return is64Bit;
    }
    /**
     * Sets the value of the is64Bit property.
     * 
     */
    public void setIs64Bit(boolean value) {
        this.is64Bit = value;
    }
    /**
     * Gets the value of the recommendedIOAPIC property.
     * 
     */
    public boolean isRecommendedIOAPIC() {
        return recommendedIOAPIC;
    }
    /**
     * Sets the value of the recommendedIOAPIC property.
     * 
     */
    public void setRecommendedIOAPIC(boolean value) {
        this.recommendedIOAPIC = value;
    }
    /**
     * Gets the value of the recommendedVirtEx property.
     * 
     */
    public boolean isRecommendedVirtEx() {
        return recommendedVirtEx;
    }
    /**
     * Sets the value of the recommendedVirtEx property.
     * 
     */
    public void setRecommendedVirtEx(boolean value) {
        this.recommendedVirtEx = value;
    }
    /**
     * Gets the value of the recommendedRAM property.
     * 
     */
    public long getRecommendedRAM() {
        return recommendedRAM;
    }
    /**
     * Sets the value of the recommendedRAM property.
     * 
     */
    public void setRecommendedRAM(long value) {
        this.recommendedRAM = value;
    }
    /**
     * Gets the value of the recommendedVRAM property.
     * 
     */
    public long getRecommendedVRAM() {
        return recommendedVRAM;
    }
    /**
     * Sets the value of the recommendedVRAM property.
     * 
     */
    public void setRecommendedVRAM(long value) {
        this.recommendedVRAM = value;
    }
    /**
     * Gets the value of the recommended2DVideoAcceleration property.
     * 
     */
    public boolean isRecommended2DVideoAcceleration() {
        return recommended2DVideoAcceleration;
    }
    /**
     * Sets the value of the recommended2DVideoAcceleration property.
     * 
     */
    public void setRecommended2DVideoAcceleration(boolean value) {
        this.recommended2DVideoAcceleration = value;
    }
    /**
     * Gets the value of the recommended3DAcceleration property.
     * 
     */
    public boolean isRecommended3DAcceleration() {
        return recommended3DAcceleration;
    }
    /**
     * Sets the value of the recommended3DAcceleration property.
     * 
     */
    public void setRecommended3DAcceleration(boolean value) {
        this.recommended3DAcceleration = value;
    }
    /**
     * Gets the value of the recommendedHDD property.
     * 
     */
    public long getRecommendedHDD() {
        return recommendedHDD;
    }
    /**
     * Sets the value of the recommendedHDD property.
     * 
     */
    public void setRecommendedHDD(long value) {
        this.recommendedHDD = value;
    }
    /**
     * Gets the value of the adapterType property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkAdapterType }
     *     
     */
    public NetworkAdapterType getAdapterType() {
        return adapterType;
    }
    /**
     * Sets the value of the adapterType property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkAdapterType }
     *     
     */
    public void setAdapterType(NetworkAdapterType value) {
        this.adapterType = value;
    }
    /**
     * Gets the value of the recommendedPAE property.
     * 
     */
    public boolean isRecommendedPAE() {
        return recommendedPAE;
    }
    /**
     * Sets the value of the recommendedPAE property.
     * 
     */
    public void setRecommendedPAE(boolean value) {
        this.recommendedPAE = value;
    }
    /**
     * Gets the value of the recommendedDVDStorageController property.
     * 
     * @return
     *     possible object is
     *     {@link StorageControllerType }
     *     
     */
    public StorageControllerType getRecommendedDVDStorageController() {
        return recommendedDVDStorageController;
    }
    /**
     * Sets the value of the recommendedDVDStorageController property.
     * 
     * @param value
     *     allowed object is
     *     {@link StorageControllerType }
     *     
     */
    public void setRecommendedDVDStorageController(StorageControllerType value) {
        this.recommendedDVDStorageController = value;
    }
    /**
     * Gets the value of the recommendedDVDStorageBus property.
     * 
     * @return
     *     possible object is
     *     {@link StorageBus }
     *     
     */
    public StorageBus getRecommendedDVDStorageBus() {
        return recommendedDVDStorageBus;
    }
    /**
     * Sets the value of the recommendedDVDStorageBus property.
     * 
     * @param value
     *     allowed object is
     *     {@link StorageBus }
     *     
     */
    public void setRecommendedDVDStorageBus(StorageBus value) {
        this.recommendedDVDStorageBus = value;
    }
    /**
     * Gets the value of the recommendedHDStorageController property.
     * 
     * @return
     *     possible object is
     *     {@link StorageControllerType }
     *     
     */
    public StorageControllerType getRecommendedHDStorageController() {
        return recommendedHDStorageController;
    }
    /**
     * Sets the value of the recommendedHDStorageController property.
     * 
     * @param value
     *     allowed object is
     *     {@link StorageControllerType }
     *     
     */
    public void setRecommendedHDStorageController(StorageControllerType value) {
        this.recommendedHDStorageController = value;
    }
    /**
     * Gets the value of the recommendedHDStorageBus property.
     * 
     * @return
     *     possible object is
     *     {@link StorageBus }
     *     
     */
    public StorageBus getRecommendedHDStorageBus() {
        return recommendedHDStorageBus;
    }
    /**
     * Sets the value of the recommendedHDStorageBus property.
     * 
     * @param value
     *     allowed object is
     *     {@link StorageBus }
     *     
     */
    public void setRecommendedHDStorageBus(StorageBus value) {
        this.recommendedHDStorageBus = value;
    }
    /**
     * Gets the value of the recommendedFirmware property.
     * 
     * @return
     *     possible object is
     *     {@link FirmwareType }
     *     
     */
    public FirmwareType getRecommendedFirmware() {
        return recommendedFirmware;
    }
    /**
     * Sets the value of the recommendedFirmware property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirmwareType }
     *     
     */
    public void setRecommendedFirmware(FirmwareType value) {
        this.recommendedFirmware = value;
    }
    /**
     * Gets the value of the recommendedUSBHID property.
     * 
     */
    public boolean isRecommendedUSBHID() {
        return recommendedUSBHID;
    }
    /**
     * Sets the value of the recommendedUSBHID property.
     * 
     */
    public void setRecommendedUSBHID(boolean value) {
        this.recommendedUSBHID = value;
    }
    /**
     * Gets the value of the recommendedHPET property.
     * 
     */
    public boolean isRecommendedHPET() {
        return recommendedHPET;
    }
    /**
     * Sets the value of the recommendedHPET property.
     * 
     */
    public void setRecommendedHPET(boolean value) {
        this.recommendedHPET = value;
    }
    /**
     * Gets the value of the recommendedUSBTablet property.
     * 
     */
    public boolean isRecommendedUSBTablet() {
        return recommendedUSBTablet;
    }
    /**
     * Sets the value of the recommendedUSBTablet property.
     * 
     */
    public void setRecommendedUSBTablet(boolean value) {
        this.recommendedUSBTablet = value;
    }
    /**
     * Gets the value of the recommendedRTCUseUTC property.
     * 
     */
    public boolean isRecommendedRTCUseUTC() {
        return recommendedRTCUseUTC;
    }
    /**
     * Sets the value of the recommendedRTCUseUTC property.
     * 
     */
    public void setRecommendedRTCUseUTC(boolean value) {
        this.recommendedRTCUseUTC = value;
    }
    /**
     * Gets the value of the recommendedChipset property.
     * 
     * @return
     *     possible object is
     *     {@link ChipsetType }
     *     
     */
    public ChipsetType getRecommendedChipset() {
        return recommendedChipset;
    }
    /**
     * Sets the value of the recommendedChipset property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChipsetType }
     *     
     */
    public void setRecommendedChipset(ChipsetType value) {
        this.recommendedChipset = value;
    }
    /**
     * Gets the value of the recommendedAudioController property.
     * 
     * @return
     *     possible object is
     *     {@link AudioControllerType }
     *     
     */
    public AudioControllerType getRecommendedAudioController() {
        return recommendedAudioController;
    }
    /**
     * Sets the value of the recommendedAudioController property.
     * 
     * @param value
     *     allowed object is
     *     {@link AudioControllerType }
     *     
     */
    public void setRecommendedAudioController(AudioControllerType value) {
        this.recommendedAudioController = value;
    }
    /**
     * Gets the value of the recommendedFloppy property.
     * 
     */
    public boolean isRecommendedFloppy() {
        return recommendedFloppy;
    }
    /**
     * Sets the value of the recommendedFloppy property.
     * 
     */
    public void setRecommendedFloppy(boolean value) {
        this.recommendedFloppy = value;
    }
    /**
     * Gets the value of the recommendedUSB property.
     * 
     */
    public boolean isRecommendedUSB() {
        return recommendedUSB;
    }
    /**
     * Sets the value of the recommendedUSB property.
     * 
     */
    public void setRecommendedUSB(boolean value) {
        this.recommendedUSB = value;
    }
}
