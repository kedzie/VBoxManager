package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

/**
 * Show error messages
 */
public class ErrorSupport implements Parcelable {
	
	public static Parcelable.Creator<ErrorSupport> CREATOR = new Parcelable.Creator<ErrorSupport>() {
		@Override
		public ErrorSupport createFromParcel(Parcel in) {
			return new ErrorSupport(in.readBundle());
		}
		@Override
		public ErrorSupport[] newArray(int size) {
			return new ErrorSupport[size];
		}
	};
	
	private TextView _textView;
	private Bundle _bundle;
	
	public ErrorSupport() {
		_bundle = new Bundle();
	}
	
	private ErrorSupport(Bundle bundle) {
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
        if(_textView!=null)
            _textView.setError(msg==null || msg.isEmpty() ? null : msg);
		showErrors();
	}
	
	public boolean hasErrors() {
		return !_bundle.isEmpty();
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
