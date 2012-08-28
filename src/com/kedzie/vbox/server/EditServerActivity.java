package com.kedzie.vbox.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.kedzie.vbox.R;

public class EditServerActivity extends SherlockActivity {
	public static final String INTENT_SERVER = "server";

	protected Server _server;

	private TextView nameText;
	private TextView hostText;
	private TextView portText;
	private TextView userText;
	private TextView passText;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.server);
		_server = (Server)(state==null ? 
				getIntent().getParcelableExtra(INTENT_SERVER) 
				: state.getParcelable(INTENT_SERVER));
		nameText = (TextView)findViewById(R.id.server_name);
		hostText = (TextView)findViewById(R.id.server_host);
		portText = (TextView)findViewById(R.id.server_port);
		userText = (TextView)findViewById(R.id.server_username);
		passText = (TextView)findViewById(R.id.server_password);
		nameText.setText(_server.getName());
		hostText.setText(_server.getHost());
		portText.setText(""+_server.getPort());
		userText.setText(_server.getUsername());
		passText.setText(_server.getPassword());
	}
	
	private void populateServer() {
		_server.setName( nameText.getText().toString() );
		_server.setHost( hostText.getText().toString() );
		_server.setPort( Integer.parseInt( portText.getText().toString()) );
		_server.setUsername( userText.getText().toString() );
		_server.setPassword( passText.getText().toString() );
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		populateServer();
		outState.putParcelable(INTENT_SERVER, _server);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.server_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
		case R.id.server_list_option_menu_save:
			save();
			return true;
		case R.id.server_list_option_menu_delete:
			delete();
			return true;
		case android.R.id.home:
			setResult(ServerListActivity.RESULT_CANCELED);
			NavUtils.navigateUpTo(this, new Intent(this, ServerListActivity.class));
			return true;
		default: 
			return true;
		}
	}

	@Override 
	public void onBackPressed() {
		setResult(ServerListActivity.RESULT_CANCELED);
		super.onBackPressed();
	}

	private void save() {
		populateServer();
		getIntent().putExtra(INTENT_SERVER, _server);
		setResult(ServerListActivity.RESULT_CODE_SAVE, getIntent());
		finish();
	}

	private void delete() {
		setResult(ServerListActivity.RESULT_CODE_DELETE, getIntent());
		finish();
	}
}