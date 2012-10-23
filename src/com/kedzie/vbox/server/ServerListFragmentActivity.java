package com.kedzie.vbox.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.server.ServerListFragment.OnSelectServerListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

/**
 * VirtualBox server list
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 * @apiviz.owns com.kedzie.vbox.server.ServerListFragment
 */
public class ServerListFragmentActivity extends BaseActivity implements OnSelectServerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        if(savedInstanceState==null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.add(android.R.id.content, new ServerListFragment(), "server_list");
            tx.commit();
        }
    }

    @Override
    public void onSelectServer(Server server) {
        new LogonTask().execute(server);
    }
    
    /**
     * Log on to VirtualBox webservice
     */
    class LogonTask extends DialogTask<Server, IVirtualBox> {
        public LogonTask() { 
            super( "LogonTask", ServerListFragmentActivity.this, null, "Connecting");
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
            Utils.toastLong(ServerListFragmentActivity.this, "Connected to VirtualBox v." + vbox.getVersion());
            startActivity(new Intent(ServerListFragmentActivity.this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr));
        }
    }
}
