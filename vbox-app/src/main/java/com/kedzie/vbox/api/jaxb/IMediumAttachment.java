

package com.kedzie.vbox.api.jaxb;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.KSoapObject;

import java.util.Objects;

@KSoapObject("IMediumAttachment")
public class IMediumAttachment
    implements Parcelable
{
    private static final ClassLoader loader = IMediumAttachment.class.getClassLoader();

    public static Parcelable.Creator<IMediumAttachment> CREATOR = new Parcelable.Creator<IMediumAttachment>() {
        @Override
        public IMediumAttachment createFromParcel(Parcel in) {
            IMediumAttachment object = new IMediumAttachment();
            object.setMedium((IMedium) in.readParcelable(loader));
            object.setController(in.readString());
            object.setType(DeviceType.fromValue(in.readString()));
            object.setSlot((Slot) in.readParcelable(loader));
            object.setPassthrough(in.readInt() == 1);
            object.setTemporaryEject(in.readInt() == 1);
            object.setIsEjected(in.readInt() == 1);
            object.setNonRotational(in.readInt() == 1);
            object.setDiscard(in.readInt() == 1);
            object.setBandwidthGroup(in.readString());
            object.setHotPluggable(in.readInt() == 1);
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

        public Slot() {
        }

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
            return String.format("%d, %d", port, device);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Slot slot = (Slot) o;
            return port == slot.port &&
                    device == slot.device;
        }

        @Override
        public int hashCode() {
            return Objects.hash(port, device);
        }
    }

    @KSoapObject("slot")
    private Slot slot = new Slot();

    protected IMedium medium;
    protected String controller;
    protected DeviceType type;
    protected boolean passthrough;
    protected boolean temporaryEject;
    protected boolean isEjected;
    protected boolean nonRotational;
    protected boolean discard;
    protected boolean hotPluggable;
    protected String bandwidthGroup;
    protected int port;
    protected int device;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(medium, flags);
        dest.writeString(controller);
        dest.writeString(type.value());
        dest.writeParcelable(slot, flags);
        dest.writeInt(passthrough ? 1 : 0);
        dest.writeInt(temporaryEject ? 1 : 0);
        dest.writeInt(isEjected ? 1 : 0);
        dest.writeInt(nonRotational ? 1 : 0);
        dest.writeInt(discard ? 1 : 0);
        dest.writeString(bandwidthGroup);
        dest.writeInt(hotPluggable ? 1 : 0);
    }


    /**
     * Gets the value of the medium property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public IMedium getMedium() {
        return medium;
    }

    /**
     * Sets the value of the medium property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedium(IMedium value) {
        this.medium = value;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    /**
     * Gets the value of the controller property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getController() {
        return controller;
    }

    /**
     * Sets the value of the controller property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setController(String value) {
        this.controller = value;
    }

    /**
     * Gets the value of the port property.
     * 
     */
    public int getPort() {
        return slot.port;
    }

    /**
     * Sets the value of the port property.
     * 
     */
    public void setPort(int value) {
        slot.port = value;
    }

    /**
     * Gets the value of the device property.
     * 
     */
    public int getDevice() {
        return slot.device;
    }

    /**
     * Sets the value of the device property.
     * 
     */
    public void setDevice(int value) {
        slot.device = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceType }
     *     
     */
    public DeviceType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceType }
     *     
     */
    public void setType(DeviceType value) {
        this.type = value;
    }

    /**
     * Gets the value of the passthrough property.
     * 
     */
    public boolean isPassthrough() {
        return passthrough;
    }

    /**
     * Sets the value of the passthrough property.
     * 
     */
    public void setPassthrough(boolean value) {
        this.passthrough = value;
    }

    /**
     * Gets the value of the temporaryEject property.
     * 
     */
    public boolean isTemporaryEject() {
        return temporaryEject;
    }

    /**
     * Sets the value of the temporaryEject property.
     * 
     */
    public void setTemporaryEject(boolean value) {
        this.temporaryEject = value;
    }

    /**
     * Gets the value of the isEjected property.
     * 
     */
    public boolean isIsEjected() {
        return isEjected;
    }

    /**
     * Sets the value of the isEjected property.
     * 
     */
    public void setIsEjected(boolean value) {
        this.isEjected = value;
    }

    /**
     * Gets the value of the nonRotational property.
     * 
     */
    public boolean isNonRotational() {
        return nonRotational;
    }

    /**
     * Sets the value of the nonRotational property.
     * 
     */
    public void setNonRotational(boolean value) {
        this.nonRotational = value;
    }

    /**
     * Gets the value of the discard property.
     * 
     */
    public boolean isDiscard() {
        return discard;
    }

    /**
     * Sets the value of the discard property.
     * 
     */
    public void setDiscard(boolean value) {
        this.discard = value;
    }

    /**
     * Gets the value of the hotPluggable property.
     * 
     */
    public boolean isHotPluggable() {
        return hotPluggable;
    }

    /**
     * Sets the value of the hotPluggable property.
     * 
     */
    public void setHotPluggable(boolean value) {
        this.hotPluggable = value;
    }

    /**
     * Gets the value of the bandwidthGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBandwidthGroup() {
        return bandwidthGroup;
    }

    /**
     * Sets the value of the bandwidthGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBandwidthGroup(String value) {
        this.bandwidthGroup = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IMediumAttachment that = (IMediumAttachment) o;
        return Objects.equals(slot, that.slot) &&
                Objects.equals(medium, that.medium) &&
                Objects.equals(controller, that.controller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, medium, controller);
    }
}
