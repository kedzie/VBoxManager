package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;
import com.kedzie.vbox.Cacheable;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.KSOAPTransport;
import com.kedzie.vbox.api.jaxb.ChipsetType;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.MachineState;

public interface IMachine extends IRemoteObject {
	public static enum LaunchMode { headless, gui; }
	
	public static final Parcelable.Creator<IMachine> CREATOR = new Parcelable.Creator<IMachine>() {
		@SuppressWarnings("unchecked")  
		public IMachine createFromParcel(Parcel in) { 
			 String id = in.readString(), url = in.readString();
			 Map<String, Object> cache = in.readHashMap(IMachine.class.getClassLoader()); //TODO: Make enumerations Serializable
			 return new KSOAPTransport(url).getProxy(IMachine.class, id, cache);
		 }
		 public IMachine[] newArray(int size) {  
			 return new IMachine[size];
		 }
	 };
	 
	@Cacheable public String getId();
	@Cacheable public String getName() ;
	@Cacheable public MachineState getState() ;
	@Cacheable public String getDescription();
	@Cacheable public String getOSTypeId();
	@Cacheable public Integer getMemorySize();
	@Cacheable public Integer getMemoryBalloonSize();
	@Cacheable public Integer getVRAMSize();
	@Cacheable public Integer getCPUCount();
	@Cacheable public Integer getCPUExecutionCap();
	@Cacheable public Integer getMonitorCount();
	@Cacheable public Boolean getAccelerate3dEnabled();
	@Cacheable public Boolean getCurrentStateModified();
	@Cacheable public Boolean getAccelerate2dVideoEnabled();
	@Cacheable public ChipsetType getChipsetType();
	@Cacheable public ISnapshot getCurrentSnapshot();
	
	public IProgress launchVMProcess(@KSOAP("session")ISession session, @KSOAP("type") LaunchMode type) throws IOException;
	public void lockMachine(@KSOAP("session")ISession s, @KSOAP("lockType")LockType lockType) throws IOException;
	
	public Map<String, List<String>> querySavedThumbnailSize(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public Map<String, List<String>> readSavedThumbnailPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public Map<String, List<String>> querySavedScreenshotPNGSize(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public Map<String, List<String>> readSavedScreenshotPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	public String queryLogFilename(@KSOAP(type="unsignedInt", value="idx") int idx);
	public byte[] readLog(@KSOAP(type="unsignedInt", value="idx") int idx, @KSOAP(type="long", value="offset") long offset, @KSOAP(type="long", value="size") long size);
}
