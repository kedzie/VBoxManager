package com.kedzie.vbox.api;

import java.io.IOException;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;

public interface IMachine extends IRemoteObject {

	public String getName() ;
	public MachineState getState() ;
	public String getDescription();
	public String getOSTypeId();
	
	public IProgress launchVMInstance(@KSOAP("session")ISession session, @KSOAP("mode") String mode) throws IOException;
	public void lockMachine(@KSOAP("session")ISession s, @KSOAP("lockType")LockType lockType) throws IOException;
	
	public ISnapshot getCurrentSnapshot();
	
}
