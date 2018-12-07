package com.kedzie.vbox.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Show list of VirtualBox servers
 * @apiviz.stereotype fragment
 */
public class ServerListFragment extends Fragment {
    private static final String FIRST_RUN_PREFERENCE = "first_run";
    
    /**
     * Handle Server selection
     */
    public interface OnSelectServerListener {
    	
        /**
         * @param server	the selected {@link Server}
         */
        void onSelectServer(Server server);
    }
    
    /**
     * Load Servers from DB
     */
    class LoadServersTask extends ActionBarTask<Void, List<Server>> {

        public LoadServersTask() {
            super((AppCompatActivity)getActivity(),  null);
        }
        @Override 
        protected List<Server> work(Void... params) throws Exception { 
            return _db.query(); 
        }
        @Override 
        protected void onSuccess(List<Server> result)    {
            _listView.setAdapter(new ServerListAdapter((AppCompatActivity)getActivity(), result));
            if(result.isEmpty())
            	showAddNewServerPrompt();
            else 
            	checkIfFirstRun(result.get(0));
        }
    }

    /**
     * Server list adapter
     */
    class ServerListAdapter extends ArrayAdapter<Server> {

    	private LayoutInflater _inflater;
    	
        public ServerListAdapter(Context context, List<Server> servers) {
            super(context, 0, servers);
            _inflater = LayoutInflater.from(context);
        }
        
        @Override
        public boolean hasStableIds() {
        	return true;
        }
        
        @Override
        public long getItemId(int position) {
        	return getItem(position).getId();
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	if(convertView==null) {
            	convertView = _inflater.inflate(R.layout.simple_selectable_list_item, parent, false);
            	convertView.setTag((TextView)convertView.findViewById(android.R.id.text1));
            }
            TextView text1 = (TextView)convertView.getTag();
            text1.setText(getItem(position).toString());
            text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_vbox, 0, 0, 0);
            return convertView;
        }
    }
    
    private OnSelectServerListener _listener;
    private ServerSQlite _db;

    @BindView(R.id.list)
     ListView _listView;
    @BindView(R.id.addButton)
     FloatingActionButton _addButton;
    private boolean _dualPane;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof OnSelectServerListener) 
            _listener = (OnSelectServerListener)activity;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.server_list, container, false);
    }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    _dualPane = getActivity().findViewById(R.id.details)!=null;
    _listView.setChoiceMode(_dualPane ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(_dualPane)
          _listView.setSelection(position);
        _listener.onSelectServer(getAdapter().getItem(position));
      }
    });
    registerForContextMenu(_listView);
    _addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addServer();
      }
    });
  }

  @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        _db = new ServerSQlite(getActivity());
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	new LoadServersTask().execute();
    }
    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        _db.close();
    }

    protected void checkIfFirstRun(Server s) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(!prefs.contains(FIRST_RUN_PREFERENCE)) {
            Editor editor = prefs.edit();
            editor.putBoolean(FIRST_RUN_PREFERENCE, false);
            editor.commit();
            new AlertDialog.Builder(getActivity())
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
    
    protected void showAddNewServerPrompt() {
    	new AlertDialog.Builder(getActivity())
        .setTitle(R.string.add_server_title)
        .setMessage(R.string.add_server_question)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            		addServer();
            }
        })
        .show();
    }

    protected ServerListAdapter getAdapter() {
        return (ServerListAdapter)_listView.getAdapter();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	if(!_dualPane)
    		inflater.inflate(R.menu.server_list_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                Utils.startActivity(getActivity(), new Intent(getActivity(), HelpActivity.class));
                return true;
            }
        return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, R.id.server_list_context_menu_select, Menu.NONE, R.string.server_connect);
        menu.add(Menu.NONE, R.id.server_list_context_menu_edit, Menu.NONE, R.string.edit);
        menu.add(Menu.NONE, R.id.server_list_context_menu_delete, Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
    	int position = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
    	final Server s = getAdapter().getItem(position);
    	switch (item.getItemId()) {
    		case R.id.server_list_context_menu_select:
    			_listener.onSelectServer(getAdapter().getItem(position));
    			return true;
    		case R.id.server_list_context_menu_edit:
    			Utils.startActivity(getActivity(), new Intent(getActivity(), EditServerActivity.class).putExtra(EditServerActivity.INTENT_SERVER, (Parcelable)s));
    			return true;
    		case R.id.server_list_context_menu_delete:
    			_db.delete(s.getId());
    			getAdapter().remove(s);
    			getAdapter().notifyDataSetChanged();
    			return true;
    	}
    	return false;
    }

    /**
     * Launch activity to create a new Server
     */
    private void addServer() {
    		Utils.startActivity(getActivity(), new Intent(getActivity(), EditServerActivity.class).putExtra(EditServerActivity.INTENT_SERVER, (Parcelable)new Server()));
    }
}
