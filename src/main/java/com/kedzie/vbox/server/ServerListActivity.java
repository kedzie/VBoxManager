package com.kedzie.vbox.server;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.task.BaseTask;


public class ServerListActivity extends BaseListActivity<Server> {
	protected static final String TAG = ServerListActivity.class.getName();
	static final int REQUEST_CODE_ADD = 9303,REQUEST_CODE_EDIT = 9304, RESULT_CODE_SAVE = 1,RESULT_CODE_DELETE = 2;
	
	private ServerDB _db;
	private VBoxSvc vmgr;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
        
        getListView().setDividerHeight(2);
        
        TextView emptyView = new TextView(this);
		emptyView.setText("Click 'Menu' to add Server");
		getListView().setEmptyView(emptyView);
		
        _db = new ServerDB(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		       	new BaseTask<Server, String>(ServerListActivity.this, vmgr, "Connecting", true) {
					@Override
					protected String work(Server... params) throws Exception {
						_vmgr = new VBoxSvc("http://"+params[0].getHost()+":"+params[0].getPort());
						_vmgr.logon(params[0].getUsername(), params[0].getPassword());
						_vmgr.setupMetrics(ServerListActivity.this, _vmgr.getVBox().getHost().getIdRef(), "*:");
						return _vmgr.getVBox().getVersion();
					}
					@Override
					protected void onPostExecute(String version) {
						super.onPostExecute(version);
						if(version!=null) {
							Toast.makeText(ServerListActivity.this, "Connected to VirtualBox v." + version, Toast.LENGTH_LONG).show();
							startActivity(new Intent().setClass(ServerListActivity.this, MachineListActivity.class).putExtra("vmgr", _vmgr));
						}
					}
		       	}.execute(getAdapter().getItem(position));
			}
		});
        _db.insertOrUpdate(new Server(new Long(-1), "192.168.1.10", 18083, "Marek", "Mk0204$$"));
        new BaseTask<Void, List<Server>>(this,  null, "Loading Servers", true)	{
    		@Override protected List<Server> work(Void... params) throws Exception { return _db.getServers(); }
    		@Override protected void onPostExecute(List<Server> result)	{
    			super.onPostExecute(result);
    			setListAdapter( new ArrayAdapter<Server>(ServerListActivity.this, android.R.layout.simple_list_item_1, result));
    		}
    	}.execute();
    }
	
	
	@Override protected void onDestroy() {
		super.onDestroy();
		_db.close();
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
	        startActivityForResult(new Intent().setClass(this, EditServerActivity.class).putExtra("server", new Server(-1L, "", 18083, "", "")), REQUEST_CODE_ADD);
	        return true;
	    default:
	        return true;
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  Server s = (Server)getListAdapter().getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.server_list_context_menu_edit:
        startActivityForResult(new Intent().setClass(this, EditServerActivity.class).putExtra("server", s), REQUEST_CODE_EDIT);
        return true;
	  case R.id.server_list_context_menu_delete:
		  _db.delete(s.getId());
		  getAdapter().remove(s);
	    return true;
	  default:
	    return true;
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

	class ServerDB extends SQLiteOpenHelper {
		public ServerDB(Context context) { 
	    	super(context, "vbox.db", null, 2);  
	    }
	    @Override
	    public void onCreate(SQLiteDatabase db) { 
	    	db.execSQL("CREATE TABLE SERVERS (ID INTEGER PRIMARY KEY, HOST TEXT, PORT INTEGER, USERNAME TEXT, PASSWORD TEXT);");    
	    }
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS SERVERS");
	        onCreate(db);
	    }
	    public void insertOrUpdate(Server s) {
	    	ContentValues c = new ContentValues();
	    	c.put("HOST", s.getHost());
	    	c.put("PORT", s.getPort());
	    	c.put("USERNAME", s.getUsername());
	    	c.put("PASSWORD", s.getPassword());
	    	if(s.getId()==-1) {
				s.setId(getWritableDatabase().insert("SERVERS", null, c));
			} else {
				c.put("ID", s.getId());
				getWritableDatabase().update("SERVERS", c, "ID  =  ?", new String[] {s.getId().toString()} );
			}
	    }
	    public void delete(Long id) {
	    	getWritableDatabase().delete("SERVERS", "ID =  ?", new String[] {id.toString()} );
	    }
	    public List<Server> getServers() {
	    	Cursor c = getReadableDatabase().query("SERVERS", new String[] { "ID", "HOST", "PORT", "USERNAME", "PASSWORD" }, null, null, null, null, null);
	    	List<Server> ret = new ArrayList<Server>();
	    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
	    		ret.add(new Server(c.getLong(c.getColumnIndex("ID")),  c.getString(c.getColumnIndex("HOST")),c.getInt(c.getColumnIndex("PORT")),c.getString(c.getColumnIndex("USERNAME")),c.getString(c.getColumnIndex("PASSWORD"))));
	    	return ret;
	    }
	}
}
