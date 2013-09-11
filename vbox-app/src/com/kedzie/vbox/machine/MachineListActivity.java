package com.kedzie.vbox.machine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentActivity;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.TabSupport;
import com.kedzie.vbox.app.TabSupportActionBarViewPager;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventIntentService;
import com.kedzie.vbox.host.HostInfoFragment;
import com.kedzie.vbox.machine.group.GroupInfoFragment;
import com.kedzie.vbox.machine.group.TreeNode;
import com.kedzie.vbox.machine.group.VMGroup;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 */
public class MachineListActivity extends BaseActivity implements OnTreeNodeSelectListener {

	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	/** {@link ActionBar} tabs */
	private TabSupport _tabSupport;

	/**
	 * Disconnect from VirtualBox webservice
	 */
	private class LogoffTask extends DialogTask<Void, Void>	{
		
		public LogoffTask(VBoxSvc vmgr) { 
			super(MachineListActivity.this, vmgr, R.string.progress_logging_off);
		}
		
		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _vmgr = BundleBuilder.getVBoxSvc(getIntent());
		setContentView(R.layout.machine_list);

		FrameLayout detailsFrame = (FrameLayout)findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        startService(new Intent(this, EventIntentService.class).putExtras(getIntent()));
	}

	@Override
	protected void onStart() {
		super.onStart();
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onTreeNodeSelect(TreeNode node) {
		if(node instanceof IMachine)
			onMachineSelected((IMachine)node);
		else if (node instanceof VMGroup) 
			onGroupSelected((VMGroup)node);
		else if (node instanceof IHost)
		    onHostSelected((IHost)node);
	}

	private void onMachineSelected(IMachine machine) {
        Bundle b = new BundleBuilder().putVBoxSvc(_vmgr).putProxy(IMachine.BUNDLE, machine).create();
        show(new FragmentElement(machine.getName(), MachineFragment.class,
                new BundleBuilder().putVBoxSvc(_vmgr).putProxy(IMachine.BUNDLE, machine).create()));
//		if (_dualPane)
//            show(new FragmentElement(machine.getName(), MachineFragment.class, b));
//		else
//            Utils.startActivity(this, new Intent(this, MachineActivity.class).putExtras(b));
	}

	private void onGroupSelected(VMGroup group) {
        show(new FragmentElement(group.getName(), GroupInfoFragment.class,
                new BundleBuilder().putVBoxSvc(_vmgr).putParcelable(VMGroup.BUNDLE, group).create()));
	}
	
	private void onHostSelected(IHost host) {
        show(new FragmentElement("Host", HostInfoFragment.class,
                new BundleBuilder().putVBoxSvc(_vmgr).putParcelable(IHost.BUNDLE, host).create()));
    }

    private void show(FragmentElement details) {
        if(_dualPane) {
            Utils.setCustomAnimations(getSupportFragmentManager().beginTransaction()).replace(R.id.details, details.instantiate(this)).commit();
        } else {
            Utils.startActivity(this, new Intent(this, FragmentActivity.class).putExtra(FragmentElement.BUNDLE, details));
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				logoff();
				return true;
		}
		return false;
	}

	@Override 
	public void onBackPressed() {
		logoff();
	}
	
	@Override
    public void finish() {
        super.finish();
        Utils.overrideBackTransition(this);
    }

	public void logoff() {
		stopService(new Intent(this, EventIntentService.class));
		if(_vmgr.getVBox()!=null)  
			new LogoffTask(_vmgr). execute();
	}
}
