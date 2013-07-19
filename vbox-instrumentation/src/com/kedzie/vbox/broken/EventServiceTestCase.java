package com.kedzie.vbox.broken;

import android.content.Intent;
import android.os.Parcelable;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.kedzie.vbox.event.EventIntentService;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.test.AllTests;

public class EventServiceTestCase extends ServiceTestCase<EventIntentService> {
	
	public EventServiceTestCase() {
		super(EventIntentService.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        AllTests.getAPI().logon();
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	AllTests.getAPI().logoff();
    }
	
	@SmallTest
    public void testStartable() throws Exception {
        startService(new Intent().setClass(getContext(), EventIntentService.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)AllTests.getAPI())); 
    }
}
