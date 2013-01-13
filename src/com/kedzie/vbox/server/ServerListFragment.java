package com.kedzie.vbox.server;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * Show list of VirtualBox servers
 * @apiviz.stereotype fragment
 */
public class ServerListFragment extends SherlockFragment {
    static final int REQUEST_CODE_ADD = 0xF000;
    static final int REQUEST_CODE_EDIT = 0x0F00;
    static final int RESULT_CODE_SAVE = 0x00F0;
    static final int RESULT_CODE_DELETE = 0x000F;
    private static final String FIRST_RUN_PREFERENCE = "first_run";
    
    public static interface OnSelectServerListener {
        public void onSelectServer(Server server);
    }
    
    /**
     * Load Servers from DB
     */
    class LoadServersTask extends ActionBarTask<Void, List<Server>> {

        public LoadServersTask() {
            super("LoadServersTask", getSherlockActivity(),  null); 
        }
        @Override 
        protected List<Server> work(Void... params) throws Exception { 
            return _db.query(); 
        }
        @Override 
        protected void onResult(List<Server> result)    {
            _listView.setAdapter(new ServerListAdapter(getSherlockActivity(), result));
            if(result.isEmpty()) {
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.add_server_question)
                    .setMessage("")
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

        public ServerListAdapter(Context context, List<Server> servers) {
            super(context, 0, servers);
            _layoutInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = _layoutInflater.inflate(R.layout.simple_list_item, parent, false);
            Server s = getItem(position);
            ((TextView)view.findViewById(R.id.list_item_text)).setText(s.toString());
            return view;
        }
    }
    
    private OnSelectServerListener _listener;
    private ServerSQlite _db;
    private ListView _listView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnSelectServerListener) 
            _listener = (OnSelectServerListener)activity;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _listView = new ListView(getActivity());
        _listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _listView.setItemChecked(position, true);
                _listener.onSelectServer(getAdapter().getItem(position));
            }
        });
        registerForContextMenu(_listView);
        return _listView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(getActivity() instanceof ServerListFragmentActivity); //this is a hack
        _db = new ServerSQlite(getActivity());
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

    protected ServerListAdapter getAdapter() {
        return (ServerListAdapter)_listView.getAdapter();
    }
    
    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_list_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.server_list_option_menu_add:
                addServer();
                return true;
            }
        return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, R.id.server_list_context_menu_select, Menu.NONE, "Connect");
        menu.add(Menu.NONE, R.id.server_list_context_menu_edit, Menu.NONE, "Edit");
        menu.add(Menu.NONE, R.id.server_list_context_menu_delete, Menu.NONE, "Delete");
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
            startActivityForResult(new Intent(getActivity(), EditServerActivity.class).putExtra(EditServerActivity.INTENT_SERVER, s), REQUEST_CODE_EDIT);
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
        startActivityForResult(new Intent(getActivity(), EditServerActivity.class)
                .putExtra(EditServerActivity.INTENT_SERVER, new Server()), 
                REQUEST_CODE_ADD);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) 
            return;
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
                getAdapter().setNotifyOnChange(false);
                int pos = getAdapter().getPosition(s);
                getAdapter().remove(s);
                getAdapter().insert(s, pos);
                getAdapter().notifyDataSetChanged();
            } else if (requestCode==REQUEST_CODE_ADD) {
                _db.insert(s);
                getAdapter().add(s);
                checkIfFirstRun(s);
            }
            break;
        }
    }
}
