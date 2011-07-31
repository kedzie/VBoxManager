package com.kedzie.vbox.server;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kedzie.vbox.R;

public class EditServerActivity extends BaseActivity {
	protected static final String TAG = EditServerActivity.class.getSimpleName();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
        ((TextView)findViewById(R.id.server_host)).setText(getIntent().getStringExtra("host"));
        ((TextView)findViewById(R.id.server_port)).setText(""+getIntent().getIntExtra("port",18083));
        ((TextView)findViewById(R.id.server_username)).setText(getIntent().getStringExtra("username"));
        ((TextView)findViewById(R.id.server_password)).setText(getIntent().getStringExtra("password"));
        ((ImageButton)findViewById(R.id.button_save)).setOnClickListener( new OnClickListener() { @Override public void onClick(View v) { save(); } });
        ((ImageButton)findViewById(R.id.button_delete)).setOnClickListener( new OnClickListener() { @Override public void onClick(View v) { delete(); } });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.server_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.server_list_option_menu_save:
			save();
			return true;
		case R.id.server_list_option_menu_delete:
			delete();
			return true;
		default: 
			return true;
		}
	}
	
	private void save() {
		long id = getIntent().getLongExtra("id", -1);
		String host =  ((TextView)findViewById(R.id.server_host)).getText().toString();
		int port = Integer.parseInt( ((TextView)findViewById(R.id.server_port)).getText().toString());
		String username = ((TextView)findViewById(R.id.server_username)).getText().toString();
		String password = ((TextView)findViewById(R.id.server_password)).getText().toString();
		getVBoxApplication().getDB().insertOrUpdate(new Server(id, host, port, username, password));
		finish();
	}
	
	private void delete() {
		getVBoxApplication().getDB().delete(getIntent().getLongExtra("id", -1));
		finish();
	}
}