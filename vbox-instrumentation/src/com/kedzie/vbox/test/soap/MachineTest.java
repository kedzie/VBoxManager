package com.kedzie.vbox.test.soap;

import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachine.LaunchMode;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.test.VBoxTestCase;

public class MachineTest extends VBoxTestCase {
	private static final String TAG = "MachineTest";
	
	private IMachine machine;
	private ISession session;
	private IConsole console;

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        session = getVBox().getSessionObject();
        machine = getVBox().findMachine("TEST");
    }
	
	@SmallTest
	public void testInitOK() {
		assertNotNull(machine);
		assertNotNull(session);
	}
	
	@SmallTest
	public void testQueryMetrics() throws Exception {
		List<IPerformanceMetric> metrics = getVBox().getPerformanceCollector().getMetrics(new String [] {"*:"}, machine);
		assertNotNull(metrics);
		Log.d(TAG, "Metrics: " + metrics);
	}
	
	@SmallTest
	public void testStart() throws Exception {
		IProgress p = machine.launchVMProcess(session, LaunchMode.headless);
		p.waitForCompletion(10000);
		assertEquals("Machine State", machine.getState(), MachineState.RUNNING);
		session = getVBox().getSessionObject();
		console = session.getConsole();
		p = console.powerDown();
		p.waitForCompletion(10000);
		assertEquals("Machine State", machine.getState(), MachineState.POWERED_OFF);
		session.unlockMachine();
	}
}
