package com.kedzie.vbox.api;

import java.io.IOException;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;

import com.kedzie.vbox.KSOAP;

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
	
	public IProgress launchVMProcess(@KSOAP("session")ISession session, @KSOAP("type") String type) throws IOException;
	public void lockMachine(@KSOAP("session")ISession s, @KSOAP("lockType")LockType lockType) throws IOException;
	
	@KSOAP(cache=true) public ISnapshot getCurrentSnapshot();
	
}
