package com.kedzie.vbox.harness;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.machine.settings.CategoryListFragmentActivity;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * Initializes & launches arbitrary activity from main launcher
 * @author Marek Kedzierski
 */
public class HarnessActivity extends BaseActivity {
	private static final String TAG = HarnessActivity.class.getSimpleName();
	
	private class LogonTask extends ActionBarTask<Server, Void> {

        public LogonTask(VBoxSvc vmgr) {
            super("LogonTask", HarnessActivity.this, vmgr);
        }

        @Override
        protected Void work(Server... server) throws Exception {
            _vboxApi = new VBoxSvc(server[0]);
            _vboxApi.logon();
            _vboxApi.getVBox().getHost().getMemorySize();
            return null;
        }
	}
	
	private class LogoffTask extends ActionBarTask<Void, Void> {
	    
	    public LogoffTask(VBoxSvc vmgr) {
            super("LogoffTask", HarnessActivity.this, vmgr);
        }

	    @Override
        protected Void work(Void... params) throws Exception {
            _vmgr.logoff();
            return null;
        }
	}
	
	private class MachineSettingsTask extends ActionBarTask<Integer, IMachine> {
	    
	    public MachineSettingsTask(String TAG, SherlockFragmentActivity ctx, VBoxSvc vmgr) {
            super(TAG, ctx, vmgr);
        }
	    
	    @Override
        protected IMachine work(Void... params) throws Exception {
            return null;
        }

        @Override
        protected IMachine work(Integer... params) throws Exception {
            return null;
        }

        @Override
        protected void onResult(IMachine result) {
            super.onResult(result);
            
        }
	}

	private VBoxSvc _vboxApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new LogonTask( _vboxApi).execute(new Server(null,m "192.168.1.10", false, 18083, "kedzie", "Mk0204$$"));
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
            case R.id.harness_machineSettings:
                new MachineSettingsTask(_vboxApi).execute(0);
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new LogoffTask( _vboxApi).execute();
    }
}
