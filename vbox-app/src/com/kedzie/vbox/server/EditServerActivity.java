package com.kedzie.vbox.server;

import org.apache.commons.validator.routines.InetAddressValidator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.settings.ErrorSupport;

public class EditServerActivity extends SherlockActivity {
	public static final String INTENT_SERVER = "server";
	
	protected Server _server;
	private ServerSQlite _db;

	private TextView nameText;
	private TextView hostText;
	private Switch sslSwitch;
	private CheckBox sslBox;
	private TextView portText;
	private TextView userText;
	private TextView passText;

	private ErrorSupport _errorSupport;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		_db = new ServerSQlite(this);
		_server = (Server)(savedInstanceState==null ? getIntent().getParcelableExtra(INTENT_SERVER) : savedInstanceState.getParcelable(INTENT_SERVER));
		nameText = (TextView)findViewById(R.id.server_name);
		hostText = (TextView)findViewById(R.id.server_host);
		if(Utils.isIceCreamSandwhich())
			sslSwitch = (Switch)findViewById(R.id.server_ssl);
		else
			sslBox = (CheckBox)findViewById(R.id.server_ssl);
		portText = (TextView)findViewById(R.id.server_port);
		userText = (TextView)findViewById(R.id.server_username);
		passText = (TextView)findViewById(R.id.server_password);
		nameText.setText(_server.getName());
		hostText.setText(_server.getHost());
		if(Utils.isIceCreamSandwhich())
			sslSwitch.setChecked(_server.isSSL());
		else
			sslBox.setChecked(_server.isSSL());
		portText.setText(""+_server.getPort());
		hostText.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
			@Override 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(InetAddressValidator.getInstance().isValid(s.toString()))
					_errorSupport.showError("host", "");
				else
					_errorSupport.showError("host", "Invalid host name or IP address");
			}
		});
		portText.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					Integer.parseInt(s.toString());
					_errorSupport.showError("port", "");
				} catch(NumberFormatException e) {
					_errorSupport.showError("port", "Port # must be positive numeric value");
				}
			}
		});
		
		userText.setText(_server.getUsername());
		passText.setText(_server.getPassword());
		
		_errorSupport = new ErrorSupport();
		_errorSupport.setTextView((TextView)findViewById(R.id.errors));
		
		startActionMode(new ActionMode.Callback() {
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.setTitle(R.string.edit_server_actionmode_title);
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
					if(!_errorSupport.hasErrors()) {
						populateServer();
						if(_server.getId().equals(-1L))
							_db.insert(_server);
						else
							_db.update(_server);
						finish();
					} else {
						Utils.toastLong(EditServerActivity.this, "Fix errors first");
					}
					return true;
				case R.id.server_list_option_menu_delete:
					_db.delete(_server.getId());
					finish();
					return true;
				default:
					return true;
				}
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {	
				setResult(RESULT_CANCELED, new Intent());
				finish();
			}
		});
	}
	
	private void populateServer() {
		_server.setName( nameText.getText().toString() );
		_server.setHost( hostText.getText().toString() );
		_server.setSSL(Utils.isIceCreamSandwhich() ? sslSwitch.isChecked() : sslBox.isChecked());
		try {
		_server.setPort( Integer.parseInt( portText.getText().toString().trim()) );
		} catch(NumberFormatException e) {}
		_server.setUsername( userText.getText().toString() );
		_server.setPassword( passText.getText().toString() );
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		populateServer();
		outState.putParcelable(INTENT_SERVER, _server);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_db.close();
	}

	@Override
	public void finish() {
	    super.finish();
	    Utils.overrideBackTransition(this);
	}
}