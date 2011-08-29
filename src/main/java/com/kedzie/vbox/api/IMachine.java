package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.KSOAPTransport;
import com.kedzie.vbox.api.jaxb.ChipsetType;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.MachineState;

public interface IMachine extends IRemoteObject {
	public static enum LaunchMode { headless, gui; }
	
	public static final Parcelable.Creator<IMachine> CREATOR = new Parcelable.Creator<IMachine>() {
		 public IMachine createFromParcel(Parcel in) { 
			 String id = in.readString();
			 String url = in.readString();
			 @SuppressWarnings("unchecked") Map<String, Object> cache = in.readHashMap(IMachine.class.getClassLoader());
			 KSOAPTransport t = new KSOAPTransport(url);
			 return t.getProxy(IMachine.class, id, cache);
		 }
		 public IMachine[] newArray(int size) {  return new IMachine[size]; }
	 };
	
	@KSOAP(cache=true) public String getId();
	@KSOAP(cache=true) public String getName() ;
	@KSOAP(cache=true) public MachineState getState() ;
	@KSOAP(cache=true) public String getDescription();
	@KSOAP(cache=true) public String getOSTypeId();
	@KSOAP(cache=true) public Integer getMemorySize();
	@KSOAP(cache=true) public Integer getMemoryBalloonSize();
	@KSOAP(cache=true) public Integer getVRAMSize();
	@KSOAP(cache=true) public Integer getCPUCount();
	@KSOAP(cache=true) public Integer getCPUExecutionCap();
	@KSOAP(cache=true) public Integer getMonitorCount();
	@KSOAP(cache=true) public Boolean getAccelerate3dEnabled();
	@KSOAP(cache=true) public Boolean getCurrentStateModified();
	@KSOAP(cache=true) public Boolean getAccelerate2dVideoEnabled();
	@KSOAP(cache=true) public ChipsetType getChipsetType();
	
	public IProgress launchVMProcess(@KSOAP("session")ISession session, @KSOAP("type") LaunchMode type) throws IOException;
	public void lockMachine(@KSOAP("session")ISession s, @KSOAP("lockType")LockType lockType) throws IOException;
	
	@KSOAP(cache=true) public ISnapshot getCurrentSnapshot();
	
	public Map<String, List<String>> querySavedThumbnailSize(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public Map<String, List<String>> readSavedThumbnailPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public Map<String, List<String>> querySavedScreenshotPNGSize(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public Map<String, List<String>> readSavedScreenshotPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public String queryLogFilename(@KSOAP(type="unsignedInt", value="idx") int idx);
	public byte[] readLog(@KSOAP(type="unsignedInt", value="idx") int idx, @KSOAP(type="long", value="offset") long offset, @KSOAP(type="long", value="size") long size);
}
