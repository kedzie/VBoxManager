package com.kedzie.vbox.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.kedzie.vbox.app.Utils;

/**
 * Holds a saved virtual machine screenshot
 */
public class Screenshot implements Parcelable {
	private static final String TAG = "Screenshot";
	
	public static Parcelable.Creator<Screenshot> CREATOR = new Parcelable.Creator<Screenshot>() {
		@Override
		public Screenshot createFromParcel(Parcel in) {
			return new Screenshot(in.readInt(), in.readInt(), in.createByteArray());
		}
		
		@Override
		public Screenshot[] newArray(int size) {
			return new Screenshot[size];
		}
	};
	
	public int width;
	public int height;
	public byte[] image;
	private Bitmap bitmap;
	
	public Screenshot(int width, int height, byte[] image) {
		super();
		Log.d(TAG, String.format("Creating screenshot (%1$dx%2$d)", width, height));
		this.width = width;
		this.height = height;
		this.image = image;
	}
	
	public Bitmap getBitmap() {
		if(bitmap==null) 
			bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bitmap;
	}
	
	public void scaleBitmap(int width, int height) {
		bitmap = Utils.scale(getBitmap(), width, height);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeByteArray(image);
	}

}
