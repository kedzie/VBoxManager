package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.machine.group.MachineGroupListBaseFragment;
import com.kedzie.vbox.machine.group.TreeNode;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 */
public class MachineListPickActivity extends BaseActivity implements OnTreeNodeSelectListener {
	
	/**
	 * Disconnect from VirtualBox webservice
	 */
	private class LogoffTask extends DialogTask<Void, Void>	{
		
		public LogoffTask(VBoxSvc vmgr) { 
			super(MachineListPickActivity.this, vmgr, R.string.progress_logging_off);
		}
		
		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}
	
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	
	/** ID of AppWidget */
	private int mAppWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		getSupportActionBar().setTitle(R.string.select_virtual_machine_widget_config);
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		 mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		if (savedInstanceState==null) {
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, new MachineGroupListBaseFragment()).commit();
		}
	}

	@Override
	public void onTreeNodeSelect(TreeNode node) {
		if(node instanceof IMachine)
			onMachineSelected((IMachine)node);
	}
	
	public void onMachineSelected(IMachine machine) {
	    Provider.savePrefs(this, _vmgr, machine, mAppWidgetId);
        setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
        finish();
	}
	
	@Override 
	public void onBackPressed() {
		if(_vmgr.getVBox()!=null)  
			new LogoffTask(_vmgr). execute();
		super.onBackPressed();
	}
}
