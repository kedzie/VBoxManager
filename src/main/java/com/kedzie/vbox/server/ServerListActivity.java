package com.kedzie.vbox.server;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.machine.MachineListActivity;


public class ServerListActivity extends BaseListActivity {
	protected static final String TAG = ServerListActivity.class.getName();
	private static final int REQUEST_CODE_ADD = 9303;
	private static final int REQUEST_CODE_EDIT = 9304;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
        getVBoxApplication().getDB().insertOrUpdate(new Server(new Long(-1), "localhost", 18083, "Marek", "Mk0204$$"));
        getVBoxApplication().getDB().insertOrUpdate(new Server(new Long(-1), "192.168.1.10", 18083, "Marek", "Mk0204$$"));
        new LoadServersTask().execute();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.server_list_options_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.server_list_option_menu_add:
	    	Intent intent = new Intent().setClass(this, EditServerActivity.class);
	        startActivityForResult(intent, REQUEST_CODE_ADD);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.server_list_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  Server s = (Server)getListAdapter().getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.server_list_context_menu_edit:
		Intent intent = new Intent().setClass(this, EditServerActivity.class);
		intent.putExtra("id", s.getId());
		intent.putExtra( "host", s.getHost() );
		intent.putExtra("port", s.getPort() );
		intent.putExtra("username", s.getUsername());
		intent.putExtra("password", s.getPassword());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
        return true;
	  case R.id.server_list_context_menu_delete:
		  getVBoxApplication().getDB().delete(s.getId());
		  new LoadServersTask().execute();
	    return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}

 	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
		case(REQUEST_CODE_ADD):
		case(REQUEST_CODE_EDIT):
		default:
			new LoadServersTask().execute();
			return;
        }
    }
	
	private class LoadServersTask extends AsyncTask<Void, Void, List<Server>>	{
		@Override
		protected void onPreExecute()		{ 
			showProgress("Loading Servers");
		}
		@Override
		protected List<Server> doInBackground(Void... params)	{
			return getVBoxApplication().getDB().getServers();
		}
		@Override
		protected void onPostExecute(List<Server> result)	{
			setListAdapter( new ArrayAdapter<Server>(ServerListActivity.this, R.layout.server_list_item, R.id.server_list_item_name, result));
			getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Server s = (Server)getListView().getAdapter().getItem(position);
					Intent intent = new Intent().setClass(ServerListActivity.this, MachineListActivity.class);
					intent.putExtra("url", "http://"+s.getHost()+":"+s.getPort());
					intent.putExtra("username", s.getUsername());
					intent.putExtra("password", s.getPassword());
			        startActivity(intent);
				}
			});
			dismissProgress();
		}
	}
}
