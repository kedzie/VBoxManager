package com.kedzie.vbox.test.soap;

import android.util.Log;
import junit.framework.Assert;
import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.test.AllTests;

public class LogonTest extends TestCase {
    private static final String TAG = "LogonTest";
    private VBoxSvc vboxAPI;
	private final Server SERVER = AllTests.TEST_SERVER;
	private final Server SSL = AllTests.TEST_SERVER_SSL;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if(vboxAPI!=null)
			vboxAPI.logoff();
	}

	@SmallTest
	public void testWrongIP() {
		try {
			vboxAPI = new VBoxSvc(new Server("", "192.168.1.240", false, SERVER.getPort(), SERVER.getUsername(), SERVER.getPassword()));
			vboxAPI.logon();
			Assert.fail();
		} catch(Exception e) {
			Log.e(TAG, "Wrong IP", e);
		}
	}
	
	@SmallTest
	public void testWrongLogon() {
		try {
			vboxAPI = new VBoxSvc(new Server("", SERVER.getHost(), false, SERVER.getPort(), SERVER.getUsername(), "wrong"));
			vboxAPI.logon();
			Assert.fail();
		} catch(Exception e) {
			Log.e(TAG, "Wrong password", e);
		}
	}
	
	@SmallTest
	public void testNoSSL() {
		try {
			vboxAPI = new VBoxSvc(new Server("", SSL.getHost(), false, SSL.getPort(), SSL.getUsername(), SSL.getPassword()));
			vboxAPI.logon();
			Assert.fail();
		} catch(Exception e) {
            Log.e(TAG, "NoSSL", e);
		}
	}
}
