package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

import com.kedzie.vbox.soap.KSoapObject;

@KSoapObject("IGuestOSType")
public class IGuestOSType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Guest OS family identifier string. */
	protected String familyId;
	
    /** Human readable description of the guest OS family. */
    protected String familyDescription;
    
    /** Guest OS identifier string. */
    protected String id;
    
    /** Human readable description of the guest OS. */
    protected String description;
    
    /** Returns true if the given OS is 64-bit. */
    protected Boolean is64Bit;
    
    /** Returns true if IO APIC recommended for this OS type. */
    protected Boolean recommendedIOAPIC;
    
    /** Returns true if VT-x or AMD-V recommended for this OS type. */
    protected Boolean recommendedVirtEx;
    
    /** Recommended RAM size in Megabytes. */
    protected Long recommendedRAM;
    
    /**Recommended video RAM size in Megabytes.  */
    protected Long recommendedVRAM;
    
    /** Returns true if 2D video acceleration is recommended for this OS type. */
    protected Boolean recommended2DVideoAcceleration;
    
    /** Returns true if 3D acceleration is recommended for this OS type. */
    protected Boolean recommended3DAcceleration;
    
    /**Recommended hard disk size in bytes.  */
    protected Long recommendedHDD;
    
    /**Returns recommended network adapter for this OS type.  */
    protected NetworkAdapterType adapterType;
    
    /** Returns true if using PAE is recommended for this OS type. */
    protected Boolean recommendedPAE;
    
    /** Recommended storage controller type for DVD/CD drives. */
    protected StorageControllerType recommendedDVDStorageController;
    
    /**Recommended storage bus type for DVD/CD drives.  */
    protected StorageBus recommendedDVDStorageBus;
    
    /** Recommended storage controller type for HD drives. */
    protected StorageControllerType recommendedHDStorageController;
    
    /**Recommended storage bus type for HD drives.  */
    protected StorageBus recommendedHDStorageBus;
    
    /** Recommended firmware type. */
    protected FirmwareType recommendedFirmware;
    
    /** Returns true if using USB Human Interface Devices, such as keyboard and mouse recommended. */
    protected Boolean recommendedUSBHID;
    
    /**Returns true if using HPET is recommended for this OS type.  */
    protected Boolean recommendedHPET;
    
    /** Returns true if using a USB Tablet is recommended. */
    protected Boolean recommendedUSBTablet;
    
    /**Returns true if the RTC of this VM should be set to UTC.  */
    protected Boolean recommendedRTCUseUTC;
    
    /** Recommended chipset type. */
    protected ChipsetType recommendedChipset;
    
    /**Recommended audio type.  */
    protected AudioControllerType recommendedAudioController;
    
    /** Returns true a floppy drive is recommended for this OS type. */
    protected Boolean recommendedFloppy;
    
    /** Returns true a USB controller is recommended for this OS type. */
    protected Boolean recommendedUSB;
    
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
    public Boolean isIs64Bit() {
        return is64Bit;
    }
    /**
     * Sets the value of the is64Bit property.
     * 
     */
    public void setIs64Bit(Boolean value) {
        this.is64Bit = value;
    }
    /**
     * Gets the value of the recommendedIOAPIC property.
     * 
     */
    public Boolean isRecommendedIOAPIC() {
        return recommendedIOAPIC;
    }
    /**
     * Sets the value of the recommendedIOAPIC property.
     * 
     */
    public void setRecommendedIOAPIC(Boolean value) {
        this.recommendedIOAPIC = value;
    }
    /**
     * Gets the value of the recommendedVirtEx property.
     * 
     */
    public Boolean isRecommendedVirtEx() {
        return recommendedVirtEx;
    }
    /**
     * Sets the value of the recommendedVirtEx property.
     * 
     */
    public void setRecommendedVirtEx(Boolean value) {
        this.recommendedVirtEx = value;
    }
    /**
     * Gets the value of the recommendedRAM property.
     * 
     */
    public Long getRecommendedRAM() {
        return recommendedRAM;
    }
    /**
     * Sets the value of the recommendedRAM property.
     * 
     */
    public void setRecommendedRAM(Long value) {
        this.recommendedRAM = value;
    }
    /**
     * Gets the value of the recommendedVRAM property.
     * 
     */
    public Long getRecommendedVRAM() {
        return recommendedVRAM;
    }
    /**
     * Sets the value of the recommendedVRAM property.
     * 
     */
    public void setRecommendedVRAM(Long value) {
        this.recommendedVRAM = value;
    }
    /**
     * Gets the value of the recommended2DVideoAcceleration property.
     * 
     */
    public Boolean isRecommended2DVideoAcceleration() {
        return recommended2DVideoAcceleration;
    }
    /**
     * Sets the value of the recommended2DVideoAcceleration property.
     * 
     */
    public void setRecommended2DVideoAcceleration(Boolean value) {
        this.recommended2DVideoAcceleration = value;
    }
    /**
     * Gets the value of the recommended3DAcceleration property.
     * 
     */
    public Boolean isRecommended3DAcceleration() {
        return recommended3DAcceleration;
    }
    /**
     * Sets the value of the recommended3DAcceleration property.
     * 
     */
    public void setRecommended3DAcceleration(Boolean value) {
        this.recommended3DAcceleration = value;
    }
    /**
     * Gets the value of the recommendedHDD property.
     * 
     */
    public Long getRecommendedHDD() {
        return recommendedHDD;
    }
    /**
     * Sets the value of the recommendedHDD property.
     * 
     */
    public void setRecommendedHDD(Long value) {
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
    public Boolean isRecommendedPAE() {
        return recommendedPAE;
    }
    /**
     * Sets the value of the recommendedPAE property.
     * 
     */
    public void setRecommendedPAE(Boolean value) {
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
    public Boolean isRecommendedUSBHID() {
        return recommendedUSBHID;
    }
    /**
     * Sets the value of the recommendedUSBHID property.
     * 
     */
    public void setRecommendedUSBHID(Boolean value) {
        this.recommendedUSBHID = value;
    }
    /**
     * Gets the value of the recommendedHPET property.
     * 
     */
    public Boolean isRecommendedHPET() {
        return recommendedHPET;
    }
    /**
     * Sets the value of the recommendedHPET property.
     * 
     */
    public void setRecommendedHPET(Boolean value) {
        this.recommendedHPET = value;
    }
    /**
     * Gets the value of the recommendedUSBTablet property.
     * 
     */
    public Boolean isRecommendedUSBTablet() {
        return recommendedUSBTablet;
    }
    /**
     * Sets the value of the recommendedUSBTablet property.
     * 
     */
    public void setRecommendedUSBTablet(Boolean value) {
        this.recommendedUSBTablet = value;
    }
    /**
     * Gets the value of the recommendedRTCUseUTC property.
     * 
     */
    public Boolean isRecommendedRTCUseUTC() {
        return recommendedRTCUseUTC;
    }
    /**
     * Sets the value of the recommendedRTCUseUTC property.
     * 
     */
    public void setRecommendedRTCUseUTC(Boolean value) {
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
    public Boolean isRecommendedFloppy() {
        return recommendedFloppy;
    }
    /**
     * Sets the value of the recommendedFloppy property.
     * 
     */
    public void setRecommendedFloppy(Boolean value) {
        this.recommendedFloppy = value;
    }
    /**
     * Gets the value of the recommendedUSB property.
     * 
     */
    public Boolean isRecommendedUSB() {
        return recommendedUSB;
    }
    /**
     * Sets the value of the recommendedUSB property.
     * 
     */
    public void setRecommendedUSB(Boolean value) {
        this.recommendedUSB = value;
    }
}
