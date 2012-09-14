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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.DialogTask;

public class ServerListActivity extends SherlockFragmentActivity {
	private static final String TAG = ServerListActivity.class.getName();
	static final int REQUEST_CODE_ADD = 0xF000, REQUEST_CODE_EDIT = 0x0F00, RESULT_CODE_SAVE = 0x00F0, RESULT_CODE_DELETE = 0x000F;
	private static final String FIRST_RUN_PREFERENCE = "first_run";

	private ServerDB _db;
	private ListView listView;
	
	/**
	 * Log on to VirtualBox webservice
	 */
	class LogonTask extends DialogTask<Server, IVirtualBox> {
		public LogonTask() { 
			super( "LogonTask", ServerListActivity.this, null, "Connecting");
		}

		@Override
		protected IVirtualBox work(Server... params) throws Exception {
			_vmgr =  new VBoxSvc("http://"+params[0].getHost()+":"+params[0].getPort());
			_vmgr.logon(params[0].getUsername(), params[0].getPassword());
			_vmgr.getVBox().getVersion();
			return _vmgr.getVBox();
		}

		@Override 
		protected void onResult(IVirtualBox vbox) {
			Utils.toastLong(ServerListActivity.this, "Connected to VirtualBox v." + vbox.getVersion());
			startActivity(new Intent(ServerListActivity.this, MachineListFragmentActivity.class)
						.putExtra(VBoxSvc.BUNDLE, _vmgr)
						.putExtra(MachineListFragmentActivity.INTENT_VERSION, vbox.getVersion()));
		}
	}
	
	/**
	 * Load Servers from DB
	 */
	class LoadServersTask extends ActionBarTask<Void, List<Server>>	{

		public LoadServersTask() {
			super("LoadServersTask", ServerListActivity.this,  null); 
		}
		@Override 
		protected List<Server> work(Void... params) throws Exception { 
			return _db.query(); 
		}
		@Override 
		protected void onResult(List<Server> result)	{
			getAdapter().clear();
			getAdapter().addAll(result);
			if(result.isEmpty()) {
				new AlertDialog.Builder(ServerListActivity.this)
					.setTitle("Add new VirtualBox server?")
					.setMessage("You have no servers defined.  Would you like to add one?")
					.setIcon(android.R.drawable.ic_dialog_info)
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
	 * Server list adapter
	 */
	class ServerListAdapter extends ArrayAdapter<Server> {
		private final LayoutInflater _layoutInflater;

		public ServerListAdapter(Context context) {
			super(context, 0, new ArrayList<Server>());
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
			Log.w(TAG, "DB upgrade [" + oldVersion + "-->" + newVersion + "], destroying data");
			db.execSQL("DROP TABLE IF EXISTS SERVERS");
			onCreate(db);
		}
		public List<Server> query() {
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
		public void insert(Server s) {
			ContentValues c = new ContentValues();
			c.put("NAME", s.getName());
			c.put("HOST", s.getHost());
			c.put("PORT", s.getPort());
			c.put("USERNAME", s.getUsername());
			c.put("PASSWORD", s.getPassword());
			s.setId(getWritableDatabase().insert( "SERVERS", null, c));
		}
		public void update(Server s) {
			ContentValues c = new ContentValues();
			c.put("ID", s.getId());
			c.put("NAME", s.getName());
			c.put("HOST", s.getHost());
			c.put("PORT", s.getPort());
			c.put("USERNAME", s.getUsername());
			c.put("PASSWORD", s.getPassword());
			getWritableDatabase().update( "SERVERS", c, "ID  =  ?", new String[] { s.getId().toString() } );
		}
		public void delete(Long id) {
			getWritableDatabase().delete( "SERVERS", "ID =  ?", new String[] { id.toString() } );
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setHomeButtonEnabled(false);
		_db = new ServerDB(this);
		listView = new ListView(this);
		listView.setAdapter( new ServerListAdapter(ServerListActivity.this) );
		setContentView(listView);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				connect(getAdapter().getItem(position));
			}
		});
		new LoadServersTask().execute();
	}

	protected void checkIfFirstRun(Server s) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(!prefs.contains(FIRST_RUN_PREFERENCE)) {
			Editor editor = prefs.edit();
			editor.putBoolean(FIRST_RUN_PREFERENCE, false);
			editor.commit();
			new AlertDialog.Builder(this)
					.setTitle(R.string.firstrun_welcome)
					.setMessage(getString(R.string.firstrun_message, s.getHost(), s.getPort()))
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

	protected ServerListAdapter getAdapter() {
		return (ServerListAdapter)listView.getAdapter();
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.server_list_actions, menu);
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
		getMenuInflater().inflate(R.menu.server_list_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		Server s = getAdapter().getItem(position);
		switch (item.getItemId()) {
		case R.id.server_list_context_menu_select:
			connect(getAdapter().getItem(position));
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
	
	/**
	 * Launch activity to create a new Server
	 */
	private void addServer() {
		startActivityForResult(new Intent(ServerListActivity.this, EditServerActivity.class).putExtra(EditServerActivity.INTENT_SERVER, new Server(-1L, "", "", 18083, "", "")), REQUEST_CODE_ADD);
	}
	
	/**
	 * Connect to server and launch application
	 * @param server VirtualBox {@link Server} to connect to
	 */
	private void connect(Server server) {
		new LogonTask().execute(server);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null) return;
		Server s = data.getParcelableExtra(EditServerActivity.INTENT_SERVER);
		switch(resultCode) {
		case RESULT_CODE_DELETE:
			if(requestCode==REQUEST_CODE_EDIT) {
				_db.delete(s.getId());
				getAdapter().remove(s);
			}
			break;
		case RESULT_CODE_SAVE:
			if(requestCode==REQUEST_CODE_EDIT) {
				_db.update(s);
				int pos = getAdapter().getPosition(s);
				getAdapter().setNotifyOnChange(false);
				getAdapter().remove(s);
				getAdapter().insert(s, pos);
			} else if (requestCode==REQUEST_CODE_ADD) {
				_db.insert(s);
				getAdapter().add(s);
				checkIfFirstRun(s);
			}
			break;
		}
		getAdapter().notifyDataSetChanged();
	}
}
