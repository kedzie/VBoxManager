package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.MachineState;

public interface IMachine extends IRemoteObject {
	public static enum LaunchMode { headless, gui; }
	
	@KSOAP(cache=true) public String getId();
	@KSOAP(cache=true) public String getName() ;
	@KSOAP(cache=true) public MachineState getState() ;
	public String getDescription();
	@KSOAP(cache=true) public String getOSTypeId();
	public Integer getMemorySize();
	public Integer getMemoryBalloonSize();
	public Integer getVRAMSize();
	
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
