package com.kedzie.vbox.harness;

import android.os.Bundle;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.BaseActivity;

/**
 * Initializes & launches arbitrary activity from main launcher
 * @author Marek Kedzierski
 */
public class PanelActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel_test);
	}
}
