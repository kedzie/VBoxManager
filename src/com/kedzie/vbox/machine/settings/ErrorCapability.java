package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

/**
 * Show error messages in VM settings
 */
public class ErrorCapability implements Parcelable {
	
	public static Parcelable.Creator<ErrorCapability> CREATOR = new Parcelable.Creator<ErrorCapability>() {
		@Override
		public ErrorCapability createFromParcel(Parcel in) {
			return new ErrorCapability(in.readBundle());
		}
		@Override
		public ErrorCapability[] newArray(int size) {
			return new ErrorCapability[size];
		}
	};
	
	private TextView _textView;
	private Bundle _bundle;
	
	public ErrorCapability() {
		_bundle = new Bundle();
	}
	
	private ErrorCapability(Bundle bundle) {
		_bundle = bundle;
	}
	
	public void showErrors() {
		String msg = "";
		for (String key : _bundle.keySet()) 
			msg+=_bundle.getString(key)+"\n";
		_textView.setText(msg);
	}
	
	public void showError(String field,String msg) {
		if(msg==null || msg.equals(""))
			_bundle.remove(field);
		else
			_bundle.putString(field, msg);
		showErrors();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBundle(_bundle);
	}
	
	public void setTextView(TextView view) {
		_textView = view;
	}
}
