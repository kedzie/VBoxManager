package com.kedzie.vbox.test.soap;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.google.common.base.Objects;
import com.kedzie.vbox.api.IDHCPServer;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.test.AllTests;
import com.kedzie.vbox.test.VBoxTestCase;

public class HostTest extends VBoxTestCase {
	private static final String TAG = "MachineStorageTest";
	
	private IHost host;
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        host = getVBox().getHost();
    }
	
	@SmallTest
	public void testGetInterfacesAndDHCP() throws Exception {
		for(IDHCPServer dhcp : getVBox().getDHCPServers()) {
		Log.i(TAG, Objects.toStringHelper(dhcp)
				.add("Enabled?", dhcp.getEnabled())
				.add("Lower IP", dhcp.getLowerIP())
				.add("Upper IP", dhcp.getUpperIP())
				.add("IP Address", dhcp.getIPAddress())
				.add("Network Name", dhcp.getNetworkName())
				.add("Network Mask", dhcp.getNetworkMask())
				.toString());
		}
		
		for(IHostNetworkInterface net : host.getNetworkInterfaces()) {
			Log.i(TAG, Objects.toStringHelper(net)
					.add("name", net.getName())
					.add("network name", net.getNetworkName())
					.toString());
			IDHCPServer dhcp = AllTests.getAPI().findDHCPServerByNetworkName(net.getNetworkName());
			Log.i(TAG, "DHCP: " + dhcp);
		}
	}
}
