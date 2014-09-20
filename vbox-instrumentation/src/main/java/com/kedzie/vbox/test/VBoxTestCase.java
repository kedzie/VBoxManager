package com.kedzie.vbox.test;

import java.io.IOException;

import junit.framework.TestCase;
import android.util.Log;

import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.IVirtualBoxErrorInfo;
import com.kedzie.vbox.soap.VBoxSvc;

public abstract class VBoxTestCase extends TestCase {
	protected final static int PROGRESS_INTERVAL = 200;
	
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
    
    protected IVirtualBox getVBox() throws Exception {
		return AllTests.getAPI().getVBox();
    }
    
    protected VBoxSvc getAPI() throws Exception {
		return AllTests.getAPI();
    }
    
    /**
	 * Handle VirtualBox API progress functionality
	 * @param p  <code>IProgress</code> of the ongoing task
	 * @return <code>IProgress</code> of the finished task
	 * @throws IOException
	 */
	protected void handleProgress(String TAG, IProgress p)  throws IOException {
		Log.d(TAG, "Handling progress");
		while(!p.getCompleted()) {
			Log.i(TAG, p.getOperation()+"/"+ p.getOperationCount() + " - " + p.getOperationDescription() + "\t" +  p.getOperationPercent() + "\tRemaining: " + p.getTimeRemaining() );
			try { Thread.sleep(PROGRESS_INTERVAL);	} catch (InterruptedException e) {}
		}
		Log.d(TAG, "Operation Completed. result code: " + p.getResultCode());
		if(p.getResultCode()!=0) {
			IVirtualBoxErrorInfo info = p.getErrorInfo();
			Log.e(TAG, "Result Code Error: " + (info!=null ? info.getText() : "No Error Info"));
		}
	}
}
