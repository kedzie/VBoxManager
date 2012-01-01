package com.kedzie.vbox.server;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.task.BaseTask;


public class ServerListActivity extends Activity implements AdapterView.OnItemClickListener {
	protected static final String TAG = ServerListActivity.class.getName();
	static final int REQUEST_CODE_ADD = 9303,REQUEST_CODE_EDIT = 9304, RESULT_CODE_SAVE = 1,RESULT_CODE_DELETE = 2;
	
	private ServerDB _db = new ServerDB(this);
	private ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);
        listView = (ListView)findViewById(R.id.list);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(this);
        new LoadServersTask(this).execute();
    }
	
	@Override protected void onDestroy() {
		super.onDestroy();
		_db.close();
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayAdapter<Server> getAdapter() {
		return (ArrayAdapter<Server>)listView.getAdapter();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		new LogonTask(this, null).execute(getAdapter().getItem(position));
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
	        startActivityForResult(new Intent(this, EditServerActivity.class).putExtra("server", new Server(-1L, "", "", 18083, "", "")), REQUEST_CODE_ADD);
	        return true;
	    default:
	        return true;
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.server_list_context_menu, menu);
		menu.setHeaderTitle("VirtualBox Webserver");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  Server s = getAdapter().getItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.server_list_context_menu_edit:
        startActivityForResult(new Intent(this, EditServerActivity.class).putExtra("server", s), REQUEST_CODE_EDIT);
        return true;
	  case R.id.server_list_context_menu_delete:
		  _db.delete(s.getId());
		  getAdapter().remove(s);
		  getAdapter().notifyDataSetChanged();
	    return true;
	  }
	  return true;
	}
	
 	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 		if(data == null) return;
 		Server s = data.getParcelableExtra("server");
        switch(resultCode) {
		case(RESULT_CODE_SAVE):
			_db.insertOrUpdate(s);
			if(requestCode==REQUEST_CODE_EDIT) {
				int pos = getAdapter().getPosition(s);
				getAdapter().remove(s);
				getAdapter().insert(s, pos);
			} else if (requestCode == REQUEST_CODE_ADD)
				getAdapter().add(s);
			break;
		case(RESULT_CODE_DELETE):
			_db.delete(s.getId());
			getAdapter().remove(s);
			break;
        }
        getAdapter().notifyDataSetChanged();
    }
 	
 	class ServerListAdapter extends ArrayAdapter<Server> {
		private final LayoutInflater _layoutInflater;
		
		public ServerListAdapter(Context context, List<Server> servers) {
			super(context, 0, servers);
			_layoutInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = _layoutInflater.inflate(R.layout.server_item, parent, false);
			Server s = getItem(position);
			((TextView)view.findViewById(R.id.server_item_text)).setText((s.getName()==null || "".equals(s.getName())) ? s.getHost() : s.getName());
			return view;
		}
	}
 	
 	/**
 	 * Load Servers from SQLLite
 	 */
 	class LoadServersTask extends BaseTask<Void, List<Server>>	{
 		public LoadServersTask(Context ctx) {
 			super("LoadServersTask", ctx,  null, "Loading Servers"); 
 		}
		@Override 
		protected List<Server> work(Void... params) throws Exception { 
			return _db.getServers(); 
		}
		@Override 
		protected void onPostExecute(List<Server> result)	{
			super.onPostExecute(result);
			if(result.size()==0) {
				result.add(new Server(new Long(-1), "kedzie-server", "192.168.1.10", 18083, "Marek", "Mk0204$$"));
		        result.add(new Server(new Long(-1), "kedzie-xps", "192.168.1.1", 18083, "kedzie", "Mk0204$$"));
		        result.add(new Server(new Long(-1), "kedzie-xps", "192.168.1.17", 18083, "kedzie", "Mk0204$$"));
			}
			listView.setAdapter( new ServerListAdapter(ServerListActivity.this, result) );
			getAdapter().setNotifyOnChange(false);
		}
	}
 	
 	/**
 	 * Connect & Logon to VirtualBox webservice
 	 */
 	class LogonTask extends BaseTask<Server, IVirtualBox>	{
 		public LogonTask(Context ctx, VBoxSvc vmgr) { 
			super( "LogonTask", ctx, vmgr, "Connecting");
		}
		@Override
		protected IVirtualBox work(Server... params) throws Exception {
			_vmgr = new VBoxSvc("http://"+params[0].getHost()+":"+params[0].getPort());
			return _vmgr.logon(params[0].getUsername(), params[0].getPassword());
		}
		protected void onPostExecute(IVirtualBox vbox) {
			super.onPostExecute(vbox);
			if(vbox!=null) {
				Toast.makeText(ServerListActivity.this, "Connected to VirtualBox v." + vbox.getVersion(), Toast.LENGTH_LONG).show();
				startActivity(new Intent(ServerListActivity.this, MachineListActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr));
			}
		}
	}

	/**
	 * Create SQLLite database
	 */
	class ServerDB extends SQLiteOpenHelper {
		public ServerDB(Context context) { 
	    	super(context, "vbox.db", null, 2);  
	    }
	    @Override
	    public void onCreate(SQLiteDatabase db) { 
	    	db.execSQL("CREATE TABLE SERVERS (ID INTEGER PRIMARY KEY, NAME TEXT, HOST TEXT, PORT INTEGER, USERNAME TEXT, PASSWORD TEXT);");    
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
	    	c.put("NAME", s.getName());
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
	    	Cursor c = getReadableDatabase().query("SERVERS", new String[] { "ID", "NAME", "HOST", "PORT", "USERNAME", "PASSWORD" }, null, null, null, null, null);
	    	List<Server> ret = new ArrayList<Server>();
	    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
	    		ret.add(new Server(
	    				c.getLong(c.getColumnIndex("ID")),  
	    				c.getString(c.getColumnIndex("NAME")), 
	    				c.getString(c.getColumnIndex("HOST")),
	    				c.getInt(c.getColumnIndex("PORT")),
	    				c.getString(c.getColumnIndex("USERNAME")),
	    				c.getString(c.getColumnIndex("PASSWORD"))));
	    	return ret;
	    }
	}
}
