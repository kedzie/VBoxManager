
package com.kedzie.vbox.api.jaxb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.KSoapObject;

@KSoapObject("IMediumAttachment")
public class IMediumAttachment implements Parcelable {
	private static final ClassLoader loader = IMediumAttachment.class.getClassLoader();

	public static Parcelable.Creator<IMediumAttachment> CREATOR = new Parcelable.Creator<IMediumAttachment>() {
		@Override
		public IMediumAttachment createFromParcel(Parcel in) {
			IMediumAttachment object = new IMediumAttachment();
			object.setMedium((IMedium) in.readParcelable(loader));
			object.setController(in.readString());
			object.setType((DeviceType) in.readSerializable());
			object.setSlot((Slot)in.readParcelable(loader));
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
		public IMediumAttachment[] newArray(int size) {
			return new IMediumAttachment[size];
		}
	};

	/**
	 * Attachment location and optional name <em>only used for IDE</em>
	 */
	public static class Slot implements Parcelable {

		public static Parcelable.Creator<IMediumAttachment.Slot> CREATOR = new Parcelable.Creator<IMediumAttachment.Slot>() {
			@Override
			public IMediumAttachment.Slot createFromParcel(Parcel in) {
				return new IMediumAttachment.Slot(in.readInt(), in.readInt(), in.readString());
			}

			@Override
			public IMediumAttachment.Slot[] newArray(int size) {
				return new IMediumAttachment.Slot[size];
			}
		};

		public int port;
		public int device;
		public String name;

		public Slot() {}

		public Slot(int port, int device) {
			this(port, device, null);
		}
		
		public Slot(int port, int device, String name) {
			this.port = port;
			this.device = device;
			this.name = name;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(port);
			dest.writeInt(device);
			dest.writeString(name);
		}

		@Override
		public String toString() {
			if (!Utils.isEmpty(name))
				return name;
			return new StringBuffer(port).append(", ").append(device).toString();
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(port, device);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || !(obj instanceof Slot))
				return false;
			Slot that = (Slot) obj;
			return Objects.equal(port, that.port) && Objects.equal(device, that.device);
		}
	}

	private IMedium medium;
	private String controller;
	private DeviceType type;
	private Slot slot = new Slot();
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
		dest.writeParcelable(slot, flags);
		dest.writeBooleanArray(new boolean[] {
			passthrough
		});
		dest.writeBooleanArray(new boolean[] {
			temporaryEject
		});
		dest.writeBooleanArray(new boolean[] {
			isEjected
		});
		dest.writeBooleanArray(new boolean[] {
			nonRotational
		});
		dest.writeBooleanArray(new boolean[] {
			discard
		});
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

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	public Integer getPort() {
		return slot.port;
	}

	public void setPort(Integer port) {
		this.slot.port = port;
	}

	public Integer getDevice() {
		return slot.device;
	}

	public void setDevice(Integer device) {
		this.slot.device = device;
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

	@Override
	public int hashCode() {
		return Objects.hashCode(slot, controller);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof IMediumAttachment))
			return false;
		IMediumAttachment that = (IMediumAttachment) obj;
		return Objects.equal(slot, that.slot) && Objects.equal(controller, that.controller);
	}
}
