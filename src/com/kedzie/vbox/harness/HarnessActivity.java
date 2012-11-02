package com.kedzie.vbox.harness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.machine.group.TreeNode;
import com.kedzie.vbox.machine.group.VMGroup;
import com.kedzie.vbox.machine.group.VMGroupListView;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.metrics.MetricActivity;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Harness created");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		
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
            case R.id.harness_hostMetrics:
                IHost host = _vboxApi.getVBox().getHost();
                startActivity(new Intent(this, MetricActivity.class)
                    .putExtra(VBoxSvc.BUNDLE, _vboxApi)
                    .putExtra(MetricActivity.INTENT_TITLE, R.string.host_metrics)
                    .putExtra(MetricActivity.INTENT_OBJECT, host.getIdRef() )
                    .putExtra(MetricActivity.INTENT_RAM_AVAILABLE, host.getMemorySize())
                    .putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
                    .putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" }));
                return true;
            case R.id.harness_machineList:
                startActivity(new Intent(this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vboxApi));
                return true;
            case R.id.harness_testsApi:
                new LoadGroupsTask().execute();
                return true;
            case R.id.harness_panelTest:
                startActivity(new Intent(this, PanelActivity.class));
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

    class LoadGroupsTask extends  ActionBarTask<Void, VMGroup> {
        private Map<String, VMGroup> groupCache = new HashMap<String, VMGroup>();

        public LoadGroupsTask() { 
            super(HarnessActivity.TAG, HarnessActivity.this, _vboxApi); 
        }

        private VMGroup get(String name) {
            if(!groupCache.containsKey(name))
                groupCache.put(name, new VMGroup(name));
            return groupCache.get(name);
        }

        @Override
        protected VMGroup work(Void... params) throws Exception {
            List<String> vGroups = _vmgr.getVBox().getMachineGroups();
            for(String tmp : vGroups) {
                if(tmp.equals("/")) continue;
                VMGroup previous = get(tmp);
                int lastIndex=0;
                while((lastIndex=tmp.lastIndexOf('/'))>0) {
                    tmp=tmp.substring(0, lastIndex);
                    VMGroup current = get(tmp);
                    current.addChild(previous);
                    previous=current;
                }
                get("/").addChild(get(tmp));
            }
            for(IMachine machine : _vmgr.getVBox().getMachines()) {
                MachineView.cacheProperties(machine);
                List<String> groups = machine.getGroups();
                if(groups.isEmpty() || groups.get(0).equals("") || groups.get(0).equals("/"))
                    get("/").addChild(machine);
                else
                    get(groups.get(0)).addChild(machine);
            }
            VMGroup root = get("/");
            Log.i(TAG, VMGroup.getTreeString(0, root));
            return root;
        }
        @Override
        protected void onResult(VMGroup root) {
            VMGroupListView listView = new VMGroupListView(HarnessActivity.this);
            listView.setRoot(root);
            listView.setOnTreeNodeSelectListener(new OnTreeNodeSelectListener() {
                @Override
                public void onTreeNodeSelect(TreeNode node) {
                    Utils.toastShort(HarnessActivity.this, "OnClick: %1$s" , node.toString());
                }
            });
            setContentView(listView);
        }
    }
}
