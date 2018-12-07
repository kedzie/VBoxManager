package com.kedzie.vbox.test.soap;

import java.io.IOException;
import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.SessionType;
import com.kedzie.vbox.api.jaxb.StorageBus;
import com.kedzie.vbox.test.VBoxTestCase;

public class MachineSettingsTest extends VBoxTestCase {
    private static final String TAG = "MachineSettingsTest";
	
	private IMachine machine;
	private ISession session;
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        machine = getVBox().findMachine("TEST");
        session = getVBox().getSessionObject();
    }
	
	public IMachine lockWrite(IMachine m) throws IOException {
	    m.lockMachine(session, LockType.WRITE);
        assertEquals("Session Type", session.getType(), SessionType.WRITE_LOCK);
        assertEquals("Session State", session.getState(), SessionState.LOCKED);
        return session.getMachine();
	}
	
	public void saveSettings(IMachine m) throws IOException {
	    m.saveSettings();
	    session.unlockMachine();
	}
	
	public void discardSettings(IMachine m) throws IOException {
        m.discardSettings();
        session.unlockMachine();
    }
	
	@SmallTest
	public void testSettings() throws IOException {
		String description = machine.getDescription();
	    String newDescription = description+"-test";
	    
	    IMachine mutable = lockWrite(machine);
		mutable.setDescription(newDescription);
		saveSettings(mutable);
		
		machine.clearCache();
		assertEquals("Changed description", newDescription, machine.getDescription());
		
		mutable = lockWrite(machine);
		mutable.setDescription(description);
		saveSettings(mutable);
		
		machine.clearCache();
		assertEquals("Original description", description, machine.getDescription());
	}
	
	@SmallTest
    public void testStorage() throws Exception {
        List<IMedium> hardDisks = getVBox().getHardDisks();
        IMedium hd1 = hardDisks.get(0);
        Log.i(TAG, "Hard Disk #1: " + hd1.getName() + " - " + hd1.getDescription() + " - " + hd1.getSize());
        
        IMachine mutable = lockWrite(machine);
        
        IStorageController sata = mutable.addStorageController("SATA Controller", StorageBus.SATA);
        mutable.attachDevice(sata.getName(), 0, 0, DeviceType.HARD_DISK, hd1);
        
        mutable.attachDeviceWithoutMedium(sata.getName(), 1, 0, DeviceType.DVD);
        
        assertEquals("# of SATA attachments", 2, mutable.getMediumAttachmentsOfController(sata.getName()).size());
        
        discardSettings(mutable);
    }
}
