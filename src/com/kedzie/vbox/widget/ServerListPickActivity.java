package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineListBaseFragment;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.machine.MachineListBaseFragment.OnSelectMachineListener;
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
public class ServerListPickActivity extends BaseActivity implements OnSelectServerListener, OnSelectMachineListener {

    private boolean _dualPane;
    private int mAppWidgetId;
    /** Currently selected server */
    private Server _server;
    /** Currently selected logged on api */
    private VBoxSvc _vmgr;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        
        setContentView(R.layout.widget_server_list);
        FrameLayout detailsFrame = (FrameLayout)findViewById(R.id.details);
        _dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    void launchMachineList(VBoxSvc vboxApi) {
        _vmgr=vboxApi;
        if(_dualPane) {
            //replace machine list fragment
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.details, Fragment.instantiate(this, 
                    MachineListBaseFragment.class.getName(), 
                    new BundleBuilder().putParcelable(VBoxSvc.BUNDLE, vboxApi).create()));
            tx.commit();
        } else {
            startActivity(new Intent(ServerListPickActivity.this, MachineListFragmentActivity.class)
                    .putExtra(VBoxSvc.BUNDLE, vboxApi)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
        }
    }
    
    @Override
    public void onMachineSelected(IMachine machine) {
        ConfigureActivity.savePrefs(this, machine, _server, mAppWidgetId);
        setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
        finish();
    }
    
    @Override
    public void onSelectServer(Server server) {
        if(_vmgr!=null)
            new LogoffTask(_vmgr).execute();
        _server=server;
        new LogonTask().execute(server);
    }
    
    /**
     * Log on to VirtualBox webservice, load machine list
     */
    class LogonTask extends DialogTask<Server, IVirtualBox> {
        public LogonTask() { 
            super( "LogonTask", ServerListPickActivity.this, null, "Connecting");
        }

        @Override
        protected IVirtualBox work(Server... params) throws Exception {
            _vmgr = new VBoxSvc(params[0]);
            _vmgr.logon();
            _vmgr.getVBox().getVersion();
            return _vmgr.getVBox();
        }

        @Override 
        protected void onResult(IVirtualBox vbox) {
            Utils.toastShort(ServerListPickActivity.this, "Connected to VirtualBox v." + vbox.getVersion());
            launchMachineList(_vmgr);
        }
    }
    
    private class LogoffTask extends DialogTask<Void, Void> {
        public LogoffTask(VBoxSvc vmgr) { 
            super( "LogoffTask", ServerListPickActivity.this, vmgr, "Logging Off");
        }
        @Override
        protected Void work(Void... params) throws Exception {
            _vmgr.logoff();
            return null;
        }
    }
}
