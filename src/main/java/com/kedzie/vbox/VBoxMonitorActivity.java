package com.kedzie.vbox;

import org.virtualbox_4_0.IVirtualBox;
import org.virtualbox_4_0.VirtualBoxManager;

import android.app.Activity;
import android.os.Bundle;

public class VBoxMonitorActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
		mgr.connect("http://192.168.1.10:18083", "marek", "Mk0204$$");
		IVirtualBox vbox = mgr.getVBox();
		mgr.disconnect();
    }
    
    
}