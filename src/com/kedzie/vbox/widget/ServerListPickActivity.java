package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.group.MachineGroupListBaseFragment;
import com.kedzie.vbox.machine.group.TreeNode;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
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
     * Log on to VirtualBox webservice, load machine list
     */
    class LogonTask extends DialogTask<Server, IVirtualBox> {
    	
        public LogonTask() {
        	super(ServerListPickActivity.this, null, R.string.progress_connecting);
        }
        
        @Override
        protected IVirtualBox work(Server... params) throws Exception {
            _vmgr = new VBoxSvc(params[0]);
            return _vmgr.logon();
        }
        
        @Override 
        protected void onSuccess(IVirtualBox vbox) {
            launchMachineList(_vmgr);
        }
    }
    
    /**
     * Disconnect from VirtualBox webservice
     */
    private class LogoffTask extends DialogTask<Void, Void> {
    	
        public LogoffTask(VBoxSvc vmgr) { 
        	super(ServerListPickActivity.this, vmgr, R.string.progress_logging_off);
        }
        
        @Override
        protected Void work(Void... params) throws Exception {
            _vmgr.logoff();
            return null;
        }
    }

    /** Are we in a dual pane (tablet) layout */
    private boolean _dualPane;
    
    /** ID of AppWidget */
    private int mAppWidgetId;
    
    /** Currently selected logged on api */
    private VBoxSvc _vmgr;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
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
            	.replace(R.id.details, Fragment.instantiate(this, MachineGroupListBaseFragment.class.getName(), new BundleBuilder().putParcelable(VBoxSvc.BUNDLE, vboxApi).create()))
            	.commit();
        } else {
            startActivityForResult(new Intent(ServerListPickActivity.this, MachineListPickActivity.class)
                    .putExtra(VBoxSvc.BUNDLE, (Parcelable)vboxApi)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId), 0);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
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
    public void onSelectServer(Server server) {
        if(_vmgr!=null)
            new LogoffTask(_vmgr).execute();
        new LogonTask().execute(server);
    }
}
