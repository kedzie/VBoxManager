package com.kedzie.vbox.harness;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * Initializes & launches arbitrary activity from main launcher
 * @author Marek Kedzierski
 */
public class HarnessActivity extends BaseActivity {
	private static final String TAG = HarnessActivity.class.getSimpleName();

	private VBoxSvc _vboxApi;
	private SliderView _sliderView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.harness);
		_sliderView = (SliderView)findViewById(R.id.slider);
		
		new ActionBarTask<Server, Void>("LogonTask", this, _vboxApi) {
		    @Override
	        protected Void work(Server... server) throws Exception {
	            _vboxApi = new VBoxSvc(server[0]);
	            _vboxApi.logon();
	            _vboxApi.getVBox().getHost().getMemorySize();
	            return null;
	        }
		}.execute(new Server(null, "192.168.1.99", false, 18083, "kedzie", "Mk0204$$" ));
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    getSupportMenuInflater().inflate(R.menu.harness_actions, menu);
	    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.harness_machineList:
                startActivity(new Intent(this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vboxApi));
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new ActionBarTask<Void, Void>("LogoffTask", this, _vboxApi) {
            @Override
            protected Void work(Void... params) throws Exception {
                _vmgr.logoff();
                return null;
            }
        }.execute();
    }
}
