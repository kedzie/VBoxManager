package com.kedzie.vbox.server;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kedzie.vbox.R;

public class EditServerActivity extends Activity {
	protected static final String TAG = EditServerActivity.class.getSimpleName();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		Server s = getIntent().getParcelableExtra("server");
        ((TextView)findViewById(R.id.server_host)).setText(s.getHost());
        ((TextView)findViewById(R.id.server_port)).setText(""+s.getPort());
        ((TextView)findViewById(R.id.server_username)).setText(s.getUsername());
        ((TextView)findViewById(R.id.server_password)).setText(s.getPassword());
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
		Server s  = getIntent().getParcelableExtra("server");
		s.setHost( ((TextView)findViewById(R.id.server_host)).getText().toString() );
		s.setPort( Integer.parseInt( ((TextView)findViewById(R.id.server_port)).getText().toString()) );
		s.setUsername( ((TextView)findViewById(R.id.server_username)).getText().toString() );
		s.setPassword(((TextView)findViewById(R.id.server_password)).getText().toString() );
		getIntent().putExtra("server", s);
		setResult(ServerListActivity.RESULT_CODE_SAVE, getIntent());
		finish();
	}
	
	private void delete() {
		setResult(ServerListActivity.RESULT_CODE_DELETE, getIntent());
		finish();
	}
}