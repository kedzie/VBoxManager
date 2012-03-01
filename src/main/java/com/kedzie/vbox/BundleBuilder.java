package com.kedzie.vbox;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SparseArray;

import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.soap.ParcelableProxy;

/**
 * Builder pattern for {@link Android.os.Bundle}.  
 * Also contains functionality for wrapping VirtualBox remote proxies in Parcels
 */
public class BundleBuilder {
	private Bundle b = new Bundle();
	
	/**
	 * Add a parceled remote proxy to bundle
	 * @param key entry key
	 * @param value remote proxy
	 * @return builder pattern
	 */
	public BundleBuilder putProxy(String key, IManagedObjectRef value) {
		b.putParcelable(key, new ParcelableProxy(value.getInterface(), value));
		return this;
	}
	
	/**
	 * Unparcel a remote proxy from bundle
	 * @param bundle the bundle
	 * @param name name of extra
	 * @param clazz remote proxy interface
	 * @return unbundled remote proxy
	 */
	public static <T> T getProxy(Bundle bundle, String name, Class<T> clazz) {
		ParcelableProxy p = bundle.getParcelable(name);
		return clazz.cast( p.getProxy() );
	}
	
	/**
	 * Add a parceled remote proxy to intent
	 * @param intent the intent
	 * @param name name of extra
	 * @param obj remote proxy
	 */
	public static void addProxy(Intent intent, String name, IManagedObjectRef obj) {
		intent.putExtra(name, new ParcelableProxy(obj.getInterface(), obj));
	}
	
	/**
	 * Unparcel a remote proxy from intent
	 * @param intent the intent
	 * @param name name of extra
	 * @param clazz remote proxy interface
	 * @return the un-parceled remote proxy
	 */
	public static <T> T getProxy(Intent intent, String name, Class<T> clazz) {
		ParcelableProxy p = intent.getParcelableExtra(name);
		return clazz.cast( p.getProxy() );
	}
	
	public BundleBuilder putAll(Bundle map) {
		b.putAll(map);
		return this;
	}
	public BundleBuilder putBoolean(String key, boolean value) {
		b.putBoolean(key, value);
		return this;
	}
	public BundleBuilder putByte(String key, byte value) {
		b.putByte(key, value);
		return this;
	}
	public BundleBuilder putChar(String key, char value) {
		b.putChar(key, value);
		return this;
	}
	public BundleBuilder putShort(String key, short value) {
		b.putShort(key, value);
		return this;
	}
	public BundleBuilder putInt(String key, int value) {
		b.putInt(key, value);
		return this;
	}
	public BundleBuilder putLong(String key, long value) {
		b.putLong(key, value);
		return this;
	}
	public BundleBuilder putFloat(String key, float value) {
		b.putFloat(key, value);
		return this;
	}
	public BundleBuilder putDouble(String key, double value) {
		b.putDouble(key, value);
		return this;
	}
	public BundleBuilder putString(String key, String value) {
		b.putString(key, value);
		return this;
	}
	public BundleBuilder putCharSequence(String key, CharSequence value) {
		b.putCharSequence(key, value);
		return this;
	}
	public BundleBuilder putParcelable(String key, Parcelable value) {
		b.putParcelable(key, value);
		return this;
	}
	public BundleBuilder putParcelableArray(String key, Parcelable[] value) {
		b.putParcelableArray(key, value);
		return this;
	}
	public BundleBuilder putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
		b.putParcelableArrayList(key, value);
		return this;
	}
	public BundleBuilder putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
		b.putSparseParcelableArray(key, value);
		return this;
	}
	public BundleBuilder putIntegerArrayList(String key, ArrayList<Integer> value) {
		b.putIntegerArrayList(key, value);
		return this;
	}
	public BundleBuilder putStringArrayList(String key, ArrayList<String> value) {
		b.putStringArrayList(key, value);
		return this;
	}
	public BundleBuilder putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
		b.putCharSequenceArrayList(key, value);
		return this;
	}
	public BundleBuilder putSerializable(String key, Serializable value) {
		b.putSerializable(key, value);
		return this;
	}
	public BundleBuilder putBooleanArray(String key, boolean[] value) {
		b.putBooleanArray(key, value);
		return this;
	}
	public BundleBuilder putByteArray(String key, byte[] value) {
		b.putByteArray(key, value);
		return this;
	}
	public BundleBuilder putShortArray(String key, short[] value) {
		b.putShortArray(key, value);
		return this;
	}
	public BundleBuilder putCharArray(String key, char[] value) {
		b.putCharArray(key, value);
		return this;
	}
	public BundleBuilder putIntArray(String key, int[] value) {
		b.putIntArray(key, value);
		return this;
	}
	public BundleBuilder putLongArray(String key, long[] value) {
		b.putLongArray(key, value);
		return this;
	}
	public BundleBuilder putFloatArray(String key, float[] value) {
		b.putFloatArray(key, value);
		return this;
	}
	public BundleBuilder putDoubleArray(String key, double[] value) {
		b.putDoubleArray(key, value);
		return this;
	}
	public BundleBuilder putStringArray(String key, String[] value) {
		b.putStringArray(key, value);
		return this;
	}
	public BundleBuilder putCharSequenceArray(String key, CharSequence[] value) {
		b.putCharSequenceArray(key, value);
		return this;
	}
	public BundleBuilder putBundle(String key, Bundle value) {
		b.putBundle(key, value);
		return this;
	}
	public Bundle create() {
		return b;
	}
	public void sendMessage(Handler h, int what) {
		Message msg = h.obtainMessage(what);
		msg.setData(b);
		msg.sendToTarget();
	}
	public void sendMessage(Messenger m, int what) throws RemoteException {
		Message msg = new Message();
		msg.what = what;
		msg.setData(b);
		m.send(msg);
	}
}
