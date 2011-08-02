package com.kedzie.vbox.server;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.BaseTask;
import com.kedzie.vbox.R;
import com.kedzie.vbox.machine.MachineListActivity;


public class ServerListActivity extends BaseListActivity<Server> {
	protected static final String TAG = ServerListActivity.class.getName();
	private static final int REQUEST_CODE_ADD = 9303;
	private static final int REQUEST_CODE_EDIT = 9304;
	static final int RESULT_CODE_SAVE = 1;
	static final int RESULT_CODE_DELETE = 2;
	
	private ServerDB _db;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
        TextView emptyView = new TextView(this);
		emptyView.setText("Click 'Menu' to add Server");
		emptyView.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		getListView().setEmptyView(emptyView);
        _db = new ServerDB(this);
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
        _db.insertOrUpdate(new Server(new Long(-1), "192.168.1.10", 18083, "Marek", "Mk0204$$"));
        new LoadServersTask(this).execute();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.server_list_options_menu, menu);
	    return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.server_list_context_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.server_list_option_menu_add:
	    	Intent intent = new Intent().setClass(this, EditServerActivity.class);
	    	intent.putExtra("server", new Server(-1L, "", 18083, "", ""));
	        startActivityForResult(intent, REQUEST_CODE_ADD);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  Server s = (Server)getListAdapter().getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.server_list_context_menu_edit:
		Intent intent = new Intent().setClass(this, EditServerActivity.class);
		intent.putExtra("server", s);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
        return true;
	  case R.id.server_list_context_menu_delete:
		  _db.delete(s.getId());
		  getAdapter().remove(s);
	    return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
 	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 		Server s = data.getParcelableExtra("server");
        switch(resultCode) {
		case(RESULT_CODE_SAVE):
			_db.insertOrUpdate(s);
			if(requestCode==REQUEST_CODE_EDIT) {
				int pos = getAdapter().getPosition(s);
				getAdapter().remove(s);
				getAdapter().insert(s, pos);
			} else if (requestCode == REQUEST_CODE_ADD) {
				getAdapter().add(s);
			}
			break;
		case(RESULT_CODE_DELETE):
			_db.delete(s.getId());
			getAdapter().remove(s);
			break;
        }
    }
	
	class LoadServersTask extends BaseTask<Void, List<Server>>	{
		public LoadServersTask(Context ctx) { super(ctx, null, "Loading Servers", true);	}
		
		@Override
		protected List<Server> work(Void... params) throws Exception {
			return _db.getServers();
		}
		@Override
		protected void onPostExecute(List<Server> result)	{
			super.onPostExecute(result);
			setListAdapter( new ArrayAdapter<Server>(ServerListActivity.this, android.R.layout.simple_list_item_1, result));
		}
	}
}
