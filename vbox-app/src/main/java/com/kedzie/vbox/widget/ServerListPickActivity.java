package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.group.MachineGroupListFragment;
import com.kedzie.vbox.machine.group.TreeNode;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.server.LoginSupport;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.server.ServerListFragment.OnSelectServerListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

/**
 * VirtualBox server list for picking a VM for widget
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 * @apiviz.owns com.kedzie.vbox.server.ServerListFragment
 */
public class ServerListPickActivity extends BaseActivity implements OnSelectServerListener, OnTreeNodeSelectListener {


	/**
	 * Disconnect from VirtualBox webservice
	 */
	private class LogoffTask extends DialogTask<Void, Void> {

		public LogoffTask(VBoxSvc vmgr) { 
			super(ServerListPickActivity.this, vmgr);
		}

		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}

	private static final int REQUEST_CODE_MACHINE_LIST = 0;

	/** Are we in a dual pane (tablet) layout */
	private boolean _dualPane;

	/** ID of AppWidget */
	private int mAppWidgetId;

	/** Currently selected logged on api */
	private VBoxSvc _vmgr;

	private LoginSupport loginSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		loginSupport = new LoginSupport(this, new LoginSupport.LoginCallback() {
			@Override
			public void onLogin(VBoxSvc vmgr) {
				launchMachineList(vmgr);
			}
		});
		getSupportActionBar().setTitle(R.string.widget_server_list);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		setContentView(R.layout.widget_server_list);
		FrameLayout detailsFrame = (FrameLayout)findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	}

	void launchMachineList(VBoxSvc vboxApi) {
		_vmgr=vboxApi;
		if(_dualPane) {
			Utils.setCustomAnimations(getSupportFragmentManager().beginTransaction())
			.replace(R.id.details, Fragment.instantiate(this, MachineGroupListFragment.class.getName(), new BundleBuilder().putParcelable(VBoxSvc.BUNDLE, vboxApi).create()))
			.commit();
		} else {
			Utils.startActivityForResult(this, new Intent(ServerListPickActivity.this, MachineListPickActivity.class)
			.putExtra(VBoxSvc.BUNDLE, (Parcelable)vboxApi)
			.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId), REQUEST_CODE_MACHINE_LIST);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch(requestCode) {
			case REQUEST_CODE_MACHINE_LIST:
				if(resultCode==RESULT_OK) {
					setResult(resultCode, data);
					finish();
				}
				break;
			case LoginSupport.REQUEST_CODE_KEYCHAIN:
				Utils.toastLong(this, "Successfully updated keystore");
				break;
		}
	}

	@Override
	public void onTreeNodeSelect(TreeNode node) {
		if(node instanceof IMachine) {
			Provider.savePrefs(this, _vmgr, (IMachine)node, mAppWidgetId);
			setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
			finish();
		}
	}

	@Override
	public void onSelectServer(final Server server) {
		if(_vmgr!=null) {
			new LogoffTask(_vmgr) {
				protected void onPostExecute(Void result) {
					loginSupport.onSelectServer(server);
				};
			}.execute();
		} else {
			loginSupport.onSelectServer(server);
		}
	}
}
