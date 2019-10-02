package com.kedzie.vbox.server;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.view.ActionMode;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.settings.ErrorSupport;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EditServerActivity extends BaseActivity {
	public static final String INTENT_SERVER = "server";

	protected Server mServer;

	private ServerSQlite mDb;

	@BindView(R.id.server_name)
	TextView nameText;
	@BindView(R.id.server_host)
	 TextView hostText;
	@BindView(R.id.server_port)
	 TextView portText;
	@BindView(R.id.server_username)
	 TextView userText;
	@BindView(R.id.server_password)
	 TextView passText;

	private Switch sslSwitch;
	private CheckBox sslBox;

	private Pattern validIpAddressRegex = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

	private Pattern validHostnameRegex = Pattern.compile("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");

	private ErrorSupport _errorSupport;

	@Override
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		ButterKnife.bind(this);
		mDb = new ServerSQlite(this);
		mServer = getIntent().getParcelableExtra(INTENT_SERVER);

		if(savedInstanceState!=null)
			mServer = savedInstanceState.getParcelable(INTENT_SERVER);

		if(Utils.isIceCreamSandwhich())
			sslSwitch = (Switch)findViewById(R.id.server_ssl);
		else
			sslBox = (CheckBox)findViewById(R.id.server_ssl);

		nameText.setText(mServer.getName());
		hostText.setText(mServer.getHost());

		if(Utils.isIceCreamSandwhich())
			sslSwitch.setChecked(mServer.isSSL());
		else
			sslBox.setChecked(mServer.isSSL());

		portText.setText(""+mServer.getPort());
		hostText.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					if (validIpAddressRegex.matcher(s).matches() ||
							validHostnameRegex.matcher(s).matches()) {
						_errorSupport.showError("host", "");
					} else {
						_errorSupport.showError("host", "Invalid host name or IP Address");
					}
				} catch(Throwable e) {
					Timber.w(e, "Error inet addresses");
					_errorSupport.showError("host", "");
				}
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

		userText.setText(mServer.getUsername());
		passText.setText(mServer.getPassword());

		_errorSupport = new ErrorSupport();
		_errorSupport.setTextView((TextView)findViewById(R.id.errors));

		startSupportActionMode(new ActionMode.Callback() {

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
							if(mServer.getId().equals(-1L))
								mDb.insert(mServer);
							else
								mDb.update(mServer);
							finish();
						} else {
							Utils.toastLong(EditServerActivity.this, "Fix errors first");
						}
						return true;
					case R.id.server_list_option_menu_delete:
						mDb.delete(mServer.getId());
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
		mServer.setName( nameText.getText().toString() );
		mServer.setHost( hostText.getText().toString() );
		mServer.setSSL(Utils.isIceCreamSandwhich() ? sslSwitch.isChecked() : sslBox.isChecked());
		try {
			mServer.setPort( Integer.parseInt( portText.getText().toString().trim()) );
		} catch(NumberFormatException e) {}
		mServer.setUsername( userText.getText().toString() );
		mServer.setPassword( passText.getText().toString() );
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		populateServer();
		outState.putParcelable(INTENT_SERVER, mServer);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	@Override
	public void finish() {
		super.finish();
		Utils.overrideBackTransition(this);
	}
}
