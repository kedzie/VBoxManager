package com.kedzie.vbox.api;

import android.os.Parcel;
import android.os.Parcelable;


public interface IRemoteObject {
	public String getId();
	public void setId(String id);
	public void clearCache();
}
