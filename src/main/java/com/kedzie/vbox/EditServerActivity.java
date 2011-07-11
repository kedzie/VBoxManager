package com.kedzie.vbox;

import android.os.Bundle;


public class EditServerActivity extends BaseActivity {
	protected static final String TAG = EditServerActivity.class.getSimpleName();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        String host = getIntent().getStringExtra("host");
        String port = getIntent().getStringExtra("port");
    }
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}