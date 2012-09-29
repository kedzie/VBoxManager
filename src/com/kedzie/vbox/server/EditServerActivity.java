package com.kedzie.vbox.server;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;

public class EditServerActivity extends SherlockActivity {
	public static final String INTENT_SERVER = "server";
	
	protected Server _server;

	private TextView nameText;
	private TextView hostText;
	private CheckBox sslBox;
	private TextView portText;
	private TextView userText;
	private TextView passText;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setResult(ServerListActivity.RESULT_CANCELED);
		setContentView(R.layout.server);
		_server = (Server)(state==null ? getIntent().getParcelableExtra(INTENT_SERVER) : state.getParcelable(INTENT_SERVER));
		nameText = (TextView)findViewById(R.id.server_name);
		hostText = (TextView)findViewById(R.id.server_host);
		sslBox = (CheckBox)findViewById(R.id.server_ssl);
		portText = (TextView)findViewById(R.id.server_port);
		userText = (TextView)findViewById(R.id.server_username);
		passText = (TextView)findViewById(R.id.server_password);
		nameText.setText(_server.getName());
		hostText.setText(_server.getHost());
		sslBox.setChecked(_server.isSSL());
		portText.setText(""+_server.getPort());
		userText.setText(_server.getUsername());
		passText.setText(_server.getPassword());
		startActionMode(new ActionMode.Callback() {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.setTitle("Edit Server");
				mode.getMenuInflater().inflate(R.menu.server_actions, menu);
				return true;
			}
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch(item.getItemId()) {
				case R.id.server_list_option_menu_save:
					populateServer();
					getIntent().putExtra(INTENT_SERVER, _server);
					setResult(ServerListActivity.RESULT_CODE_SAVE, getIntent());
					finish();
					return true;
				case R.id.server_list_option_menu_delete:
					setResult(ServerListActivity.RESULT_CODE_DELETE, getIntent());
					finish();
					return true;
				default:
					return true;
				}
			}
			@Override
			public void onDestroyActionMode(ActionMode mode) {	
				setResult(ServerListActivity.RESULT_CANCELED, getIntent());
				finish();
			}
		});
	}
	
	private void populateServer() {
		_server.setName( nameText.getText().toString() );
		_server.setHost( hostText.getText().toString() );
		_server.setSSL(sslBox.isChecked());
		_server.setPort( Integer.parseInt( portText.getText().toString()) );
		_server.setUsername( userText.getText().toString() );
		_server.setPassword( passText.getText().toString() );
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		populateServer();
		outState.putParcelable(INTENT_SERVER, _server);
	}
}