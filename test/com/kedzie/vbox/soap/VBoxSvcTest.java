package com.kedzie.vbox.soap;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.IVirtualBox;

public class VBoxSvcTest {

//	VBoxSvc vmgr = new VBoxSvc("http://99.38.98.125:18083");
//	VBoxSvc vmgr = new VBoxSvc("http://192.168.1.99:18083");
	VBoxSvc vmgr = new VBoxSvc("http://192.168.2.2:18083");
	
	@Test public void wrongIPTest() {
		try {
			new VBoxSvc("http://192.168.4.4:180").logon("bla", "bla");
			Assert.fail();
		} catch(Exception e) {
			System.out.println("Wrong IP"+e);
			e.printStackTrace();
		}
	}
	
	@Test public void wrongLogonTest() {
		try {
			new VBoxSvc("http://192.168.2.2:18083").logon("kedzie", "wrong");
			Assert.fail();
		} catch(Exception e) {
			System.out.println("Wrong password"+ e);
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void queryMetricsTest() throws Exception {
		IVirtualBox vbox = vmgr.logon("kedzie", "Mk0204$$");
		try {
			List<IMachine> machines = vbox.getMachines();
			List<IPerformanceMetric> metrics = vbox.getPerformanceCollector().getMetrics(new String [] {"*:"}, machines.get(0));
			System.out.println("Metrics: " + metrics);
		} finally {
			vbox.logoff();
		}
	}
	
}
