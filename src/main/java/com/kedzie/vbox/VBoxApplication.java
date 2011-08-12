package com.kedzie.vbox;


import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.virtualbox_4_1.MachineState;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SparseArray;
import com.kedzie.vbox.server.PreferencesActivity;

public class VBoxApplication  extends Application {

	public static Map<String,Integer> resources;
	public static Map<MachineState,Integer> states;
	static {
		states = new HashMap<MachineState, Integer>();
		states.put(MachineState.Running, R.drawable.ic_list_start_small);
		states.put(MachineState.Starting, R.drawable.ic_list_start_small);
		states.put(MachineState.Stopping, R.drawable.ic_list_poweroff_small);
		states.put(MachineState.PoweredOff, R.drawable.ic_list_poweroff_small);
		states.put(MachineState.Paused, R.drawable.ic_list_pause_small);
		states.put(MachineState.LiveSnapshotting, R.drawable.ic_list_save_small);
		states.put(MachineState.DeletingSnapshot, R.drawable.ic_list_save_small);
		states.put(MachineState.DeletingSnapshotOnline, R.drawable.ic_list_save_small);
		states.put(MachineState.DeletingSnapshotPaused, R.drawable.ic_list_save_small);
		states.put(MachineState.RestoringSnapshot, R.drawable.ic_list_save_small);
		states.put(MachineState.Saving, R.drawable.ic_list_save_small);
		states.put(MachineState.Saved, R.drawable.ic_list_save_small);
		states.put(MachineState.Restoring, R.drawable.ic_list_start_small);
		states.put(MachineState.Aborted, R.drawable.ic_list_abort_small);
		states.put(MachineState.Stuck, R.drawable.ic_list_stuck_small);
		
		resources = new HashMap<String, Integer>();
		resources.put( "Start", R.drawable.ic_list_start );
		resources.put("Power Off", R.drawable.ic_list_poweroff);
		resources.put("Pause", R.drawable.ic_list_pause);
		resources.put("Resume", R.drawable.ic_list_start);
		resources.put("Reset", R.drawable.ic_list_reset);
		resources.put("Power Button", R.drawable.ic_list_acpi );
		resources.put("Save State", R.drawable.ic_list_save);
		resources.put("Discard State", R.drawable.ic_list_save);
		resources.put("Take Snapshot", R.drawable.ic_list_save);
		resources.put("Restore Snapshot", R.drawable.ic_list_save);
				
		resources.put("os_ubuntu", R.drawable.ic_list_os_ubuntu);
		resources.put("os_ubuntu_64", R.drawable.ic_list_os_ubuntu_64);
		resources.put("os_macos", R.drawable.ic_list_os_macos);
		resources.put("os_macos_64", R.drawable.ic_list_os_macos_64);
		resources.put("os_linux", R.drawable.ic_list_os_linux);
		resources.put("os_linux24_64", R.drawable.ic_list_os_linux24_64);
		resources.put("os_linux24", R.drawable.ic_list_os_linux24);
		resources.put("os_linux26_64", R.drawable.ic_list_os_linux26_64);
		resources.put("os_linux26", R.drawable.ic_list_os_linux26);
		resources.put("os_redhat_64", R.drawable.ic_list_os_redhat_64);
		resources.put("os_redhat", R.drawable.ic_list_os_redhat);
		resources.put("os_debian_64", R.drawable.ic_list_os_debian_64);
		resources.put("os_debian", R.drawable.ic_list_os_debian);
		resources.put("os_windowsxp_64", R.drawable.ic_list_os_winxp_64);
		resources.put("os_windowsxp", R.drawable.ic_list_os_winxp);
		resources.put("os_windows7_64", R.drawable.ic_list_os_win7_64);
		resources.put("os_windows7", R.drawable.ic_list_os_win7);
		resources.put("os_winvista", R.drawable.ic_list_os_winvista);
		resources.put("os_winvista_64", R.drawable.ic_list_os_winvista_64);
		resources.put("os_fedora", R.drawable.ic_list_os_fedora);
		resources.put("os_fedora_64", R.drawable.ic_list_os_fedora_64);
		resources.put("os_opensuse", R.drawable.ic_list_os_opensuse);
		resources.put("os_opensuse_64", R.drawable.ic_list_os_opensuse_64);
		resources.put("os_oracle", R.drawable.ic_list_os_oracle);
		resources.put("os_oracle_64", R.drawable.ic_list_os_oracle_64);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(Class<T> clazz, Annotation []a) {
		for(Annotation at : a) if(at.annotationType().equals(clazz)) return (T)at;
		return null;
	}
	
	public static String[] getActions(MachineState state) {
		if(state.equals(MachineState.Running)) return new String[] { "Pause", "Reset", "Power Off" , "Save State", "Power Button", "Take Snapshot" };
		 else if (state.equals(MachineState.PoweredOff) || state.equals(MachineState.Aborted))	return new String[] { "Start",  "Take Snapshot" };
		else if (state.equals(MachineState.Paused))	return new String[] { "Resume", "Reset", "Power Off" };
		 else if (state.equals(MachineState.Saved))	return new String[] { "Restore State", "Discard State" };
		return new String[] {};
	}
	
	public int getPeriod() {
		return getSharedPreferences(getPackageName(), 0).getInt(PreferencesActivity.PERIOD, 1);
	}
	
	public int getCount() {
		return getSharedPreferences(getPackageName(), 0).getInt(PreferencesActivity.COUNT, 25);
	}

	/**
	 * get Resource based on string
	 * @param name name of resource
	 * @return address of resource
	 */
	public static int get(String name) {
		return resources.containsKey(name) ? resources.get(name) : R.drawable.ic_list_os_linux; 	
	}
	
	/**
	 * Get resource drawable for given <code>MachineState</code>
	 * @param state name of resource
	 * @return address of resource
	 */
	public static int get(MachineState state) {
		return states.containsKey(state)  ? states.get(state) : R.drawable.ic_list_start_small;	
	}
	
	/**
	 * Builder pattern for <code>Android.os.Bundle</code>
	 * @author Marek Kedzierski
	 * @Aug 8, 2011
	 */
	public static class BundleBuilder {
		private Bundle b = new Bundle();
		
		/** @see android.os.Bundle#putAll(android.os.Bundle) */
		public BundleBuilder putAll(Bundle map) {
			b.putAll(map);
			return this;
		}
		/** @see android.os.Bundle#putBoolean(java.lang.String, boolean) */
		public BundleBuilder putBoolean(String key, boolean value) {
			b.putBoolean(key, value);
			return this;
		}
		/** @see android.os.Bundle#putByte(java.lang.String, byte) */
		public BundleBuilder putByte(String key, byte value) {
			b.putByte(key, value);
			return this;
		}
		/** @see android.os.Bundle#putChar(java.lang.String, char) */
		public BundleBuilder putChar(String key, char value) {
			b.putChar(key, value);
			return this;
		}
		/** @see android.os.Bundle#putShort(java.lang.String, short) */
		public BundleBuilder putShort(String key, short value) {
			b.putShort(key, value);
			return this;
		}
		/** @see android.os.Bundle#putInt(java.lang.String, int) */
		public BundleBuilder putInt(String key, int value) {
			b.putInt(key, value);
			return this;
		}
		/** @see android.os.Bundle#putLong(java.lang.String, long) */
		public BundleBuilder putLong(String key, long value) {
			b.putLong(key, value);
			return this;
		}
		/** @see android.os.Bundle#putFloat(java.lang.String, float) */
		public BundleBuilder putFloat(String key, float value) {
			b.putFloat(key, value);
			return this;
		}
		/** @see android.os.Bundle#putDouble(java.lang.String, double) */
		public BundleBuilder putDouble(String key, double value) {
			b.putDouble(key, value);
			return this;
		}
		/** @see android.os.Bundle#putString(java.lang.String, java.lang.String) */
		public BundleBuilder putString(String key, String value) {
			b.putString(key, value);
			return this;
		}
		/** @see android.os.Bundle#putCharSequence(java.lang.String, java.lang.CharSequence) */
		public BundleBuilder putCharSequence(String key, CharSequence value) {
			b.putCharSequence(key, value);
			return this;
		}
		/** @see android.os.Bundle#putParcelable(java.lang.String, android.os.Parcelable) */
		public BundleBuilder putParcelable(String key, Parcelable value) {
			b.putParcelable(key, value);
			return this;
		}
		/** @see android.os.Bundle#putParcelableArray(java.lang.String, android.os.Parcelable[]) */
		public BundleBuilder putParcelableArray(String key, Parcelable[] value) {
			b.putParcelableArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putParcelableArrayList(java.lang.String, java.util.ArrayList) */
		public BundleBuilder putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
			b.putParcelableArrayList(key, value);
			return this;
		}
		/** @see android.os.Bundle#putSparseParcelableArray(java.lang.String, android.util.SparseArray) */
		public BundleBuilder putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
			b.putSparseParcelableArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putIntegerArrayList(java.lang.String, java.util.ArrayList) */
		public BundleBuilder putIntegerArrayList(String key, ArrayList<Integer> value) {
			b.putIntegerArrayList(key, value);
			return this;
		}
		/** @see android.os.Bundle#putStringArrayList(java.lang.String, java.util.ArrayList) */
		public BundleBuilder putStringArrayList(String key, ArrayList<String> value) {
			b.putStringArrayList(key, value);
			return this;
		}
		/** @see android.os.Bundle#putCharSequenceArrayList(java.lang.String, java.util.ArrayList) */
		public BundleBuilder putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
			b.putCharSequenceArrayList(key, value);
			return this;
		}
		/** @see android.os.Bundle#putSerializable(java.lang.String, java.io.Serializable) */
		public BundleBuilder putSerializable(String key, Serializable value) {
			b.putSerializable(key, value);
			return this;
		}
		/** @see android.os.Bundle#putBooleanArray(java.lang.String, boolean[]) */
		public BundleBuilder putBooleanArray(String key, boolean[] value) {
			b.putBooleanArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putByteArray(java.lang.String, byte[]) */
		public BundleBuilder putByteArray(String key, byte[] value) {
			b.putByteArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putShortArray(java.lang.String, short[]) */
		public BundleBuilder putShortArray(String key, short[] value) {
			b.putShortArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putCharArray(java.lang.String, char[]) */
		public BundleBuilder putCharArray(String key, char[] value) {
			b.putCharArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putIntArray(java.lang.String, int[]) */
		public BundleBuilder putIntArray(String key, int[] value) {
			b.putIntArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putLongArray(java.lang.String, long[]) */
		public BundleBuilder putLongArray(String key, long[] value) {
			b.putLongArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putFloatArray(java.lang.String, float[]) */
		public BundleBuilder putFloatArray(String key, float[] value) {
			b.putFloatArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putDoubleArray(java.lang.String, double[]) */
		public BundleBuilder putDoubleArray(String key, double[] value) {
			b.putDoubleArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putStringArray(java.lang.String, java.lang.String[]) */
		public BundleBuilder putStringArray(String key, String[] value) {
			b.putStringArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putCharSequenceArray(java.lang.String, java.lang.CharSequence[]) */
		public BundleBuilder putCharSequenceArray(String key, CharSequence[] value) {
			b.putCharSequenceArray(key, value);
			return this;
		}
		/** @see android.os.Bundle#putBundle(java.lang.String, android.os.Bundle) */
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
}
