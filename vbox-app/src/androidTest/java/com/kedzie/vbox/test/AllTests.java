package com.kedzie.vbox.test;
import java.net.InetAddress;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;
import android.util.Log;

import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;

public class AllTests extends TestSuite {
	private static final String TAG = "AllTests";
	private static final int TIMEOUT = 2000;
	public static final Server TEST_SERVER = new Server("", "", false, 18083, "kedzie", "Mk0204$$");
    public static final Server TEST_SERVER_SSL = new Server("", "", true, 18084, "kedzie", "Mk0204$$");
    
    private static final String[] hostnames = { "10.0.2.2", "10.0.0.11", "10.0.0.2" };
    
	private static VBoxSvc vboxApi;

	public static VBoxSvc getAPI() throws Exception {
		if(vboxApi==null) {
			for(String host : hostnames) {
				if( InetAddress.getByName(host).isReachable(TIMEOUT)) {
					Log.i(TAG, "Using HOST: " + host);
					TEST_SERVER.setHost(host);
					TEST_SERVER_SSL.setHost(host);
					break;
				}
			}
			vboxApi = new VBoxSvc(TEST_SERVER);
		}
		return vboxApi;
	}

	public static Test suite() {
		return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
	}
}
