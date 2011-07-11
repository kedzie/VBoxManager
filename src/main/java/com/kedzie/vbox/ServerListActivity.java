package com.kedzie.vbox;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;


public class ServerListActivity extends BaseActivity {
	protected static final String TAG = ServerListActivity.class.getSimpleName();
	private static final int REQUEST_CODE_ADD = 9303;
	private static final int REQUEST_CODE_EDIT = 9304;
	
	private ListView listView; 
	private ServerListAdapter listAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);
        listView = (ListView)findViewById(R.id.servers_list);
        registerForContextMenu(listView);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		new LoadServersTask().execute();
	}

	@Override
	protected void onStop() {
		super.onStop();
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
	  Server s = listAdapter.getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.server_list_context_menu_edit:
		Intent intent = new Intent().setClass(this, EditServerActivity.class);
		intent.putExtra( "host", s.getHost() );
		intent.putExtra("port", s.getPort() );
        startActivityForResult(intent, REQUEST_CODE_EDIT);
        return true;
	  case R.id.server_list_context_menu_delete:
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
			return;
		case(REQUEST_CODE_EDIT):
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
			try	{
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Server> result)	{
			listAdapter = new ServerListAdapter(ServerListActivity.this, result);
			listView.setAdapter( listAdapter );
			dismissProgress();
		}
	}
}
