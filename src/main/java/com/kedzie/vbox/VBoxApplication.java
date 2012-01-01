package com.kedzie.vbox;


import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SparseArray;

import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.machine.PreferencesActivity;

public class VBoxApplication  extends Application {
	private Map<String,Integer> resources = new HashMap<String, Integer>();
	private Map<String,Integer> resources_color = new HashMap<String, Integer>();
	
	{
		resources.put(MachineState.RUNNING.name(), R.drawable.ic_list_start);
		resources.put(MachineState.STARTING.name(), R.drawable.ic_list_start);
		resources.put(MachineState.STOPPING.name(), R.drawable.ic_list_acpi);
		resources.put(MachineState.POWERED_OFF.name(), R.drawable.ic_list_acpi);
		resources.put(MachineState.PAUSED.name(), R.drawable.ic_list_pause);
		resources.put(MachineState.LIVE_SNAPSHOTTING.name(), R.drawable.ic_list_snapshot);
		resources.put(MachineState.DELETING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del);
		resources.put(MachineState.DELETING_SNAPSHOT_ONLINE.name(), R.drawable.ic_list_snapshot_del);
		resources.put(MachineState.DELETING_SNAPSHOT_PAUSED.name(), R.drawable.ic_list_snapshot_del);
		resources.put(MachineState.RESTORING_SNAPSHOT.name(), R.drawable.ic_list_snapshot);
		resources.put(MachineState.SAVING.name(), R.drawable.ic_list_save);
		resources.put(MachineState.SAVED.name(), R.drawable.ic_list_save);
		resources.put(MachineState.RESTORING.name(), R.drawable.ic_list_save);
		resources.put(MachineState.ABORTED.name(), R.drawable.ic_list_abort);
		resources.put(MachineState.STUCK.name(), R.drawable.ic_list_stuck);
		resources.put( "Start", R.drawable.ic_list_start );
		resources.put("Power Off", R.drawable.ic_list_poweroff);
		resources.put("Pause", R.drawable.ic_list_pause);
		resources.put("Resume", R.drawable.ic_list_start);
		resources.put("Reset", R.drawable.ic_list_reset);
		resources.put("Power Button", R.drawable.ic_list_acpi );
		resources.put("Save State", R.drawable.ic_list_save);
		resources.put("Discard State", R.drawable.ic_list_save);
		resources.put("Take Snapshot", R.drawable.ic_list_snapshot_add);
		resources.put("Restore Snapshot", R.drawable.ic_list_snapshot);
		resources.put("Delete Snapshot", R.drawable.ic_list_snapshot_del);
		resources_color.put(MachineState.RUNNING.name(), R.drawable.ic_list_start_c);
		resources_color.put(MachineState.STARTING.name(), R.drawable.ic_list_start_c);
		resources_color.put(MachineState.STOPPING.name(), R.drawable.ic_list_acpi_c);
		resources_color.put(MachineState.POWERED_OFF.name(), R.drawable.ic_list_acpi_c);
		resources_color.put(MachineState.PAUSED.name(), R.drawable.ic_list_pause_c);
		resources_color.put(MachineState.LIVE_SNAPSHOTTING.name(), R.drawable.ic_list_snapshot_add_c);
		resources_color.put(MachineState.DELETING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del_c);
		resources_color.put(MachineState.DELETING_SNAPSHOT_ONLINE.name(), R.drawable.ic_list_snapshot_del_c);
		resources_color.put(MachineState.DELETING_SNAPSHOT_PAUSED.name(), R.drawable.ic_list_snapshot_del_c);
		resources_color.put(MachineState.RESTORING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_c);
		resources_color.put(MachineState.SAVING.name(), R.drawable.ic_list_save_c);
		resources_color.put(MachineState.SAVED.name(), R.drawable.ic_list_save_c);
		resources_color.put(MachineState.RESTORING.name(), R.drawable.ic_list_save_c);
		resources_color.put(MachineState.ABORTED.name(), R.drawable.ic_list_abort_c);
		resources_color.put(MachineState.STUCK.name(), R.drawable.ic_list_stuck_c);
		resources_color.put( "Start", R.drawable.ic_list_start_c );
		resources_color.put("Power Off", R.drawable.ic_list_poweroff_c);
		resources_color.put("Pause", R.drawable.ic_list_pause_c);
		resources_color.put("Resume", R.drawable.ic_list_start_c);
		resources_color.put("Reset", R.drawable.ic_list_reset_c);
		resources_color.put("Power Button", R.drawable.ic_list_acpi_c );
		resources_color.put("Save State", R.drawable.ic_list_save_c);
		resources_color.put("Discard State", R.drawable.ic_list_save_c);
		resources_color.put("Take Snapshot", R.drawable.ic_list_snapshot_add_c);
		resources_color.put("Restore Snapshot", R.drawable.ic_list_snapshot_c);
		resources_color.put("Delete Snapshot", R.drawable.ic_list_snapshot_del_c);
	}
	
	public Map<String,Integer> getDrawables() {
		return getSharedPreferences(getPackageName(), 0).getBoolean(PreferencesActivity.ICON_COLORS, PreferencesActivity.ICON_COLORS_DEFAULT) 
				? resources_color :  resources;
	}
	
	/**
	 * get Resource based on string
	 * @param name name of resource
	 * @return address of resource
	 */
	public  int get(String name) {
		if(!getDrawables().containsKey(name)) {
			int id = getResources().getIdentifier(name, "drawable", getPackageName());
			getDrawables().put(name, id!=0 ? id : R.drawable.ic_list_os_other);
		}
		return getDrawables().get(name);
	}
	
	/**
	 * Get resource drawable for given <code>MachineState</code>
	 * @param state name of resource
	 * @return address of resource
	 */
	public int get(MachineState state) {
		return getDrawables().containsKey(state.name())  ? getDrawables().get(state.name()) : R.drawable.ic_list_start;	
	}
	
	public String[] getActions(MachineState state) {
		if(state.equals(MachineState.RUNNING)) return new String[] { "Pause", "Reset", "Power Off" , "Power Button", "Save State", "Take Snapshot" };
		 else if (state.equals(MachineState.POWERED_OFF) || state.equals(MachineState.ABORTED))	return new String[] { "Start",  "Take Snapshot" };
		else if (state.equals(MachineState.PAUSED))	return new String[] { "Resume", "Reset", "Power Off", "Take Snapshot" };
		 else if (state.equals(MachineState.SAVED))	return new String[] { "Restore State", "Discard State" };
		return new String[] {};
	}
	
	public int getPeriod() {
		return getSharedPreferences(getPackageName(), 0).getInt(PreferencesActivity.PERIOD, PreferencesActivity.PERIOD_DEFAULT);
	}
	
	public int getCount() {
		return getSharedPreferences(getPackageName(), 0).getInt(PreferencesActivity.COUNT, PreferencesActivity.COUNT_DEFAULT);
	}
	
	/**
	 * Builder pattern for <code>Android.os.Bundle</code>
	 */
	public static class BundleBuilder {
		private Bundle b = new Bundle();
		
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
		public BundleBuilder putProxy(String key, IManagedObjectRef value) {
			b.putParcelable(key, new ParcelableProxy(value.getInterface(), value));
			return this;
		}
		public static void addProxy(Intent intent, String name, IManagedObjectRef obj) {
			intent.putExtra(name, new ParcelableProxy(obj.getInterface(), obj));
		}
		public static <T> T getProxy(Intent intent, String name, Class<T> clazz) {
			ParcelableProxy p = intent.getParcelableExtra(name);
			return clazz.cast( p.getProxy() );
		}
		public static <T> T getProxy(Bundle bundle, String name, Class<T> clazz) {
			ParcelableProxy p = bundle.getParcelable(name);
			return clazz.cast( p.getProxy() );
		}
	}
}
