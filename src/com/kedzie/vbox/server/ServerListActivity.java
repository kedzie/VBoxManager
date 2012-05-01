package com.kedzie.vbox.server;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;


public class ServerListActivity extends SherlockActivity implements AdapterView.OnItemClickListener {
	private static final String TAG = ServerListActivity.class.getName();
	static final int REQUEST_CODE_ADD = 9303,REQUEST_CODE_EDIT = 9304, RESULT_CODE_SAVE = 1,RESULT_CODE_DELETE = 2;
	private static final String FIRST_RUN_PREFERENCE = "first_run";
	
	private ServerDB _db = new ServerDB(this);
	private ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        listView = new ListView(this);
        setContentView(listView);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(this);
        
        new LoadServersTask().execute();
    }
	
	protected void checkIfFirstRun(Server s) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(!prefs.contains(FIRST_RUN_PREFERENCE)) {
			Log.i(TAG, "First execution detected");
			Editor editor = prefs.edit();
			editor.putBoolean(FIRST_RUN_PREFERENCE, false);
			editor.commit();
			new AlertDialog.Builder(this)
			.setTitle("Welcome")
			.setMessage(String.format("Make sure you virtualBox web service is running.  i.e. vboxwebsrv --host %s --port %d", s.getHost(), s.getPort()))
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.show();
		}
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
		new LogonTask().execute(getAdapter().getItem(position));
	}
	
		@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
			 getSupportMenuInflater().inflate(R.menu.server_list_options_menu, menu);
			    return true;
	}

	@Override
	public boolean onOptionsItemSelected( com.actionbarsherlock.view.MenuItem item) {
		 switch (item.getItemId()) {
		    case R.id.server_list_option_menu_add:
		        addServer();
		        return true;
		    default:
		        return true;
		    }
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.server_list_context_menu, menu);
		menu.setHeaderTitle("VirtualBox Server");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  int position = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
	Server s = getAdapter().getItem(position);
	  switch (item.getItemId()) {
	  case R.id.server_list_context_menu_select:
		  new LogonTask().execute(getAdapter().getItem(position));
	        return true;
	  case R.id.server_list_context_menu_edit:
        startActivityForResult(new Intent(this, EditServerActivity.class).putExtra(EditServerActivity.INTENT_SERVER, s), REQUEST_CODE_EDIT);
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
 		Server s = data.getParcelableExtra(EditServerActivity.INTENT_SERVER);
        switch(resultCode) {
		case(RESULT_CODE_SAVE):
			_db.insertOrUpdate(s);
			if(requestCode==REQUEST_CODE_EDIT) {
				int pos = getAdapter().getPosition(s);
				getAdapter().setNotifyOnChange(false);
				getAdapter().remove(s);
				getAdapter().insert(s, pos);
			} else if (requestCode == REQUEST_CODE_ADD) {
				getAdapter().add(s);
				checkIfFirstRun(s);
			}
			break;
		case(RESULT_CODE_DELETE):
			_db.delete(s.getId());
			getAdapter().remove(s);
			break;
        }
        getAdapter().notifyDataSetChanged();
    }
 	
 	/**
	 * Launch activity to create a new Server
	 */
	protected void addServer() {
		startActivityForResult(new Intent(ServerListActivity.this, EditServerActivity.class).putExtra(EditServerActivity.INTENT_SERVER, new Server(-1L, "", "", 18083, "", "")), REQUEST_CODE_ADD);
	}
 	
 	/**
 	 * Server list adapter
 	 */
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
			((TextView)view.findViewById(R.id.server_item_text)).setText(s.toString());
			return view;
		}
	}
 	
 	/**
 	 * Logs on and launches MachineList Activity
 	 */
 	class LogonTask extends BaseTask<Server, IVirtualBox> {
		
		public LogonTask() { 
			super( "LogonTask", ServerListActivity.this, null, "Connecting");
		}
		
		@Override
		protected IVirtualBox work(Server... params) throws Exception {
			_vmgr = new VBoxSvc("http://"+params[0].getHost()+":"+params[0].getPort());
			 _vmgr.logon(params[0].getUsername(), params[0].getPassword());
			 _vmgr.getVBox().getVersion();
			 return _vmgr.getVBox();
		}

		@Override protected void onPostExecute(IVirtualBox vbox) {
			if(vbox!=null) {
				Utils.toast(ServerListActivity.this, "Connected to VirtualBox v." + vbox.getVersion());
				startActivity(new Intent(ServerListActivity.this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr));
			}
			super.onPostExecute(vbox);
		}
	}
 	
 	/**
 	 * Load Servers from DB
 	 */
 	class LoadServersTask extends BaseTask<Void, List<Server>>	{
 		
 		public LoadServersTask() {
 			super("LoadServersTask", ServerListActivity.this,  null, "Loading Servers"); 
 		}
		@Override 
		protected List<Server> work(Void... params) throws Exception { 
			return _db.getServers(); 
		}
		@Override 
		protected void onPostExecute(List<Server> result)	{
			super.onPostExecute(result);
			listView.setAdapter( new ServerListAdapter(ServerListActivity.this, result) );
			if(result.isEmpty()) {
				new AlertDialog.Builder(ServerListActivity.this)
				.setTitle("Add new server")
				.setMessage("You have no VirtualBox servers defined.  Would you like to add one?")
				.setIcon(android.R.drawable.ic_menu_help)
				.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						addServer();
					}
				})
				.setCancelable(true)
				.show();
			}
		}
	}

	/**
	 * Table for VirtualBox Servers
	 */
	class ServerDB extends SQLiteOpenHelper {
		public ServerDB(Context context) { 
	    	super(context, "vbox.db", null, 2);  
	    }
	    @Override
	    public void onCreate(SQLiteDatabase db) { 
	    	Log.i("ServerSQL", "Creating database schema");
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
