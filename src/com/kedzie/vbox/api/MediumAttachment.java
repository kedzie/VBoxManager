package com.kedzie.vbox.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.soap.KSoapObject;

@KSoapObject("IMediumAttachment")
public class MediumAttachment implements Parcelable {
	private static final ClassLoader loader = MediumAttachment.class.getClassLoader();
	
	public static Parcelable.Creator<MediumAttachment> CREATOR = new Parcelable.Creator<MediumAttachment>() {
		@Override
		public MediumAttachment createFromParcel(Parcel in) {
			MediumAttachment object = new MediumAttachment();
			object.setMedium((IMedium)in.readParcelable(loader));
			object.setController(in.readString());
			object.setType((DeviceType)in.readSerializable());
			object.setPort(in.readInt());
			object.setDevice(in.readInt());
			boolean[] tmp = new boolean[1];
			in.readBooleanArray(tmp);
			object.setPassthrough(tmp[0]);
			in.readBooleanArray(tmp);
			object.setTemporaryEject(tmp[0]);
			in.readBooleanArray(tmp);
			object.setIsEjected(tmp[0]);
			in.readBooleanArray(tmp);
			object.setNonRotational(tmp[0]);
			in.readBooleanArray(tmp);
			object.setDiscard(tmp[0]);
			object.setBandwidthGroup(in.readString());
			return object;
		}
		
		@Override
		public MediumAttachment[] newArray(int size) {
			return new MediumAttachment[size];
		}
	};

	private IMedium medium;
	private String controller;
	private DeviceType type;
	private Integer port;
	private Integer device;
	private Boolean passthrough;
	private Boolean temporaryEject;
	private Boolean isEjected;
	private Boolean nonRotational;
	private Boolean discard;
	private String bandwidthGroup;
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(medium, flags);
		dest.writeString(controller);
		dest.writeSerializable(type);
		dest.writeInt(port);
		dest.writeInt(device);
		dest.writeBooleanArray(new boolean[] {passthrough});
		dest.writeBooleanArray(new boolean[] {temporaryEject});
		dest.writeBooleanArray(new boolean[] {isEjected});
		dest.writeBooleanArray(new boolean[] {nonRotational});
		dest.writeBooleanArray(new boolean[] {discard});
		dest.writeString(bandwidthGroup);
	}
	
	public IMedium getMedium() {
		return medium;
	}
	public void setMedium(IMedium medium) {
		this.medium = medium;
	}
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
	public DeviceType getType() {
		return type;
	}
	public void setType(DeviceType type) {
		this.type = type;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getDevice() {
		return device;
	}
	public void setDevice(Integer device) {
		this.device = device;
	}
	public Boolean getPassthrough() {
		return passthrough;
	}
	public void setPassthrough(Boolean passthrough) {
		this.passthrough = passthrough;
	}
	public Boolean getTemporaryEject() {
		return temporaryEject;
	}
	public void setTemporaryEject(Boolean temporaryEject) {
		this.temporaryEject = temporaryEject;
	}
	public Boolean getIsEjected() {
		return isEjected;
	}
	public void setIsEjected(Boolean isEjected) {
		this.isEjected = isEjected;
	}
	public Boolean getNonRotational() {
		return nonRotational;
	}
	public void setNonRotational(Boolean nonRotational) {
		this.nonRotational = nonRotational;
	}
	public Boolean getDiscard() {
		return discard;
	}
	public void setDiscard(Boolean discard) {
		this.discard = discard;
	}
	public String getBandwidthGroup() {
		return bandwidthGroup;
	}
	public void setBandwidthGroup(String bandwidthGroup) {
		this.bandwidthGroup = bandwidthGroup;
	}
}
