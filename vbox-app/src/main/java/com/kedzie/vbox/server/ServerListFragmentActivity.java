package com.kedzie.vbox.server;

import android.content.Intent;
import android.os.Bundle;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.server.ServerListFragment.OnSelectServerListener;
import com.kedzie.vbox.soap.VBoxSvc;

import static java.security.AccessController.getContext;

/**
 * VirtualBox server list
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 * @apiviz.owns com.kedzie.vbox.server.ServerListFragment
 */
public class ServerListFragmentActivity extends BaseActivity implements OnSelectServerListener {
    
    private LoginSupport loginSupport;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginSupport = new LoginSupport(this, new LoginSupport.LoginCallback() {
          @Override
          public void onLogin(VBoxSvc vmgr) {
            Intent intent = new Intent(ServerListFragmentActivity.this, MachineListActivity.class);
            BundleBuilder.putVBoxSvc(intent, vmgr);
            Utils.startActivityForResult(ServerListFragmentActivity.this, intent, 0);
          }
        });
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new ServerListFragment(), "server_list").commit();
        }
    }

    @Override
    public void onSelectServer(final Server server) {
      loginSupport.onSelectServer(server);
    }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == 5) { //new server has been selected so connect immediately
      Server server = data.getParcelableExtra("server");
      loginSupport.onSelectServer(server);
    }
  }
}
