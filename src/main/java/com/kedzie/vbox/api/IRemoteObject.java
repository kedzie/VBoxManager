package com.kedzie.vbox.api;

import android.os.Parcelable;

public interface IRemoteObject extends Parcelable {
	public String getIdRef();
	public void setIdRef(String id);
	public void clearCache();
}
