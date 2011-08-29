package com.kedzie.vbox.machine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;

public class MachineInfoActivity extends Activity {

	private VBoxSvc _vmgr;
	private IMachine _machine;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_info);
        _vmgr =getIntent().getParcelableExtra("vmgr");
		_machine = _vmgr.getProxy(IMachine.class, getIntent().getStringExtra("machine"));
		((TextView)findViewById(R.id.num_cpus)).setText( _machine.getCPUCount()+"" );
    }
	
}
