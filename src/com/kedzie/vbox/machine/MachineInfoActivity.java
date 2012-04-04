package com.kedzie.vbox.machine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;

public class MachineInfoActivity extends Activity {

	private IMachine _machine;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_info);
        _machine = BundleBuilder.getProxy(getIntent(), EventThread.BUNDLE_MACHINE, IMachine.class);
		((TextView)findViewById(R.id.name)).setText( _machine.getName()+"" );
		((TextView)findViewById(R.id.ostype)).setText( _machine.getOSTypeId()+"" );
		((TextView)findViewById(R.id.baseMemory)).setText( _machine.getMemorySize()+"" );
		((TextView)findViewById(R.id.processors)).setText( _machine.getCPUCount()+"" );
		((TextView)findViewById(R.id.bootOrder)).setText( "" );
		((TextView)findViewById(R.id.acceleration)).setText("" );
		
		((TextView)findViewById(R.id.videoMemory)).setText( _machine.getVRAMSize()+"" );
		((TextView)findViewById(R.id.accelerationVideo)).setText( (_machine.getAccelerate2DVideoEnabled() ? "2D" : "") + " " +  (_machine.getAccelerate3DEnabled() ? "3D" : "") );
		((TextView)findViewById(R.id.rdpPort)).setText( "NaN" );
		
		((TextView)findViewById(R.id.description)).setText( _machine.getDescription()+"" );
    }
	
}
